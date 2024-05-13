package com.riveronly.wanAndroid

import android.app.Application
import com.tencent.mmkv.MMKV

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        MMKV.initialize(app)
    }

    companion object {
        lateinit var app: Application
    }
}