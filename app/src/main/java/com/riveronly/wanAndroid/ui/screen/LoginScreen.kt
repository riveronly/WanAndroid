package com.riveronly.wanAndroid.ui.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riveronly.wanAndroid.ui.modal.toast
import com.riveronly.wanAndroid.utils.MMKVUtil

const val ClickLogin = "ClickLogin"
const val ClickLogout = "ClickLogout"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController? = null) {
    val view = LocalView.current
    val usernameCache = MMKVUtil.getString("username")
    val passwordCache = MMKVUtil.getString("password")
    // 用于记住用户名和密码的状态
    var username by remember { mutableStateOf(usernameCache) }
    var password by remember { mutableStateOf(passwordCache) }
    val activity = (LocalContext.current as? Activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(title = {
            Text(
                text = "", maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }, navigationIcon = {
            IconButton(onClick = {
                activity?.finish()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0xfff5f5f5))
                .weight(1f)
                .fillMaxSize()
                .padding(top = 50.dp)
        ) {
            // 用户名输入框
            OutlinedTextField(value = username,
                maxLines = 1,
                singleLine = true,
                onValueChange = { newValue -> username = newValue },
                label = { Text("用户名") })

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
            Button(onClick = {
                if (username.isBlank() || password.isBlank()) {
                    view.toast("请输入用户名和密码")
                    return@Button
                }
                MMKVUtil.put("username", username)
                MMKVUtil.put("password", password)
                val intent = Intent()
                intent.putExtra(ClickLogin, true)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }) {
                Text("登录")
            }
        }
    }
}
