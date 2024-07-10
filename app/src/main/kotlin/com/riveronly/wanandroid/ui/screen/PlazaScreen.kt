package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import com.riveronly.wanandroid.ui.activity.screen.ARTICLE_BEAN
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.paging.PlazaPagingSource
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun PlazaScreen() {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}
    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 8,
        ),
        pagingSourceFactory = { PlazaPagingSource() }
    )
    val pagingItems = pager.flow.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Text(text = "近期分享", fontSize = 18.sp)
        }
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = false,
            onRefresh = {
                scope.launch {
                    pagingItems.refresh()
                    pullToRefreshState.animateToHidden()
                }
            }) {
            LazyColumn(
                state = listState, modifier = Modifier.fillMaxSize()
            ) {
                items(pagingItems.itemCount) {
                    val item = pagingItems[it] ?: return@items

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
                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "加载中...")
                        }
                    }
                }
            }
        }
    }
}