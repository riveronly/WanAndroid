package com.riveronly.wanandroid.net

import com.riveronly.wanandroid.bean.*
import retrofit2.http.*

interface ApiService {
    companion object : ApiService by RetrofitBuilder.service

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String, @Field("password") password: String
    ): BaseResponse<LoginBean>

    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): BaseResponse<RegisterBean>

    @GET("/lg/coin/userinfo/json")
    suspend fun coin(): BaseResponse<CoinBean>

    @GET("/user/lg/userinfo/json")
    suspend fun userinfo(): BaseResponse<UserInfoBean>

    @GET("/user/logout/json")
    suspend fun logout(): BaseResponse<String>

    @GET("/banner/json")
    suspend fun banner(): BaseResponse<ArrayList<BannerItemBean>>

    @GET("/article/list/{page}/json")
    suspend fun articleList(
        @Path("page") page: Int
    ): BaseResponse<ArticleListBean>

    @GET("/lg/collect/list/{page}/json")
    suspend fun collectList(
        @Path("page") page: Int
    ): BaseResponse<CollectBean>

    @FormUrlEncoded
    @POST("/lg/uncollect/{id}/json")
    suspend fun unCollectInMine(
        @Path("id") id: Int,
        @Field("originId") originId: Int
    ): BaseResponse<String>

    @POST("/lg/uncollect_originId/{id}/json")
    suspend fun unCollect(
        @Path("id") id: Int,
    ): BaseResponse<String>

    @POST("/lg/collect/{id}/json")
    suspend fun collect(
        @Path("id") id: Int,
    ): BaseResponse<String>

    @GET("/user_article/list/{page}/json")
    suspend fun userArticleList(
        @Path("page") page: Int
    ): BaseResponse<UserArticleBean>

}