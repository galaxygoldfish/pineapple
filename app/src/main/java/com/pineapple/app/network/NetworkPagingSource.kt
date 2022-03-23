package com.pineapple.app.network

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.model.reddit.PostListing
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class RedditPageStore(val before: String?, val after: String?)

class NetworkPagingSource(
    private val service: NetworkService,
    private val subreddit: String,
    private val sort: String
) : PagingSource<String, PostItem>() {

    private val keys: MutableMap<Int, RedditPageStore> = mutableMapOf()

    @OptIn(ExperimentalPagingApi::class)
    override fun getRefreshKey(state: PagingState<String, PostItem>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            keys[anchorPosition]?.before ?: keys[anchorPosition]?.after
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostItem> {
        val completableDeferred = CompletableDeferred<LoadResult<String, PostItem>>()
        try {
            Log.e("0", "starting request")
            service.fetchSubreddit(name = subreddit, sort = sort, after = params.key)
                .enqueue(object : Callback<PostListing> {
                    override fun onResponse(call: Call<PostListing>, response: Response<PostListing>) {
                        Log.e("0", "response received")
                        val nextKey = response.body()?.data?.after
                        val prevKey = response.body()?.data?.before
                        keys[keys.size] = RedditPageStore(prevKey, nextKey)
                        Log.e("0", "keys found: $prevKey and $nextKey")
                        completableDeferred.complete(
                            if (response.body()?.data?.children?.isNotEmpty() == true) {
                                Log.e("0", "children aren't empty")
                                LoadResult.Page(
                                    data = response.body()?.data!!.children,
                                    nextKey = nextKey,
                                    prevKey = prevKey
                                )
                            } else {
                                Log.e("0", "children are empty lol")
                                LoadResult.Page(listOf(), null, null) // ?
                            }
                        )
                    }
                    override fun onFailure(call: Call<PostListing>, t: Throwable) {
                        Log.e("0", "request failure, throwable ${t.message}")
                        completableDeferred.complete(LoadResult.Error(t))
                    }
                })
        } catch (exception: Exception) {
            Log.e("0", "exception occurred, $exception")
            completableDeferred.complete(LoadResult.Error(exception))
        }
        return completableDeferred.await()
    }

}