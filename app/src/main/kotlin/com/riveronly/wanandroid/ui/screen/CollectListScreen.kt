package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.riveronly.wanandroid.R
import com.riveronly.wanandroid.bean.CollectBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.ui.activity.screen.ARTICLE_BEAN
import com.riveronly.wanandroid.ui.activity.screen.SCREEN_NAME
import com.riveronly.wanandroid.ui.activity.screen.ScreenActivity
import com.riveronly.wanandroid.ui.activity.screen.Screens
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.ui.modal.toast
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun CollectListScreen() {
    val view = LocalView.current
    val loadingView = view.loadingModal()
    val scope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)
    var collectListRes by remember { mutableStateOf(CollectBean()) }
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
            collectListRes = collectList.data
        } else {
            view.toast(collectList.errorMsg)
        }
    }
    LaunchedEffect(Unit) {
        fetchApi()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(title = {
            Text(
                text = "我的收藏", maxLines = 1, overflow = TextOverflow.Ellipsis
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
        LazyColumn(
            state = listState, modifier = Modifier.fillMaxSize()
        ) {
            items(items = collectListRes.datas) { item ->
                ListItem(
                    modifier = Modifier.clickable {
                        val intent = Intent(view.context, ScreenActivity::class.java)
                        intent.putExtra(SCREEN_NAME, Screens.ArticleDetail.route)
                        intent.putExtra(ARTICLE_BEAN, Json.encodeToString(item))
                        startActivityLauncher.launch(intent)
                    },
                    headlineContent = {
                        Text(item.title)
                    },
                    supportingContent = {
                        Text(item.author + ' ' + item.niceDate)
                    },
                    trailingContent = {
                        IconButton(onClick = {
                            scope.launch {
                                loadingView.show()
                                ApiService.unCollectInMine(item.id, item.originId)
                                loadingView.dismiss()
                                fetchApi()
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.star_fill_24px
                                ), contentDescription = ""
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}