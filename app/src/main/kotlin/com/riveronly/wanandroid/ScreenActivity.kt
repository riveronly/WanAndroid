package com.riveronly.wanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riveronly.wanandroid.ui.screen.WebViewScreen
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme

class ScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = intent.getStringExtra("startDestination") ?: ""
        val webViewUrl = intent.getStringExtra("webViewUrl") ?: ""
        val webViewTitle = intent.getStringExtra("webViewTitle") ?: ""
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
                }
            }
        }
    }
}