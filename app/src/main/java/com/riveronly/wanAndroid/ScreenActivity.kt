package com.riveronly.wanAndroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riveronly.wanAndroid.ui.screen.LoginScreen
import com.riveronly.wanAndroid.ui.screen.WebViewScreen

class ScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDestination = intent.getStringExtra("startDestination") ?: ""
        val webViewUrl = intent.getStringExtra("webViewUrl") ?: ""
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable(
                    route = "LoginScreen",
                ) {
                    LoginScreen(navController)
                }
                composable(
                    route = "WebViewScreen",
                ) {
                    WebViewScreen(webViewUrl)
                }
            }
        }
    }
}