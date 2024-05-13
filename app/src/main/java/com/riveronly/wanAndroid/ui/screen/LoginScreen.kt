package com.riveronly.wanAndroid.ui.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.riveronly.wanAndroid.utils.MMKVUtil

const val ClickLogin = "ClickLogin"
const val ClickLogout = "ClickLogout"

@Composable
fun LoginScreen(navController: NavController? = null) {
    val usernameCache = MMKVUtil.getString("username")
    val passwordCache = MMKVUtil.getString("password")
    // 用于记住用户名和密码的状态
    var username by remember { mutableStateOf(usernameCache) }
    var password by remember { mutableStateOf(passwordCache) }
    val activity = (LocalContext.current as? Activity)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (passwordCache.isBlank()) {
            // 用户名输入框
            OutlinedTextField(
                value = username,
                maxLines = 1,
                singleLine = true,
                onValueChange = { newValue -> username = newValue },
                label = { Text("用户名") }
            )

            // 密码输入框
            OutlinedTextField(
                value = password,
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { newValue -> password = newValue },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation()
            )

            // 登录按钮
            Button(
                onClick = {
                    MMKVUtil.put("username", username)
                    MMKVUtil.put("password", password)
                    val intent = Intent()
                    intent.putExtra(ClickLogin, true)
                    activity?.setResult(Activity.RESULT_OK, intent)
                    activity?.finish()
                }
            ) {
                Text("登录")
            }
        } else {
            // 退出按钮
            Button(
                onClick = {
                    MMKVUtil.removeKey("password")
                    val intent = Intent()
                    intent.putExtra(ClickLogout, true)
                    activity?.setResult(Activity.RESULT_OK, intent)
                    activity?.finish()
                }
            ) {
                Text("退出账号")
            }
        }
    }
}
