package com.riveronly.wanandroid.ui.web

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.riveronly.wanandroid.utils.injectVConsoleJs
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    url: String,
    navigator: WebViewNavigator,
    modifier: Modifier = Modifier,
    goBack: () -> Unit = {},
    goForward: (url: String?) -> Unit = {},
    shouldOverrideUrl: (url: String) -> Unit = {},
    onLoadUrl: (url: String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var fullScreenLayer by remember { mutableStateOf<View?>(null) }
    var injectState by remember { mutableStateOf(false) }
    BackHandler(true) {
        navigator.navigateBack()
    }
    webView?.let {
        LaunchedEffect(it, navigator) {
            navigator.lastLoadedUrl = it.url
            with(navigator) {
                handleNavigationEvents(
                    onBack = {
                        if (it.canGoBack()) {
                            it.goBack()
                        } else if (WebViewManager.back(it)) {
                            goBack()
                        } else {
                            onNavigateUp()
                        }
                    },
                    onForward = {
                        goForward(WebViewManager.forward(it))
                    },
                    reload = {
                        it.reload()
                    }
                )
            }
        }
    }
    AndroidView(
        factory = { context ->
            val activity = context as androidx.activity.ComponentActivity
            val windowManager = activity.windowManager
            WebViewManager.obtain(context, url).apply {
                setDownloadListener()
                setOnLongClickListener()
                this.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val forceDarkMode =
                    currentNightMode == Configuration.UI_MODE_NIGHT_YES
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    settings.isAlgorithmicDarkeningAllowed = forceDarkMode
                } else {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                        WebSettingsCompat.setForceDark(
                            settings,
                            if (forceDarkMode) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                        )
                    }
                }
                webChromeClient = object : WebChromeClient() {

                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        navigator.progress = (newProgress / 100f).coerceIn(0f, 1f)
                        if (newProgress > 80 && navigator.injectVConsole && !injectState) {
                            view.apply { evaluateJavascript(context.injectVConsoleJs()) {} }
                            injectState = true
                        }
                    }

                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        super.onShowCustomView(view, callback)
                        windowManager.addView(
                            view,
                            WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION)
                        )
                        fullScreenLayer = view
                    }

                    override fun onHideCustomView() {
                        super.onHideCustomView()
                        fullScreenLayer?.let {
                            windowManager.removeViewImmediate(fullScreenLayer)
                            fullScreenLayer = null
                        }
                    }

                    override fun onPermissionRequest(request: PermissionRequest?) {
                        if (request == null) {
                            return
                        }

                        val resources = mutableListOf<String>()
                        val permissions = mutableListOf<String>()

                        request.resources.forEach { resource ->
                            when (resource) {
                                "android.webkit.resource.VIDEO_CAPTURE" -> {
                                    resources.add(resource)
                                    permissions.add(Manifest.permission.CAMERA)
                                }

                                "android.webkit.resource.AUDIO_CAPTURE" -> {
                                    resources.add(resource)
                                    permissions.add(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        }

                        val resourcesArray = resources.toTypedArray()
                        val permissionsArray = permissions.toTypedArray()

//                        activity.requestPermissions(permissionsArray, 0)
                    }
                }
                webViewClient = object : WebViewClient() {

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if (view != null && request != null) {
                            when {
                                request.isAssetsResource() -> {
                                    return request.assetsResourceRequest(view.context)
                                }

                                request.isCacheResource() -> {
                                    return request.cacheResourceRequest(view.context)
                                }
                            }
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        if (view == null || request == null) {
                            return false
                        }
                        val requestUrl = request.url.toString()
                        if (request.hasGesture()
                            && !request.isRedirect
                            && URLUtil.isNetworkUrl(requestUrl)
                            && requestUrl != url
                        ) {
                            shouldOverrideUrl(requestUrl)
                            return true
                        }
                        if (!URLUtil.isValidUrl(requestUrl)) {
                            try {
                                view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                            } catch (e: Exception) {
                                Log.e(this.javaClass.name, e.message.toString())
                            }
                            return true
                        }
                        return false
                    }

                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        navigator.lastLoadedUrl = url
                        injectState = false
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        injectState = false
                    }
                }
                if (URLUtil.isValidUrl(url) && !URLUtil.isValidUrl(this.url)) {
                    this.loadUrl(url)
                    onLoadUrl(url)
                }
            }.also { webView = it }
        },
        modifier = modifier,
        onRelease = {
            WebViewManager.recycle(it)
        }
    )
}

@Stable
class WebViewNavigator(
    private val scope: CoroutineScope
) {
    private sealed interface NavigationEvent {
        data object Back : NavigationEvent
        data object Forward : NavigationEvent
        data object Reload : NavigationEvent
    }

    private val navigationEvents: MutableSharedFlow<NavigationEvent> = MutableSharedFlow()

    var lastLoadedUrl: String? by mutableStateOf(null)
        internal set
    var injectVConsole: Boolean by mutableStateOf(false)
        internal set
    var progress: Float by mutableFloatStateOf(0f)
        internal set

    @OptIn(FlowPreview::class)
    internal suspend fun handleNavigationEvents(
        onBack: () -> Unit = {},
        onForward: () -> Unit = {},
        reload: () -> Unit = {},
    ) = withContext(Dispatchers.Main) {
        navigationEvents.debounce(0).collect { event ->
            when (event) {
                NavigationEvent.Back -> onBack()
                NavigationEvent.Forward -> onForward()
                NavigationEvent.Reload -> reload()
            }
        }
    }

    fun navigateBack() {
        scope.launch { navigationEvents.emit(NavigationEvent.Back) }
    }

    fun navigateForward() {
        scope.launch { navigationEvents.emit(NavigationEvent.Forward) }
    }

    fun reload() {
        scope.launch { navigationEvents.emit(NavigationEvent.Reload) }
    }

    fun injectVConsole(): Boolean {
        injectVConsole = !injectVConsole
        reload()
        return injectVConsole
    }

}

@Composable
fun rememberWebViewNavigator(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): WebViewNavigator = remember(coroutineScope) { WebViewNavigator(coroutineScope) }
