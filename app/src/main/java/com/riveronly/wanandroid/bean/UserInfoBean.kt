package com.riveronly.wanandroid.bean

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserInfoBean(
    val userInfo: LoginBean = LoginBean(),
    val coinInfo: CoinBean = CoinBean(),
)
