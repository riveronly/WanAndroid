package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import com.riveronly.wanandroid.bean.UserArticleBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.ui.activity.screen.ARTICLE_BEAN
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.modal.toast
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun PlazaScreen() {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val userArticleListBean = remember { mutableStateOf(UserArticleBean()) }

    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    suspend fun fetchApi() {
        //广场帖子列表
        val userArticleList = ApiService.userArticleList(0)
        if (userArticleList.errorCode == 0 && userArticleList.data != null) {
            userArticleListBean.value = userArticleList.data
        } else {
            view.toast(userArticleList.errorMsg)
        }
    }

    LaunchedEffect(Unit) {
        fetchApi()
    }
    if (userArticleListBean.value.datas.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    scope.launch {
                        fetchApi()
                    }
                }
        ) {
            Text("点击重试")
        }
    } else {
        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
            scope.launch {
                isRefreshing = true
                fetchApi()
                isRefreshing = false
            }
        }) {
            LazyColumn(
                state = listState, modifier = Modifier.fillMaxSize()
            ) {
                items(items = userArticleListBean.value.datas) { item ->
                    ListItem(
                        modifier = Modifier.clickable {
                            val intent = Intent(view.context, ScreenActivity::class.java)
                            intent.putExtra(SCREEN_NAME, Screens.ArticleWebView.route)
                            intent.putExtra(ARTICLE_BEAN, Json.encodeToString(item))
                            startActivityLauncher.launch(intent)
                        },
                        headlineContent = {
                            Text(text = item.title)
                        },
                        trailingContent = {
                            Text(text = item.niceShareDate)
                        },
                        supportingContent = {
                            Text(text = item.shareUser)
                        }
                    )
                }
            }
        }
    }
}