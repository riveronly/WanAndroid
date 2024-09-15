package com.riveronly.wanandroid.ui.screen

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riveronly.wanandroid.MainViewModel
import com.riveronly.wanandroid.R
import com.riveronly.wanandroid.helper.DataStoreHelper
import com.riveronly.wanandroid.net.RetrofitBuilder.LOCAL_TOKEN
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.utils.LifecycleEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {
    val viewModel: MainViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val loadingView = view.loadingModal()
    val activity = (LocalContext.current as? Activity)
    var localToken by remember {
        mutableStateOf(DataStoreHelper.getStringSet(LOCAL_TOKEN))
    }
    var isShowFooter = false

    LifecycleEffect(onResume = {
        scope.launch {
            localToken = DataStoreHelper.getStringSet(LOCAL_TOKEN)
            localToken.collect {
                isShowFooter = it.isNotEmpty()
            }
        }
    })

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = {
            Text(
                text = "设置", maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }, navigationIcon = {
            IconButton(onClick = {
                activity?.finish()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back"
                )
            }
        })
        if (isShowFooter) {
            ListItem(
                modifier = Modifier.clickable {
                    scope.launch {
                        loadingView.show()
                        viewModel.fetchLogout()
                        loadingView.dismiss()
                        localToken = DataStoreHelper.getStringSet(LOCAL_TOKEN)
                        activity?.finish()
                    }
                },
                headlineContent = {
                    Text("退出登录")
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.logout_24px),
                        contentDescription = ""
                    )
                }
            )
            HorizontalDivider()
        }
    }
}