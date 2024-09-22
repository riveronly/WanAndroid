package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
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
import com.riveronly.wanandroid.helper.DataStoreHelper
import com.riveronly.wanandroid.net.RetrofitBuilder.LOCAL_TOKEN
import com.riveronly.wanandroid.ui.activity.login.LoginActivity
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.modal.toast
import github.leavesczy.matisse.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 我的
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ResourceType")
@Composable
fun MineScreen() {
    val viewModel: MainViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current
    var localToken by remember {
        mutableStateOf(DataStoreHelper.getStringSet(LOCAL_TOKEN))
    }

    fun checkLoginStatus() {
        scope.launch {
            localToken = DataStoreHelper.getStringSet(LOCAL_TOKEN)
            localToken.collect {
                if (it.isEmpty()) {
                    viewModel.fetchLogout()
                } else {
                    viewModel.fetchUserinfo()
                }
            }
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

    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val minAnimationDuration = 1000L
    val mediaPickerLauncher =
        rememberLauncherForActivityResult(contract = MatisseContract()) { result: List<MediaResource>? ->
            if (!result.isNullOrEmpty()) {
                val mediaResource = result[0]
                val uri = mediaResource.uri
                val path = mediaResource.path
                val name = mediaResource.name
                val mimeType = mediaResource.mimeType
            }
        }

    val matisse = Matisse(
        maxSelectable = 1,
        imageEngine = CoilImageEngine(),
        mediaType = MediaType.ImageOnly
    )

    PullToRefreshBox(
        isRefreshing,
        state = pullToRefreshState,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                val startTime = System.currentTimeMillis()

                checkLoginStatus()

                val elapsedTime = System.currentTimeMillis() - startTime
                // 如果请求时间小于最小动画时长，则延迟剩余时间
                if (elapsedTime < minAnimationDuration) {
                    delay(minAnimationDuration - elapsedTime)
                }
                isRefreshing = false
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if (viewModel.userInfoRes.userInfo.id == 0) {
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivityLauncher.launch(intent)
                        } else {
                            mediaPickerLauncher.launch(matisse)
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
                    ?: "请登录",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = "今日签到")
                    },
                    modifier = Modifier.clickable {
                        if (viewModel.userInfoRes.userInfo.id == 0) {
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivityLauncher.launch(intent)
                        } else {
                            scope.launch {
                                viewModel
                                    .fetchCoin()
                                    .collect {
                                        if (it) {
                                            view.toast("签到成功")
                                        }
                                    }
                            }
                        }
                    }
                )
                HorizontalDivider()
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
}

@Composable
fun ArrowRightIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_chevron), contentDescription = ""
    )
}
