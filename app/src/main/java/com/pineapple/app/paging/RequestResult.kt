package com.pineapple.app.paging

sealed class RequestResult<out T>(val status: RequestStatus, val data: T?, val message: String?) {

    data class Success<out R>(val result: R) : RequestResult<R>(
        data = result,
        status = RequestStatus.SUCCESS,
        message = null
    )

    data class Error(var exception: String) : RequestResult<Nothing>(
        data = null,
        status = RequestStatus.ERROR,
        message = exception
    )

    data class Loading<out R>(val isLoading: Boolean) : RequestResult<R>(
        data = null,
        status = RequestStatus.LOADING,
        message = null
    )

}
