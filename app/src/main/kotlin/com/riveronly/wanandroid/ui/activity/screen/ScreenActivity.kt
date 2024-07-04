package com.riveronly.wanandroid.ui.activity.screen

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riveronly.wanandroid.bean.ArticleListBean
import com.riveronly.wanandroid.ui.screen.ArticleWebViewScreen
import com.riveronly.wanandroid.ui.screen.CollectListScreen
import com.riveronly.wanandroid.ui.screen.SettingScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.serialization.json.Json

const val SCREEN_NAME = "screenName"
const val ARTICLE_BEAN = "articleBean"

class ScreenActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = intent.getStringExtra(SCREEN_NAME) ?: ""
        val articleBeanJson = intent.getStringExtra(ARTICLE_BEAN) ?: ""
        val articleBean by lazy { Json.decodeFromString<ArticleListBean.Data>(articleBeanJson) }
        setContent {
            WanAndroidTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    composable(
                        route = "ArticleWebViewScreen",
                    ) {
                        ArticleWebViewScreen(articleBean)
                    }
                    composable(
                        route = "SettingScreen",
                    ) {
                        SettingScreen()
                    }
                    composable(
                        route = "CollectListScreen",
                    ) {
                        CollectListScreen()
                    }
                }
            }
        }
    }
}