@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pineapple.app.ui.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.consts.PostFilterSort
import com.pineapple.app.consts.PostFilterTime
import com.pineapple.app.network.paging.PagingRepository
import com.pineapple.app.network.repository.RedditRepository
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val postRepository: PagingRepository,
    private val mmkv: MMKV,
    private val redditRepository: RedditRepository
) : ViewModel() {

    val isUserless by lazy { mmkv.getBoolean(MMKVKey.USER_GUEST, true) }

    var currentFilterTime by mutableStateOf(PostFilterTime.TIME_DAY)
    var currentFilterSort by mutableStateOf(PostFilterSort.SORT_HOT)

    var shouldScrollToTopAfterRefresh by mutableStateOf(false)

    val postListState = LazyListState()

    fun updateFilters(sort: String, time: String) {
        if (sort != currentFilterSort || time != currentFilterTime) {
            currentFilterSort = sort
            currentFilterTime = time
            shouldScrollToTopAfterRefresh = true
        }
    }

    val pagedPosts = snapshotFlow { currentFilterSort to currentFilterTime }
        .flatMapLatest { (sort, time) ->
            postRepository
                .postsPager(
                    subreddit = "all",
                    sort = sort,
                    time = time
                ).flow
        }
        .cachedIn(viewModelScope)

    /**
     * Post an update to the reddit API that reflects the bookmark state of a post
     * Also update local cache via repository so UI reacts immediately
     * @param save: True if we save the post, false otherwise
     * @param postID: The ID of the post to update (without prefix)
     */
    fun updatePostFavorite(save: Boolean, postID: String) {
        viewModelScope.launch {
            if (save) {
                redditRepository.savePost(postID)
            } else {
                redditRepository.unsavePost(postID)
            }
        }
    }

    /**
     * Post an update to the reddit API that reflects whether a post is up/downvoted
     * Also update local cache via repository so UI reacts immediately
     * @param direction: 1 for upvote, -1 for downvote, 0 for no vote or to remove vote
     * @param postId: The ID of the post to update (without prefix)
     */
    fun updatePostVote(direction: Int, postId: String) {
        viewModelScope.launch {
            redditRepository.castVoteAndCache(postId, direction)
        }
    }

}