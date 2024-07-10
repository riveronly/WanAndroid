package com.riveronly.wanandroid.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.riveronly.wanandroid.bean.ArticleListBean
import com.riveronly.wanandroid.net.ApiService

class HomePagingSource : PagingSource<Int, ArticleListBean.Data>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleListBean.Data> {
        try {
            // 从网络请求或其他地方获取数据
            val nextPageNumber = params.key ?: 0
            val data = ApiService.articleList(nextPageNumber).data?.datas ?: emptyList()

            // 返回加载结果
            return LoadResult.Page(
                data = data,
                prevKey = if (nextPageNumber == 0) null else nextPageNumber - 1,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            // 返回加载失败的结果
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleListBean.Data>): Int? {
        return null
    }
}