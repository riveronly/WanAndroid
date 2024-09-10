package com.riveronly.wanandroid.bean

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LoginBean(
    val admin: Boolean = false,
    val chapterTops: List<String> = listOf(),
    val coinCount: Int = 0,
    val collectIds: List<Int> = listOf(),
    val email: String = "",
    val icon: String = "",
    val id: Int = 0,
    val nickname: String = "",
    val password: String = "",
    val publicName: String = "",
    val token: String = "",
    val type: Int = 0,
    val username: String = ""
)