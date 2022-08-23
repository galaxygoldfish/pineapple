package com.pineapple.app.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.network.RedditNetworkProvider

data class RedditPageStore(val before: String?, val after: String?)

class NetworkPagingSource(
    private val service: RedditNetworkProvider,
    private val subreddit: String,
    private val sort: String,
    private val time: String
) : PagingSource<String, PostItem>() {

    private val keys: MutableMap<Int, RedditPageStore> = mutableMapOf()

    override fun getRefreshKey(state: PagingState<String, PostItem>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            keys[anchorPosition]?.before ?: keys[anchorPosition]?.after
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostItem> {
        val response = service.fetchSubreddit(name = subreddit, sort = sort, time = time, after = params.key)
        val nextKey = response.data.after
        val prevKey = response.data.before
        keys[keys.size] = RedditPageStore(prevKey, nextKey)
        Log.e("0", "keys found: $prevKey and $nextKey")
        return if (response.data.children.isNotEmpty()) {
            Log.e("0", "children aren't empty")
            LoadResult.Page(
                data = response.data.children,
                nextKey = nextKey,
                prevKey = prevKey
            )
        } else {
            Log.e("0", "children are empty lol")
            LoadResult.Page(listOf(), null, null)
        }
    }

}