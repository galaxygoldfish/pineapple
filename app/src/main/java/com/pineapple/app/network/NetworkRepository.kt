package com.pineapple.app.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pineapple.app.model.PostItem
import kotlinx.coroutines.flow.Flow

class NetworkRepository(private val service: NetworkService) {

    fun fetchSubreddit(name: String, sort: String) : Flow<PagingData<PostItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NetworkPagingSource(service, name, sort)
            }
        ).flow
    }

}