package com.riveronly.wanandroid.ui.activity.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.riveronly.wanandroid.net.ApiService

class LoginViewModel : ViewModel() {
    var isLogin by mutableStateOf(false)

    /**
     * 登录：获取、保存token
     */
    suspend fun fetchLogin(username: String, password: String) {
        val login = ApiService.login(username, password)
        isLogin = login.errorCode == 0 && login.data != null
    }
}
