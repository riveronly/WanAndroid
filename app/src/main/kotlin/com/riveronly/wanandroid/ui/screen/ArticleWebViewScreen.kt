package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.riveronly.wanandroid.bean.ArticleListBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.ui.modal.loadingModal
import com.riveronly.wanandroid.ui.web.WebView
import com.riveronly.wanandroid.ui.web.rememberWebViewNavigator
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleWebViewScreen(articleBean: ArticleListBean.Data) {
    var article by remember { mutableStateOf(articleBean) }
    val view = LocalView.current
    val loadingView = view.loadingModal()
    val navigator = rememberWebViewNavigator()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = article.title, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navigator.navigateBack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back"
                )
            }
        }, actions = {
            IconButton(onClick = {
                scope.launch {
                    loadingView.show()
                    if (article.collect) {
                        ApiService.unCollect(article.id)
                    } else {
                        ApiService.collect(article.id)
                    }
                    article = article.copy(collect = !article.collect)
                    loadingView.dismiss()
                }
            }) {
                Icon(
                    painter = painterResource(
                        id = if (article.collect) R.drawable.star_fill_24px
                        else R.drawable.star_24px
                    ), contentDescription = ""
                )
            }
        })
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            WebView(
                modifier = Modifier.weight(1f),
                url = article.link,
                navigator = navigator,
                onNavigateUp = {
                    activity.finish()
                }
            )
        }
    }
}