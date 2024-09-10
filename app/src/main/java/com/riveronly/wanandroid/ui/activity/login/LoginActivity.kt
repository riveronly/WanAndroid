package com.riveronly.wanandroid.ui.activity.login

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.ui.modal.toast
import com.riveronly.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                val view = LocalView.current
                val loadingView = view.loadingModal()
                val scope = rememberCoroutineScope()
                // 用于记住用户名和密码的状态
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var passwordConfirm by remember { mutableStateOf("") }
                val activity = (LocalContext.current as? Activity)
                val keyboardController = LocalSoftwareKeyboardController.current

                Scaffold(topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (viewModel.isRegister) "注册" else "登录",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                activity?.finish()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                }) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(Modifier.animateContentSize()) {
                            // 用户名输入框
                            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
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

                            if (viewModel.isRegister) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = passwordConfirm,
                                    maxLines = 1,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    onValueChange = { newValue -> passwordConfirm = newValue },
                                    label = { Text("确认密码") },
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            }
                        }

                        // 登录按钮
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp), onClick = {
                            keyboardController?.hide()
                            if (username.isBlank() || password.isBlank()) {
                                view.toast("请输入用户名和密码")
                                return@Button
                            }
                            if (viewModel.isRegister && passwordConfirm.isBlank()) {
                                view.toast("请输入确认密码")
                                return@Button
                            }
                            scope.launch {
                                loadingView.show()
                                if (viewModel.isRegister) {
                                    viewModel.fetchRegister(username, password, passwordConfirm)
                                        .collect {
                                            loadingView.dismiss()
                                            if (it.errorCode == 0) {
                                                viewModel.isRegister = false
                                                view.toast("注册成功,请登录")
                                            } else {
                                                view.toast(it.errorMsg)
                                            }
                                        }
                                } else {
                                    viewModel.fetchLogin(username, password).collect {
                                        loadingView.dismiss()
                                        if (it) {
                                            activity?.finish()
                                        } else {
                                            view.toast("登录失败")
                                        }
                                    }
                                }
                            }
                        }) {
                            Text(
                                fontSize = 16.sp,
                                text = if (viewModel.isRegister) "注册" else "登录"
                            )
                        }
                        //注册按钮
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            onClick = {
                                viewModel.isRegister = !viewModel.isRegister
                            }) {
                            Text(
                                fontSize = 16.sp,
                                text = if (viewModel.isRegister) "←已有账号？去登录" else "没有账号？去注册→"
                            )
                        }
                    }
                }
            }
        }
    }
}