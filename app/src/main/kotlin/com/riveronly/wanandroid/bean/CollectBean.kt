package com.riveronly.wanandroid.bean


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CollectBean(
    val curPage: Int = 0,
    val datas: List<Data> = listOf(),
    val offset: Int = 0,
    val over: Boolean = false,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0
) {
    @Keep
    @Serializable
    data class Data(
        val author: String = "",
        val chapterId: Int = 0,
        val chapterName: String = "",
        val courseId: Int = 0,
        val desc: String = "",
        val envelopePic: String = "",
        val id: Int = 0,
        val link: String = "",
        val niceDate: String = "",
        val origin: String = "",
        val originId: Int = 0,
        val publishTime: Long = 0,
        val title: String = "",
        val userId: Int = 0,
        val visible: Int = 0,
        val zan: Int = 0
    )
}