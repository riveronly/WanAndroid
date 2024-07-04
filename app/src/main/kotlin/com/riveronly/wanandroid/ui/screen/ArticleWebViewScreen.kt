package com.riveronly.wanandroid.ui.screen

import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import com.kevinnzou.web.LoadingState
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewNavigator
import com.kevinnzou.web.rememberWebViewState
import com.riveronly.wanandroid.bean.ArticleListBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.ui.modal.loadingModal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleWebViewScreen(articleBean: ArticleListBean.Data) {
    val webViewTitle: String = articleBean.title
    val webViewUrl: String = articleBean.link
    val webViewUrlId: Int = articleBean.id
    val view = LocalView.current
    val loadingView = view.loadingModal()
    val state = rememberWebViewState(webViewUrl)
    val navigator = rememberWebViewNavigator()
    val scope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)
    val loadingState = state.loadingState

    Column {
        TopAppBar(title = {
            Text(
                text = webViewTitle, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }, navigationIcon = {
            IconButton(onClick = {
                if (navigator.canGoBack) {
                    navigator.navigateBack()
                } else {
                    activity?.finish()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
            actions = {
                Button(onClick = {
                    scope.launch {
                        loadingView.show()
                        ApiService.collect(webViewUrlId)
                        loadingView.dismiss()
                    }
                }) {
                    Text(text = "收藏")
                }
            }
        )
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = { loadingState.progress }, modifier = Modifier.fillMaxWidth()
            )
        }
        WebView(
            modifier = Modifier
                .weight(1f)
                .animateContentSize(),
            state = state,
            navigator = navigator
        )
    }
}