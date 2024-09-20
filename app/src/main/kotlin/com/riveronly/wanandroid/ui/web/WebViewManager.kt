package com.riveronly.wanandroid.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.MutableContextWrapper
import android.graphics.Color
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import java.lang.ref.WeakReference

@SuppressLint("SetJavaScriptEnabled")
class WebViewManager private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: WebViewManager? = null

        private fun getInstance() = INSTANCE ?: synchronized(WebViewManager::class.java) {
            INSTANCE ?: WebViewManager().also { INSTANCE = it }
        }

        fun prepare(context: Context) {
            getInstance().prepare(context)
        }

        fun destroy() {
            getInstance().destroy()
        }

        fun obtain(context: Context, url: String): WebView {
            return getInstance().obtain(context, url)
        }

        fun back(webView: WebView): Boolean {
            return getInstance().back(webView)
        }

        fun forward(webView: WebView): String? {
            return getInstance().forward(webView)
        }

        fun recycle(webView: WebView) {
            getInstance().recycle(webView)
        }

    }

    private val webViewMap = mutableMapOf<String, WebView>()
    private val webViewQueue: ArrayDeque<WebView> = ArrayDeque()
    private val backStack: ArrayDeque<String> = ArrayDeque()
    private val forwardStack: ArrayDeque<String> = ArrayDeque()
    private var lastBackWebView: WeakReference<WebView?> = WeakReference(null)

    private fun getWebView(context: Context): WebView {
        val webView = if (webViewQueue.isEmpty()) {
            create(MutableContextWrapper(context))
        } else {
            webViewQueue.removeFirst()
        }
        prepare(MutableContextWrapper(context.applicationContext))
        return webView
    }

    private fun create(context: Context): WebView {
        val webView = WebView(context)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.overScrollMode = WebView.OVER_SCROLL_NEVER
        webView.isVerticalScrollBarEnabled = false
        val webSettings = webView.settings
        webSettings.setSupportZoom(true)
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.displayZoomControls = false
        webSettings.useWideViewPort = true
        webSettings.mediaPlaybackRequiresUserGesture = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        return webView
    }

    private fun prepare(context: Context) {
        if (webViewQueue.isEmpty()) {
            Looper.myQueue().addIdleHandler {
                webViewQueue.add(create(MutableContextWrapper(context.applicationContext)))
                false
            }
        }
    }

    private fun destroy() {
        try {
            backStack.clear()
            forwardStack.clear()
            lastBackWebView.clear()
            webViewMap.destroyWebView()
            webViewQueue.destroyWebView()
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }

    private fun obtain(context: Context, url: String): WebView {
        val webView = webViewMap.getOrPut(url) {
            getWebView(MutableContextWrapper(context))
        }
        if (webView.parent != null) {
            (webView.parent as ViewGroup).removeView(webView)
        }
        val contextWrapper = webView.context as MutableContextWrapper
        contextWrapper.baseContext = context
        return webView
    }

    private fun back(webView: WebView): Boolean {
        return try {
            backStack.removeLast()//通过NoSuchElementException判断是否处在第一页
            forwardStack.add(webView.originalUrl.toString())
            true
        } catch (e: Exception) {
            lastBackWebView = WeakReference(webView)
            false
        }
    }

    private fun forward(webView: WebView): String? {
        return try {
            val forwardLastUrl = forwardStack.removeLast()
            backStack.add(webView.originalUrl.toString())
            forwardLastUrl
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            null
        }
    }

    private fun recycle(webView: WebView) {
        try {
            webView.removeParentView()
            val originalUrl = webView.originalUrl.toString()
            if (lastBackWebView.get() != webView) {
                if (!forwardStack.contains(originalUrl)) {
                    backStack.add(originalUrl)
                }
            } else {
                destroy()
                //重新缓存一个webView
                prepare(webView.context)
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }

    private fun WebView.removeParentView(): WebView {
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        val contextWrapper = context as MutableContextWrapper
        contextWrapper.baseContext = context.applicationContext
        return this
    }

    private fun MutableList<WebView>.destroyWebView() {
        forEach {
            it.removeParentView()
            it.removeAllViews()
            it.destroy()
        }
        clear()
    }

    private fun MutableMap<String, WebView>.destroyWebView() {
        values.toList().forEach {
            it.removeParentView()
            it.removeAllViews()
            it.destroy()
        }
        clear()
    }

}

fun WebView.setDownloadListener() {
    setDownloadListener { url, _, _, _, _ ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }
}

fun WebView.setOnLongClickListener() {
    setOnLongClickListener {
        val result = hitTestResult
        when (result.type) {
            WebView.HitTestResult.IMAGE_TYPE, WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                result.extra?.let { extra ->
                    val contextWrapper = context as MutableContextWrapper
                    contextWrapper.baseContext.saveImageBase64Dialog(extra)
                }
                true
            }

            else -> false
        }
    }
}

private fun Context.saveImageBase64Dialog(data: String) {
//    showStandardDialog(
//        content = "你希望保存该图片吗？",
//        confirm = {
//            if (URLUtil.isValidUrl(data)) {
//                saveImagesToAlbum(data) { _, _ -> }
//            } else {
//                var str = data
//                if (str.contains(",")) {
//                    str = str.split(",")[1]
//                }
//                val array = Base64.decode(str, Base64.NO_WRAP)
//                val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
//                saveImagesToAlbum(bitmap) { _, _ -> }
//            }
//        }
//    )
}

fun WebResourceRequest.isAssetsResource(): Boolean {
    val url = url.toString()
    return url.startsWith("file:///android_asset/")
}

fun WebResourceRequest.isCacheResource(): Boolean {
    val url = url.toString()
    val extension = url.getExtensionFromUrl()
    return extension == "ico" || extension == "bmp" || extension == "gif"
            || extension == "jpeg" || extension == "jpg" || extension == "png"
            || extension == "svg" || extension == "webp" || extension == "css"
            || extension == "js" || extension == "json" || extension == "eot"
            || extension == "otf" || extension == "ttf" || extension == "woff"
}

fun WebResourceRequest.assetsResourceRequest(context: Context): WebResourceResponse? {
    try {
        val url = url.toString()
        val filenameIndex = url.lastIndexOf("/") + 1
        val filename = url.substring(filenameIndex)
        val suffixIndex = url.lastIndexOf(".")
        val suffix = url.substring(suffixIndex + 1)
        val webResourceResponse = WebResourceResponse(
            url.getMimeTypeFromUrl(),
            null,
            context.assets.open("$suffix/$filename")
        )
        webResourceResponse.responseHeaders = mapOf("access-control-allow-origin" to "*")
        return webResourceResponse
    } catch (e: Exception) {
        Log.e(this.javaClass.name, e.message.toString())
    }
    return null
}

fun WebResourceRequest.cacheResourceRequest(context: Context): WebResourceResponse? {
//    try {
//        val url = url.toString()
//        val savePath = CacheUtils.getDirPath(context, "web_cache")
//        val fileName = url.encodeUtf8().md5().hex()
//        val file = File(savePath, fileName)
//        if (!file.exists() || !file.isFile) {
//            runBlocking {
//                download(savePath, fileName) {
//                    setUrl(url)
//                    putHeader(requestHeaders)
//                }
//            }
//        }
//        if (file.exists() && file.isFile) {
//            val webResourceResponse = WebResourceResponse(
//                url.getMimeTypeFromUrl(),
//                null,
//                file.inputStream()
//            )
//            webResourceResponse.responseHeaders = mapOf("access-control-allow-origin" to "*")
//            return webResourceResponse
//        }
//    } catch (e: Exception) {
//        Log.e(this.javaClass.name, e.message.toString())
//    }
    return null
}

private fun String.getExtensionFromUrl(): String {
    try {
        if (isNotBlank() && this != "null") {
            return MimeTypeMap.getFileExtensionFromUrl(this)
        }
    } catch (e: Exception) {
        Log.e(this.javaClass.name, e.message.toString())
    }
    return ""
}

private fun String.getMimeTypeFromUrl(): String {
    try {
        val extension = this.getExtensionFromUrl()
        if (extension.isNotBlank() && extension != "null") {
            if (extension == "json") {
                return "application/json"
            }
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
        }
    } catch (e: Exception) {
        Log.e(this.javaClass.name, e.message.toString())
    }
    return "*/*"
}