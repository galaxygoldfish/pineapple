package com.pineapple.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.repository.RedditAuthRepository
import com.pineapple.app.network.repository.RedditRepository
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: RedditRepository,
    private val authRepository: RedditAuthRepository,
    private val mmkv: MMKV
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    var isLoading by mutableStateOf(false)
        private set

    val isUserless: Boolean get() = authRepository.isUserless

    init {
        refresh()
    }

    fun refresh() {
        if (isUserless) return
        viewModelScope.launch {
            isLoading = true
            val username = repository.fetchAndCacheMe()
                ?: mmkv.decodeString(MMKVKey.CURRENT_USERNAME)
            isLoading = false
            if (username != null) {
                mmkv.encode(MMKVKey.CURRENT_USERNAME, username)
                repository.observeUser(username).collectLatest { _user.value = it }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _user.value = null
    }
}
