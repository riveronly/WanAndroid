package com.riveronly.wanAndroid.bean


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleListBean(
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
        val adminAdd: Boolean = false,
        val apkLink: String = "",
        val audit: Int = 0,
        val author: String = "",
        val canEdit: Boolean = false,
        val chapterId: Int = 0,
        val chapterName: String = "",
        val collect: Boolean = false,
        val courseId: Int = 0,
        val desc: String = "",
        val descMd: String = "",
        val envelopePic: String = "",
        val fresh: Boolean = false,
        val host: String = "",
        val id: Int = 0,
        val isAdminAdd: Boolean = false,
        val link: String = "",
        val niceDate: String = "",
        val niceShareDate: String = "",
        val origin: String = "",
        val prefix: String = "",
        val projectLink: String = "",
        val publishTime: Long = 0,
        val realSuperChapterId: Int = 0,
        val selfVisible: Int = 0,
        val shareDate: Long = 0,
        val shareUser: String = "",
        val superChapterId: Int = 0,
        val superChapterName: String = "",
        val tags: List<Tag> = listOf(),
        val title: String = "",
        val type: Int = 0,
        val userId: Int = 0,
        val visible: Int = 0,
        val zan: Int = 0
    ) {
        @Keep
        @Serializable
        data class Tag(
            val name: String = "",
            val url: String = ""
        )
    }
}