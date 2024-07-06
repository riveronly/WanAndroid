package com.riveronly.wanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.riveronly.wanandroid.ui.screen.HomeScreen
import com.riveronly.wanandroid.ui.screen.MineScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch

enum class Tab(val title: String, val icon: ImageVector, val isUsing: Boolean = true) {
    Home("首页", Icons.Rounded.Home),
    Mine("我的", Icons.Rounded.Face),
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
                val scope = rememberCoroutineScope()

                Scaffold(bottomBar = {
                    NavigationBar {
                        Tab.entries.forEachIndexed { index, tab ->
                            if (tab.isUsing) {
                                NavigationBarItem(
                                    selected = pagerState.settledPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.scrollToPage(index)
                                        }
                                    },
                                    icon = { Icon(tab.icon, contentDescription = "") },
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
                        when (Tab.entries[index]) {
                            Tab.Home -> HomeScreen()
                            Tab.Mine -> MineScreen()
                        }
                    }
                }
            }
        }
    }
}
