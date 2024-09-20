package com.riveronly.wanandroid.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 忽略电池优化,保持后台常驻
 */
@SuppressLint("BatteryLife")
fun Context.requestIgnoreBatteryOptimizations() {
    //申请加入白名单
    try {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        //判断应用是否在白名单中
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
            return
        }
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:${packageName}")
        startActivity(intent)
    } catch (e: Exception) {
        Log.e(this.javaClass.name, e.message.toString())
    }
}

/**
 * 申请日历权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestCalendar(callback: PermissionsCallback) {
    supportFragmentManager.requestCalendar(callback)
}

fun FragmentManager.requestCalendar(callback: PermissionsCallback) {
    val permissions = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
    requestPermissions(permissions, callback)
}

/**
 * 申请相机权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestCamera(callback: PermissionsCallback) {
    supportFragmentManager.requestCamera(callback)
}

fun FragmentManager.requestCamera(callback: PermissionsCallback) {
    val permissions = arrayOf(Manifest.permission.CAMERA)
    requestPermissions(permissions, callback)
}

/**
 * 申请联系人权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestContacts(callback: PermissionsCallback) {
    supportFragmentManager.requestContacts(callback)
}

fun FragmentManager.requestContacts(callback: PermissionsCallback) {
    val permissions = arrayOf(
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )
    requestPermissions(permissions, callback)
}

/**
 * 申请位置权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestLocation(callback: PermissionsCallback) {
    supportFragmentManager.requestLocation(callback)
}

fun FragmentManager.requestLocation(callback: PermissionsCallback) {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    requestPermissions(permissions, callback)
}

/**
 * 申请电话权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestPhone(callback: PermissionsCallback) {
    supportFragmentManager.requestPhone(callback)
}

fun FragmentManager.requestPhone(callback: PermissionsCallback) {
    val permissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
    )
    requestPermissions(permissions, callback)
}

/**
 * 申请录音权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestRecordAudio(callback: PermissionsCallback) {
    supportFragmentManager.requestRecordAudio(callback)
}

fun FragmentManager.requestRecordAudio(callback: PermissionsCallback) {
    val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    requestPermissions(permissions, callback)
}

/**
 * 申请传感器权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestSensors(callback: PermissionsCallback) {
    supportFragmentManager.requestSensors(callback)
}

fun FragmentManager.requestSensors(callback: PermissionsCallback) {
    val permissions = arrayOf(Manifest.permission.BODY_SENSORS)
    requestPermissions(permissions, callback)
}

/**
 * 申请短信权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestSMS(callback: PermissionsCallback) {
    supportFragmentManager.requestSMS(callback)
}

fun FragmentManager.requestSMS(callback: PermissionsCallback) {
    val permissions = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS
    )
    requestPermissions(permissions, callback)
}

/**
 * 申请存储空间权限
 *
 * @param callback 回调
 */
fun FragmentActivity.requestStorage(callback: PermissionsCallback) {
    supportFragmentManager.requestStorage(callback)
}

fun FragmentManager.requestStorage(callback: PermissionsCallback) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        )
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    requestPermissions(permissions, callback)
}

fun FragmentActivity.requestMediaImages(callback: PermissionsCallback) {
    supportFragmentManager.requestMediaImages(callback)
}

fun FragmentManager.requestMediaImages(callback: PermissionsCallback) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        )
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    requestPermissions(permissions, callback)
}

fun FragmentActivity.requestMediaVideo(callback: PermissionsCallback) {
    supportFragmentManager.requestMediaVideo(callback)
}

fun FragmentManager.requestMediaVideo(callback: PermissionsCallback) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        )
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    requestPermissions(permissions, callback)
}

fun FragmentActivity.requestMediaAudio(callback: PermissionsCallback) {
    supportFragmentManager.requestMediaAudio(callback)
}

fun FragmentManager.requestMediaAudio(callback: PermissionsCallback) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        )
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
        )
    else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    requestPermissions(permissions, callback)
}

fun FragmentActivity.requestPermissions(permissions: Array<String>, callback: PermissionsCallback) {
    supportFragmentManager.requestPermissions(permissions, callback)
}

fun FragmentManager.requestPermissions(permissions: Array<String>, callback: PermissionsCallback) {
    val tag = PermissionsFragment::class.java.simpleName
    var fragment = findFragmentByTag(tag)
    if (fragment == null) {
        fragment = PermissionsFragment.newInstance()
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.add(fragment, tag)
        fragmentTransaction.commitAllowingStateLoss()
        executePendingTransactions()
    }
    if (fragment is PermissionsFragment) {
        fragment.requestPermissions(permissions, callback)
    }
}

interface PermissionsCallback {
    fun allow()
    fun deny()
}

class PermissionsFragment : Fragment() {

    private var callback: PermissionsCallback? = null
    private var permissions: MutableList<String> = arrayListOf()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { ps ->
            var isGranted = true
            ps.entries.forEach {
                if (it.key in permissions && !it.value)
                    isGranted = false
            }
            if (!isGranted) {
                callback?.deny()
            } else {
                callback?.allow()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    fun requestPermissions(
        permissions: Array<String>,
        callback: PermissionsCallback
    ) {
        this.permissions.clear()
        this.permissions.addAll(permissions)
        this.callback = callback
        if (!hasPermissions(requireContext())) {
            launcher.launch(permissions)
        } else {
            this.callback?.allow()
        }
    }

    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        @JvmStatic
        fun newInstance(): PermissionsFragment {
            return PermissionsFragment()
        }
    }

}