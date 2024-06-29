package com.riveronly.wanandroid.bean


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BannerBeanItem(
    val desc: String = "",
    val id: Int = 0,
    val imagePath: String = "",
    val isVisible: Int = 0,
    val order: Int = 0,
    val title: String = "",
    val type: Int = 0,
    val url: String = ""
)
