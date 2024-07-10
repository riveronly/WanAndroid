package com.riveronly.wanandroid.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riveronly.wanandroid.MainViewModel
import com.riveronly.wanandroid.R
import com.riveronly.wanandroid.helper.KVHelper
import com.riveronly.wanandroid.net.RetrofitBuilder.LOCAL_TOKEN
import com.riveronly.wanandroid.ui.modal.Item
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
    val localToken = remember {
        mutableStateOf(KVHelper.getStringSet(LOCAL_TOKEN))
    }

    LifecycleEffect(onResume = {
        scope.launch {
            localToken.value = KVHelper.getStringSet(LOCAL_TOKEN)
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
        if (!localToken.value.isNullOrEmpty()) {
            Item(title = "退出登录", accessory = {
                Icon(
                    painter = painterResource(id = R.drawable.logout_24px),
                    contentDescription = ""
                )
            }, onClick = {
                scope.launch {
                    loadingView.show()
                    viewModel.fetchLogout()
                    loadingView.dismiss()
                    localToken.value = KVHelper.getStringSet(LOCAL_TOKEN)
                    activity?.finish()
                }
            })
            HorizontalDivider()
        }
    }
}