package com.riveronly.wanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.riveronly.wanandroid.ui.screen.HomeScreen
import com.riveronly.wanandroid.ui.screen.MineScreen
import com.riveronly.wanandroid.ui.screen.QRScanScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch

enum class Tab(val title: String, val icon: ImageVector, val isUsing: Boolean = true) {
    Home("首页", Icons.Rounded.Home),
    Scan("扫码", Icons.Rounded.Info, false),
    Me("我的", Icons.Rounded.Person),
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            WanAndroidTheme {
                val pagerState = rememberPagerState(pageCount = { Tab.entries.size })
                val nowPageIndex = pagerState.currentPage
                val scope = rememberCoroutineScope()
                val isInitList =
                    remember { mutableStateOf(BooleanArray(Tab.entries.size) { index -> index == 0 }) }

                Scaffold(bottomBar = {
                    NavigationBar {
                        Tab.entries.forEachIndexed { index, tab ->
                            if (tab.isUsing) {
                                NavigationBarItem(selected = nowPageIndex == index,
                                    onClick = {
                                        scope.launch {
                                            isInitList.value[index] = true
                                            pagerState.scrollToPage(index)
                                        }
                                    },
                                    icon = { Icon(tab.icon, contentDescription = null) },
                                    label = { Text(tab.title) })
                            }
                        }
                    }
                }) { innerPadding ->
                    HorizontalPager(
                        modifier = Modifier.padding(innerPadding),
                        userScrollEnabled = false,
                        state = pagerState,
                        beyondBoundsPageCount = Tab.entries.size,
                    ) { index ->
                        if (isInitList.value[index] && Tab.entries[index].isUsing) {
                            when (Tab.entries[index]) {
                                Tab.Home -> HomeScreen()
                                Tab.Me -> MineScreen()
                                Tab.Scan -> QRScanScreen()
                            }
                        } else {
                            Box {}
                        }
                    }
                }
            }
        }
    }
}
