package com.riveronly.wanandroid.ui.activity.login

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riveronly.wanandroid.R
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.ui.modal.toast
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.bottom_to_top,
            R.anim.light_to_dark
        )
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.dark_to_light,
            R.anim.top_to_bottom
        )

        setContent {
            WanAndroidTheme {
                val view = LocalView.current
                val loadingView = view.loadingModal()
                val scope = rememberCoroutineScope()
                // 用于记住用户名和密码的状态
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                val activity = (LocalContext.current as? Activity)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "", maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                activity?.finish()
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 50.dp, horizontal = 20.dp)
                    ) {
                        // 用户名输入框
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = username,
                            maxLines = 1,
                            singleLine = true,
                            onValueChange = { newValue -> username = newValue },
                            label = { Text("用户名") })

                        // 密码输入框
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            onClick = {
                                if (username.isBlank() || password.isBlank()) {
                                    view.toast("请输入用户名和密码")
                                    return@Button
                                }
                                scope.launch {
                                    loadingView.show()
                                    viewModel.fetchLogin(username, password)
                                    loadingView.dismiss()
                                    if (viewModel.isLogin) {
                                        activity?.finish()
                                    } else {
                                        view.toast("登录失败")
                                    }
                                }
                            }) {
                            Text(fontSize = 16.sp, text = "登录")
                        }
                    }
                }
            }
        }
    }
}