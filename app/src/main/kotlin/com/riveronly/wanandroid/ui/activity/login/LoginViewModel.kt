package com.riveronly.wanandroid.ui.activity.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.riveronly.wanandroid.net.ApiService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LoginViewModel : ViewModel() {
    var isLogin by mutableStateOf(false)

    /**
     * 登录：获取、保存token
     */
    suspend fun fetchLogin(username: String, password: String) = flow {
        val login = ApiService.login(username, password)
        emit(login.errorCode == 0 && login.data != null)
    }.catch {
        emit(false)
    }
}
