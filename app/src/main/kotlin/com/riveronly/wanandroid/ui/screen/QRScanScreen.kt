package com.riveronly.wanandroid.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.riveronly.wanandroid.utils.LifecycleEffect
import com.riveronly.wanandroid.utils.QRCodeAnalyzer
import java.net.URL

/**
 * zxing二维码扫描
 */
@SuppressLint("RememberReturnType")
@Composable
fun QRScanScreen() {
    val view = LocalView.current
    var scanResult by remember { mutableStateOf("") }
    var isBrowserOpen by remember { mutableStateOf(false) }

    //检查是否有相机权限
    val isCameraPermissionGranted = remember {
        view.context.checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    if (!isCameraPermissionGranted) {
        // 如果没有相机权限，则申请权限
        val permissions = arrayOf(android.Manifest.permission.CAMERA)
        requestPermissions(view.context as Activity, permissions, 0)
    }

    LifecycleEffect(onResume = {
        isBrowserOpen = false
    })

    // 打开浏览器的函数
    val openBrowser = {
        if (!isBrowserOpen) {
            isBrowserOpen = true
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scanResult))
            if (isValidUrl(scanResult)) { // 添加URL验证
                view.context.startActivity(intent)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CameraX { result ->
            if (isValidUrl(result)) { // 使用isValidUrl进行URL验证
                scanResult = result
                openBrowser()
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Black
        )
        Button(content = {
            Text(text = scanResult)
        }, onClick = {
            if (isValidUrl(scanResult)) { // 点击按钮时再次使用isValidUrl进行验证
                openBrowser()
            }
        })
    }
}

// 验证URL是否有效
fun isValidUrl(urlString: String): Boolean {
    try {
        URL(urlString).toURI()
        return true
    } catch (e: Exception) {
        return false
    }
}

@Composable
fun CameraX(onScanResult: (String) -> Unit) {
    val view = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(view.context) }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(colorScheme.primary)
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = Icons.Rounded.Close,
            contentDescription = ""
        )
        AndroidView(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(82.dp)
                .clipToBounds(),
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QRCodeAnalyzer { result ->
                        result?.let {
                            onScanResult(it)
                        }
                    })
                try {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner, selector, preview, imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("CameraX", "启动相机时出错", e)
                }

                return@AndroidView previewView
            },
        )
    }
}