package com.pineapple.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pineapple.app.R
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.network.model.cache.PostWithUser
import com.pineapple.app.network.paging.PagingRepository
import com.pineapple.app.network.repository.RedditRepository
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collectLatest
import java.util.Collections
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RedditRepository,
    private val pagingRepository: PagingRepository,
    private val mmkv: MMKV
) : ViewModel() {

    val postState = MutableStateFlow<PostWithUser?>(null)
    val isLoading = MutableStateFlow(true)

    // replyCounts maps comment full-id (e.g. "t1_abc") to cached count (or -1 if unknown)
    private val _replyCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val replyCounts: StateFlow<Map<String, Int>> = _replyCounts

    /** Observe replies for a parent comment as stored in Room (commentDao) */
    fun observeRepliesForComment(parentCommentFullId: String): Flow<List<CommentWithUser>> {
        return repository.observeRepliesForComment(parentCommentFullId)
    }

    /**
     * Load replies for a specific comment: request network fetch which will update Room
     * and return the number of replies parsed; UI can observe `observeRepliesForComment` for inserted replies.
     */
    fun loadRepliesForComment(postId: String, commentIdNoPrefix: String) {
        val full = if (commentIdNoPrefix.startsWith("t1_")) commentIdNoPrefix else "t1_$commentIdNoPrefix"
        viewModelScope.launch {
            try {
                // refreshRepliesForComment returns the number of replies parsed and inserted
                val parsed = repository.refreshRepliesForComment(postId, commentIdNoPrefix)
                if (parsed >= 0) {
                    _replyCounts.value = _replyCounts.value + (full to parsed)
                }
            } catch (_: Throwable) {
                // swallow minimal
            }
        }
    }

    var showingMoreSheet by mutableStateOf(false)

    var showingCommentReplySheet by mutableStateOf(false)
    var commentToShowReplySheet by mutableStateOf<CommentWithUser?>(null)

    var showingCommentMoreSheet by mutableStateOf(false)
    var commentToShowMoreSheet by mutableStateOf<CommentWithUser?>(null)

    val isUserless by lazy { mmkv.getBoolean(MMKVKey.USER_GUEST, true) }

    // Comments exposed as a StateFlow of PagingData so UI can `collectAsState()` easily
    private val _comments = MutableStateFlow<PagingData<CommentWithUser>>(PagingData.empty())
    val comments = _comments
    private var commentsJob: Job? = null

    // Deduplicate in-flight user fetches to avoid duplicate network calls during fast scrolls
    private val fetchingUsers = Collections.synchronizedSet(mutableSetOf<String>())

    val snackbarState = SnackbarHostState()

    fun observePost(postId: String) {
        viewModelScope.launch {
            repository.observePostWithUser(postId)
                .collect { postWithUser ->
                    postState.value = postWithUser
                    if (postWithUser != null) {
                        isLoading.value = false
                    }
                }
        }
    }

    fun refresh(postId: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.refreshPostAndAuthor(postId)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadPost(postId: String) {
        // Called from LaunchedEffect(postID)
        observePost(postId)
        refresh(postId)
        // start collecting paged comments for this post so UI can observe `comments`
        startCommentsForPost(postId)
    }

    // Start collecting comments PagingData into a StateFlow (cancels previous collection)
    fun startCommentsForPost(postId: String) {
        commentsJob?.cancel()
        commentsJob = viewModelScope.launch {
            pagingRepository.commentsPager(postId).flow
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _comments.value = pagingData
                }
        }
    }

    // Trigger a refresh to fetch comments and cache them
    fun refreshComments(postId: String) {
        viewModelScope.launch {
            repository.refreshCommentsForPost(postId)
        }
    }

    /**
     * Public helper: called by the UI when a comment row becomes visible.
     * Dedupes in-flight requests by username and launches an IO coroutine to fetch and cache the user.
     */
    fun fetchUserOnVisible(username: String?) {
        if (username.isNullOrBlank()) return
        val first = fetchingUsers.add(username)
        if (!first) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.fetchAndCacheUser(username)
            } catch (_: Throwable) {
                // swallow - minimal behavior (no logging)
            } finally {
                fetchingUsers.remove(username)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        commentsJob?.cancel()
    }

    /**
     * Open a snackbar notifying the user that the action they are trying to perform requires
     * authentication, with a button allowing them to log in if they want to
     */
    fun encourageUserAuthSnackbar() {
        viewModelScope.launch {
            val result = snackbarState.showSnackbar(
                message = context.getString(R.string.home_snackbar_auth_text),
                actionLabel = context.getString(R.string.home_snackbar_auth_login),
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                launchRedditAuthFlow(context)
            }
        }
    }

    /**
     * Open the reddit authentication flow in the default browser, which will return to the app
     * via a deep link once completed, containing the code in a query parameter. (handled in NavHost)
     */
    fun launchRedditAuthFlow(context: Context) {
        Intent(Intent.ACTION_VIEW).apply {
            data = ("https://www.reddit.com/api/v1/authorize.compact" +
                    "?client_id=${mmkv.decodeString(MMKVKey.CLIENT_ID)}" +
                    "&response_type=code" +
                    "&state=${UUID.randomUUID()}" +
                    "&redirect_uri=pineapple://login" +
                    "&duration=permanent" +
                    "&scope=identity edit flair history modconfig modflair modlog " +
                    "modposts, modwiki mysubreddits privatemessages read report save " +
                    "submit subscribe vote wikiedit wikiread"
                    ).toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }

    /**
     * Post an update to the reddit API that reflects whether a post/comment is up/downvoted
     * Also update local cache via repository so UI reacts immediately
     * @param direction: 1 for upvote, -1 for downvote, 0 for no vote or to remove vote
     * @param postId: The ID of the post to update (without prefix)
     */
    fun updateVote(direction: Int, postId: String) {
        viewModelScope.launch {
            repository.castVoteAndCache(postId, direction, prefix = "")
        }
    }

}