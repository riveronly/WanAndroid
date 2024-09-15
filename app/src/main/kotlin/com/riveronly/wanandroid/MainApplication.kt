package com.riveronly.wanandroid

import android.app.Application
import com.riveronly.wanandroid.helper.DataStoreHelper

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        DataStoreHelper.init(app)
    }

    companion object {
        lateinit var app: Application
    }
}