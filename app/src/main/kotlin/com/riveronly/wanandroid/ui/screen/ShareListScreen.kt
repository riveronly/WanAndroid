package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import com.riveronly.wanandroid.bean.CollectBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.ui.activity.screen.ARTICLE_BEAN
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.modal.Item
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.ui.modal.toast
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun ShareListScreen() {
    val view = LocalView.current
    val loadingView = view.loadingModal()
    val scope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)
    val collectListRes = remember { mutableStateOf(CollectBean()) }
    val listState = rememberLazyListState()
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    suspend fun fetchApi() {
        //帖子列表
        loadingView.show()
        val collectList = ApiService.collectList(0)
        loadingView.dismiss()
        if (collectList.errorCode == 0 && collectList.data != null) {
            collectListRes.value = collectList.data
        } else {
            view.toast(collectList.errorMsg)
        }
    }
    LaunchedEffect(Unit) {
        fetchApi()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(title = {
            Text(
                text = "我的分享", maxLines = 1, overflow = TextOverflow.Ellipsis
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
        LazyColumn(
            state = listState, modifier = Modifier.fillMaxSize()
        ) {
            items(items = collectListRes.value.datas) { item ->
                Item(title = item.title,
                    detail = item.author + ' ' + item.niceDate,
                    onClick = {
                        val intent = Intent(view.context, ScreenActivity::class.java)
                        intent.putExtra(SCREEN_NAME, Screens.ArticleWebView.route)
                        intent.putExtra(ARTICLE_BEAN, Json.encodeToString(item))
                        startActivityLauncher.launch(intent)
                    })
            }
        }
    }
}