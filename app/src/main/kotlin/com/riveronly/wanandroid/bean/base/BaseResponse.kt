package com.riveronly.wanandroid.bean.base

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BaseResponse<T>(
    val errorCode: Int = 0,
    val errorMsg: String = "",
    val data: T? = null
)