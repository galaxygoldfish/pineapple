package com.pineapple.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.cache.PostWithUser
import com.pineapple.app.network.model.cache.CommentWithUser
import com.pineapple.app.network.paging.PagingRepository
import com.pineapple.app.network.repository.RedditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: RedditRepository,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    private val usernameFlow = MutableStateFlow<String?>(null)

    val posts: Flow<PagingData<PostWithUser>> = usernameFlow.flatMapLatest { name ->
        if (name == null) emptyFlow()
        else pagingRepository.userPostsPager(name).flow
    }.cachedIn(viewModelScope)

    val comments: Flow<PagingData<CommentWithUser>> = usernameFlow.flatMapLatest { name ->
        if (name == null) emptyFlow()
        else pagingRepository.userCommentsPager(name).flow
    }.cachedIn(viewModelScope)

    var isLoading by mutableStateOf(false)

    fun loadUser(username: String) {
        usernameFlow.value = username
        viewModelScope.launch {
            repository.observeUser(username).collectLatest {
                _user.value = it
            }
        }
        
        viewModelScope.launch {
            isLoading = true
            try {
                repository.fetchAndCacheUser(username)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updatePostFavorite(saved: Boolean, postId: String) {
        viewModelScope.launch {
            if (saved) {
                repository.savePost(postId.removePrefix("t3_"))
            } else {
                repository.unsavePost(postId.removePrefix("t3_"))
            }
        }
    }

    fun updatePostVote(postId: String, direction: Int) {
        viewModelScope.launch {
            repository.castVoteAndCache(postId.removePrefix("t3_"), direction)
        }
    }
}
