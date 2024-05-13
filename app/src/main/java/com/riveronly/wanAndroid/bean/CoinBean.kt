package com.riveronly.wanAndroid.bean


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CoinBean(
    val coinCount: Int = 0,
    val level: Int = 0,
    val nickname: String = "",
    val rank: String = "",
    val userId: Int = 0,
    val username: String = ""
)