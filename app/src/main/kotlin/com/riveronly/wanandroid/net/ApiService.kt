package com.riveronly.wanandroid.net

import com.riveronly.wanandroid.bean.ArticleListBean
import com.riveronly.wanandroid.bean.BannerItemBean
import com.riveronly.wanandroid.bean.CoinBean
import com.riveronly.wanandroid.bean.LoginBean
import com.riveronly.wanandroid.bean.RegisterBean
import com.riveronly.wanandroid.bean.UserInfoBean
import com.riveronly.wanandroid.bean.base.BaseResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
    suspend fun articleList(@Path("page") page: Int): BaseResponse<ArticleListBean>

}