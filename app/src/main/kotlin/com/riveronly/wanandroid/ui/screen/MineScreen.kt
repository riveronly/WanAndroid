package com.riveronly.wanandroid.ui.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riveronly.wanandroid.MainViewModel
import com.riveronly.wanandroid.R
import com.riveronly.wanandroid.ui.activity.LoginActivity
import com.riveronly.wanandroid.ui.modal.Item
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.utils.MMKVUtil
import kotlinx.coroutines.launch

/**
 * 我的
 */
@SuppressLint("ResourceType")
@Composable
fun MineScreen() {
    val viewModel: MainViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current
    val loadingView = view.loadingModal()
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val username = MMKVUtil.getString("username")
            val password = MMKVUtil.getString("password")
            if (username.isNotBlank() && password.isNotBlank()) {
                scope.launch {
                    loadingView.show()
                    viewModel.fetchLogin(username, password)
                    loadingView.dismiss()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    if (!viewModel.isLogin) {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivityLauncher.launch(intent)
                    }
                }
                .padding(10.dp)) {
            Text(text = viewModel.userInfoRes.nickname.takeIf { viewModel.isLogin } ?: "请登录",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Item(
                title = "我的积分",
                accessory = { Text(text = "${viewModel.coinRes.coinCount}") },
                onClick = {})
            Item(title = "我的分享", accessory = { ArrowRightIcon() }, onClick = {})
            Item(title = "我的收藏", accessory = { ArrowRightIcon() }, onClick = {})
            Item(title = "稍后阅读", accessory = { ArrowRightIcon() }, onClick = {})
            Item(title = "关于作者", accessory = { ArrowRightIcon() }, onClick = {})
            Item(title = "设置", accessory = { ArrowRightIcon() }, onClick = {})
            Item(title = "退出登录", accessory = { ArrowRightIcon() }, onClick = {
                scope.launch {
                    loadingView.show()
                    viewModel.fetchLogout()
                    loadingView.dismiss()
                }
            })
        }
    }
}

@Composable
fun ArrowRightIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_chevron),
        contentDescription = ""
    )
}
