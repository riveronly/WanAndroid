package com.riveronly.wanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riveronly.wanandroid.Tab.Home
import com.riveronly.wanandroid.Tab.Mine
import com.riveronly.wanandroid.Tab.Plaza
import com.riveronly.wanandroid.ui.screen.HomeScreen
import com.riveronly.wanandroid.ui.screen.MineScreen
import com.riveronly.wanandroid.ui.screen.PlazaScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme

enum class Tab(
    val title: String,
    @DrawableRes val iconResNormal: Int,
    @DrawableRes val iconResFill: Int,
) {
    Home("首页", R.drawable.home_24px, R.drawable.home_fill_24px),
    Plaza("广场", R.drawable.dashboard_24px, R.drawable.dashboard_fill_24px),
    Mine("我的", R.drawable.face_24px, R.drawable.face_fill_24px),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            WanAndroidTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        CustomizeNavigationBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Home.title
                    ) {
                        composable(Home.title) { HomeScreen() }
                        composable(Plaza.title) { PlazaScreen() }
                        composable(Mine.title) { MineScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomizeNavigationBar(navController: NavController) {
    NavigationBar {
        Tab.entries.forEach { tab ->
            val isSelected =
                navController.currentDestination?.route == tab.title
            NavigationBarItem(
                label = { Text(tab.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(tab.title) {
                        // 避免 BackStack 增长，跳转页面时，将栈内 startDestination 之外的页面弹出
                        popUpTo(navController.graph.findStartDestination().id) {
                            //出栈的 BackStack 保存状态
                            saveState = true
                        }
                        // 避免点击同一个 Item 时反复入栈
                        launchSingleTop = true

                        // 如果之前出栈时保存状态了，那么重新入栈时恢复状态
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(
                            if (isSelected) tab.iconResFill
                            else tab.iconResNormal
                        ),
                        contentDescription = ""
                    )
                }
            )
        }
    }
}
