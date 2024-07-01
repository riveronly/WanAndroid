package com.riveronly.wanandroid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riveronly.wanandroid.bean.CoinBean
import com.riveronly.wanandroid.bean.LoginBean
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.utils.MMKVUtil
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var userInfoRes by mutableStateOf(LoginBean())
    var coinRes by mutableStateOf(CoinBean())
    var isLogin by mutableStateOf(false)

    init {
        val username = MMKVUtil.getString("username")
        val password = MMKVUtil.getString("password")
        if (username.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch {
                fetchLogin(username, password)
            }
        }
    }

    /**
     * 登入
     */
    suspend fun fetchLogin(username: String, password: String) {
        val login = ApiService.login(username, password)
        if (login.errorCode == 0) {
            if (login.data != null) {
                userInfoRes = login.data
                isLogin = true
                fetchCoin()
            }
        }
    }

    /**
     * 登出
     */
    suspend fun fetchLogout() {
        val logout = ApiService.logout()
        userInfoRes = LoginBean()
        isLogin = false
    }

    /**
     * 签到
     */
    suspend fun fetchCoin() {
        val coin = ApiService.coin()
        if (coin.errorCode == 0) {
            if (coin.data != null) {
                coinRes = coin.data
            }
        }
    }

}