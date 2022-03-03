package com.pineapple.app.network

class NetworkServiceHelper(private val networkService: NetworkService) {

    suspend fun fetchSubreddit(name: String, sort: String)
        = networkService.fetchSubreddit(name, sort)

}