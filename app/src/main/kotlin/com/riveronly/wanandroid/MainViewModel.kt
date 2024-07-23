package com.riveronly.wanandroid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.riveronly.wanandroid.bean.UserInfoBean
import com.riveronly.wanandroid.helper.MMKVHelper
import com.riveronly.wanandroid.net.ApiService
import com.riveronly.wanandroid.net.RetrofitBuilder.LOCAL_TOKEN
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {
    var userInfoRes by mutableStateOf(UserInfoBean())

    /**
     * 登出
     */
    suspend fun fetchLogout() {
        ApiService.logout()
        MMKVHelper.removeKey(LOCAL_TOKEN)
        userInfoRes = UserInfoBean()
    }

    /**
     * 签到
     */
    suspend fun fetchCoin() = flow {
        val coinRes = ApiService.coin()
        emit(coinRes.errorCode == 0)
    }

    /**
     * 个人信息
     */
    suspend fun fetchUserinfo() {
        val userinfo = ApiService.userinfo()
        if (userinfo.errorCode == 0 && userinfo.data != null) {
            userInfoRes = userinfo.data
        }
    }
}