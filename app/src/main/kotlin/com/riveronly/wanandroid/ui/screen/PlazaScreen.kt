package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.delay
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
    val pager = remember {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 8,
            ),
            pagingSourceFactory = { PlazaPagingSource() }
        )
    }
    val pagingItems = pager.flow.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "近期分享",
            fontSize = 18.sp
        )
        var isRefreshing by remember { mutableStateOf(false) }
        val minAnimationDuration = 1000L
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    val startTime = System.currentTimeMillis()

                    pagingItems.refresh()

                    val elapsedTime = System.currentTimeMillis() - startTime
                    // 如果请求时间小于最小动画时长，则延迟剩余时间
                    if (elapsedTime < minAnimationDuration) {
                        delay(minAnimationDuration - elapsedTime)
                    }
                    isRefreshing = false
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
                            intent.putExtra(SCREEN_NAME, Screens.ArticleDetail.route)
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
                    HorizontalDivider()
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
            if (listState.canScrollBackward) {
                ListToTopButton(onClick = {
                    if (listState.firstVisibleItemIndex >= 3) {
                        listState.requestScrollToItem(3)
                    }
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                })
            }
        }
    }
}