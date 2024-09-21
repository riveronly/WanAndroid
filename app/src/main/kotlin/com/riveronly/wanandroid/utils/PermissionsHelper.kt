package com.riveronly.wanandroid.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

object PermissionsHelper {

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var activity: ComponentActivity
    private lateinit var onPermissionsGranted: () -> Unit
    private lateinit var onPermissionsDenied: () -> Unit

    // 初始化并设置 activity
    fun init(activity: ComponentActivity): PermissionsHelper {
        PermissionsHelper.activity = activity
        permissionsLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.all { it.value }
            if (granted) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
        return this
    }

    // 请求权限的统一方法
    private fun requestPermissions(
        permissions: Array<String>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        onPermissionsGranted = onGranted
        onPermissionsDenied = onDenied
        if (permissions.all {
                ContextCompat.checkSelfPermission(
                    activity, it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            onGranted()
        } else {
            permissionsLauncher.launch(permissions)
        }
        return this
    }

    // 链式调用常用权限方法

    //忽略电池优化,保持后台常驻
    fun requestIgnoreBatteryOptimizations() {
        // 申请加入白名单
        try {
            val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
            // 判断应用是否在白名单中
            if (powerManager.isIgnoringBatteryOptimizations(activity.packageName)) {
                return
            }
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivity(intent)
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }

    // 请求相机权限
    fun requestCameraPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            onGranted,
            onDenied
        )
    }

    // 请求存储权限
    fun requestStoragePermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        return requestPermissions(permissions, onGranted, onDenied)
    }

    // 请求位置权限
    fun requestLocationPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            onGranted,
            onDenied
        )
    }

    // 请求电话权限
    fun requestPhonePermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
            ),
            onGranted,
            onDenied
        )
    }

    // 请求联系人权限
    fun requestContactsPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            ),
            onGranted,
            onDenied
        )
    }

    // 请求录音权限
    fun requestRecordAudioPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            onGranted,
            onDenied
        )
    }

    // 请求短信权限
    fun requestSMSPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
            ),
            onGranted,
            onDenied
        )
    }

    // 请求传感器权限
    fun requestSensorsPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): PermissionsHelper {
        return requestPermissions(
            arrayOf(Manifest.permission.BODY_SENSORS),
            onGranted,
            onDenied
        )
    }
}
