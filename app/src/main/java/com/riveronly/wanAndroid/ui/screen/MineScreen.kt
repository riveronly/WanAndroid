package com.riveronly.wanAndroid.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riveronly.wanAndroid.ClickLogin
import com.riveronly.wanAndroid.ClickLogout
import com.riveronly.wanAndroid.LoginActivity
import com.riveronly.wanAndroid.bean.LoginBean
import com.riveronly.wanAndroid.net.ApiService
import com.riveronly.wanAndroid.ui.modal.loadingModal
import com.riveronly.wanAndroid.ui.modal.toast
import com.riveronly.wanAndroid.utils.MMKVUtil
import kotlinx.coroutines.launch

/**
 * 我的
 */
@SuppressLint("ResourceType")
@Composable
fun MineScreen() {
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current
    val loadingView = view.loadingModal()
    val userInfoState = remember { mutableStateOf(LoginBean()) }
    // 使用 rememberLauncherForActivityResult 创建启动 Activity 的 launcher
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val clickLogin = result.data?.getBooleanExtra(ClickLogin, false) ?: false
            val clickLogout = result.data?.getBooleanExtra(ClickLogout, false) ?: false
            if (clickLogin) {
                scope.launch {
                    val username = MMKVUtil.getString("username")
                    val password = MMKVUtil.getString("password")
                    if (username.isBlank() || password.isBlank()) {
                        return@launch
                    }
                    //登录
                    loadingView.show()
                    val login = ApiService.login(username, password)
                    loadingView.dismiss()
                    view.toast(login.errorMsg.takeIf { login.errorCode != 0 } ?: "登录成功")
                    if (login.errorCode == 0) {
                        userInfoState.value = login.data!!
                        //签到
                        val coin = ApiService.coin()
                        view.toast(coin.errorMsg.takeIf { coin.errorCode != 0 }
                            ?: "积分${coin.data?.coinCount} 排名${coin.data?.rank}")
                    }
                }
            }
            if (clickLogout) {
                scope.launch {
                    loadingView.show()
                    val logout = ApiService.logout()
                    userInfoState.value = LoginBean()
                    loadingView.dismiss()
                    view.toast(logout.errorMsg.takeIf { logout.errorCode != 0 } ?: "已退出登录")
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(100.dp)
                .clickable {
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivityLauncher.launch(intent)
                }
                .padding(10.dp)
        ) {
            Text(text = userInfoState.value.nickname.takeIf { it.isNotEmpty() } ?: "登录",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}
