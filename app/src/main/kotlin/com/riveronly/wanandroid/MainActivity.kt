package com.riveronly.wanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.riveronly.wanandroid.Tab.Home
import com.riveronly.wanandroid.Tab.Mine
import com.riveronly.wanandroid.Tab.Plaza
import com.riveronly.wanandroid.ui.screen.HomeScreen
import com.riveronly.wanandroid.ui.screen.MineScreen
import com.riveronly.wanandroid.ui.screen.PlazaScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch

enum class Tab(
    val title: String,
    @DrawableRes val iconResNormal: Int,
    @DrawableRes val iconResFill: Int,
    val isUsing: Boolean = true
) {
    Home("首页", R.drawable.home_24px, R.drawable.home_fill_24px),
    Plaza("广场", R.drawable.dashboard_24px, R.drawable.dashboard_fill_24px),
    Mine("我的", R.drawable.face_24px, R.drawable.face_fill_24px),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            WanAndroidTheme {
                val pagerState = rememberPagerState(pageCount = { Tab.entries.size })
                val scope = rememberCoroutineScope()
                val homeListState = rememberLazyListState()
                val plazaListState = rememberLazyListState()

                Scaffold(bottomBar = {
                    NavigationBar {
                        Tab.entries.forEachIndexed { index, tab ->
                            if (tab.isUsing) {
                                val isSelected = pagerState.settledPage == index
                                NavigationBarItem(
                                    label = { Text(tab.title) },
                                    selected = isSelected,
                                    onClick = {
                                        scope.launch {
                                            if (isSelected) {
                                                when (index) {
                                                    0 -> scrollToTop(homeListState)
                                                    1 -> scrollToTop(plazaListState)
                                                }
                                            } else {
                                                pagerState.scrollToPage(index)
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painterResource(
                                                if (isSelected) {
                                                    when (index) {
                                                        0 -> if (homeListState.canScrollBackward) {
                                                            R.drawable.vertical_align_top_24px
                                                        } else {
                                                            tab.iconResFill
                                                        }

                                                        1 -> if (plazaListState.canScrollBackward) {
                                                            R.drawable.vertical_align_top_24px
                                                        } else {
                                                            tab.iconResFill
                                                        }

                                                        else -> tab.iconResFill
                                                    }
                                                } else {
                                                    tab.iconResNormal
                                                }
                                            ),
                                            contentDescription = ""
                                        )
                                    }
                                )
                            }
                        }
                    }
                }) { innerPadding ->
                    HorizontalPager(
                        modifier = Modifier.padding(innerPadding),
                        userScrollEnabled = false,
                        state = pagerState,
                        beyondViewportPageCount = Tab.entries.size,
                    ) { index ->
                        when (Tab.entries[index]) {
                            Home -> HomeScreen(homeListState)
                            Plaza -> PlazaScreen(plazaListState)
                            Mine -> MineScreen()
                        }
                    }
                }
            }
        }
    }
}

/**
 * listState控制滑到顶部
 */
suspend fun scrollToTop(listState: LazyListState) {
    if (listState.firstVisibleItemIndex >= 1) {
        listState.scrollToItem(1)
    }
    listState.animateScrollToItem(0)
}
