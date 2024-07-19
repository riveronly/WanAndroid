package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.riveronly.wanandroid.helper.KVHelper
import com.riveronly.wanandroid.net.RetrofitBuilder.LOCAL_TOKEN
import com.riveronly.wanandroid.ui.activity.login.LoginActivity
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.ui.modal.toast
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
    val localToken = remember {
        mutableStateOf(KVHelper.getStringSet(LOCAL_TOKEN))
    }

    fun checkLoginStatus() {
        scope.launch {
            localToken.value = KVHelper.getStringSet(LOCAL_TOKEN)
            loadingView.show()
            if (localToken.value.isNullOrEmpty()) {
                viewModel.fetchLogout()
            } else {
                viewModel.fetchUserinfo()
            }
            loadingView.dismiss()
        }
    }

    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        checkLoginStatus()
    }

    LaunchedEffect(Unit) {
        checkLoginStatus()
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
                    if (viewModel.userInfoRes.userInfo.id == 0) {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivityLauncher.launch(intent)
                    } else {
                        scope.launch {
                            viewModel
                                .fetchCoin()
                                .collect {
                                    if (it) {
                                        view.toast("今日已签到")
                                    }
                                }
                        }
                    }
                }
                .padding(10.dp)) {
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .size(48.dp),
                painter = painterResource(id = R.drawable.face_24px),
                contentDescription = "",
                tint = Color.White
            )
            Text(text = viewModel.userInfoRes.userInfo.nickname.takeIf { it.isNotBlank() }
                ?: "请登录", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "我的积分")
                },
                trailingContent = {
                    Text(text = "${viewModel.userInfoRes.coinInfo.coinCount}")
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = "我的收藏")
                },
                trailingContent = {
                    ArrowRightIcon()
                },
                modifier = Modifier.clickable {
                    val intent = Intent(view.context, ScreenActivity::class.java)
                    intent.putExtra(SCREEN_NAME, Screens.CollectList.route)
                    startActivityLauncher.launch(intent)
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = "设置")
                },
                trailingContent = {
                    ArrowRightIcon()
                },
                modifier = Modifier.clickable {
                    val intent = Intent(view.context, ScreenActivity::class.java)
                    intent.putExtra(SCREEN_NAME, Screens.Setting.route)
                    startActivityLauncher.launch(intent)
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ArrowRightIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_chevron), contentDescription = ""
    )
}
