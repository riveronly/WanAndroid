package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.riveronly.wanandroid.bean.BannerItemBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.ui.activity.screen.ARTICLE_BEAN
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.modal.toast
import com.riveronly.wanandroid.ui.paging.HomePagingSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen() {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val imgList = remember { mutableStateOf(ArrayList<BannerItemBean>()) }
    val listState = rememberLazyListState()
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}
    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 8
        ),
        pagingSourceFactory = { HomePagingSource() }
    )
    val pagingItems = pager.flow.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        //banner图片列表
        val banner = ApiService.banner()
        if (banner.errorCode == 0 && banner.data != null) {
            imgList.value = banner.data
        } else {
            view.toast(banner.errorMsg)
        }
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
            item {
                Carousel(imgList.value)
            }
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
                        Text(text = item.niceDate)
                    },
                    supportingContent = {
                        Text(text = item.author.takeIf { author -> author.isNotBlank() }
                            ?: item.shareUser)
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
    }
}

@Composable
fun Carousel(imgList: List<BannerItemBean>) {
    //HorizontalPager的状态
    val pagerState = rememberPagerState(pageCount = { imgList.size })
    //当前滚动到了哪哪一个页面
    val nowPageIndex = pagerState.currentPage
    //用于点击指示器的时候启动协程进行HorizontalPager位置手动更新
    val scope = rememberCoroutineScope()

    //观察pagerState.settledPage，已确保滚动结束再进行下一次自动轮播
    LaunchedEffect(pagerState.settledPage) {
        delay(3000)
        val scroller =
            if (pagerState.currentPage + 1 == imgList.size) 0 else pagerState.currentPage + 1
        pagerState.animateScrollToPage(scroller)
    }

    Box { //这样指示器就可以覆盖在轮播图上
        HorizontalPager(
            state = pagerState,
            //不让一个图最宽，留空可以一个页面渲染出多个图
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = Modifier
                .height(150.dp)
                .padding(top = 10.dp),
            beyondViewportPageCount = imgList.size
        ) { index ->
            //激活项的缩放过渡
            val imgScale by animateFloatAsState(
                targetValue = if (nowPageIndex == index) 1f else 0.8f,
                animationSpec = tween(300),
                label = ""
            )
            //使用Coil加载网络图片
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(imgScale)
                    .clip(
                        RoundedCornerShape(10.dp) //轮播图圆角裁剪
                    )
                    .background(Color.Gray),
                model = ImageRequest.Builder(LocalContext.current).data(imgList[index].imagePath)
                    .scale(Scale.FIT).build(),
                contentDescription = "图片$index",
                contentScale = ContentScale.FillBounds
            )
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp), horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                Box(modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(iteration)
                        }
                    })
            }
        }
    }
}