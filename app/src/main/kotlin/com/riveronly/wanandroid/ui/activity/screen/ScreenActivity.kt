package com.riveronly.wanandroid.ui.activity.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riveronly.wanandroid.ui.screen.SettingScreen
import com.riveronly.wanandroid.ui.screen.WebViewScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme

const val SCREEN_NAME = "screenName"
const val WEB_VIEW_URL = "webViewUrl"
const val WEB_VIEW_TITLE = "webViewTitle"

class ScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = intent.getStringExtra(SCREEN_NAME) ?: ""
        val webViewTitle = intent.getStringExtra(WEB_VIEW_TITLE) ?: ""
        val webViewUrl = intent.getStringExtra(WEB_VIEW_URL) ?: ""
        setContent {
            WanAndroidTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    composable(
                        route = "WebViewScreen",
                    ) {
                        WebViewScreen(webViewUrl, webViewTitle)
                    }
                    composable(
                        route = "SettingScreen",
                    ) {
                        SettingScreen()
                    }
                }
            }
        }
    }
}