package com.pineapple.app.ui.state

/**
 * Object to reflect a state of the reddit authentication process
 */
sealed class AuthViewState {
    object Idle : AuthViewState()
    object Loading : AuthViewState()
    object Success : AuthViewState()
    data class Error(val message: String) : AuthViewState()
}