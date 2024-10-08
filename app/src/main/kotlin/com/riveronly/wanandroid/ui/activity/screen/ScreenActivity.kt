package com.riveronly.wanandroid.ui.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riveronly.wanandroid.bean.ArticleListBean
import com.riveronly.wanandroid.ui.screen.CollectListScreen
import com.riveronly.wanandroid.ui.screen.SettingScreen
import com.riveronly.wanandroid.ui.screen.WebViewScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.serialization.json.Json

const val SCREEN_NAME = "screenName"
const val ARTICLE_BEAN = "articleBean"

enum class Screens(val route: String) {
    Setting("设置"),
    ArticleDetail("文章详情"),
    CollectList("我的收藏"),
}

class ScreenActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val screenName = intent.getStringExtra(SCREEN_NAME) ?: ""
        val articleBeanJson = intent.getStringExtra(ARTICLE_BEAN) ?: ""
        val json = Json {
            ignoreUnknownKeys = true
        }
        val articleBean by lazy { json.decodeFromString<ArticleListBean.Data>(articleBeanJson) }

        setContent {
            WanAndroidTheme {
                Scaffold {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = screenName
                    ) {
                        composable(Screens.Setting.route) {
                            SettingScreen()
                        }
                        composable(Screens.ArticleDetail.route) {
                            WebViewScreen(articleBean)
                        }
                        composable(Screens.CollectList.route) {
                            CollectListScreen()
                        }
                    }
                }
            }
        }
    }
}