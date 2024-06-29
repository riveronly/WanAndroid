package com.riveronly.wanandroid.net

import com.riveronly.wanandroid.bean.base.BaseResponse
import com.riveronly.wanandroid.utils.MMKVUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private const val BASE_URL = "https://www.wanandroid.com"
    private const val CONNECT_TIMEOUT_SECONDS = 15L // 连接超时时间
    private const val READ_TIMEOUT_SECONDS = 15L // 读取超时时间

    private val retrofit by lazy { initRetrofit() }
    val service: ApiService by lazy { retrofit.create(ApiService::class.java) }

    private fun initRetrofit(): Retrofit {
        val okHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .sslSocketFactory(
                    SSLSocketManager.ssLSocketFactory,
                    SSLSocketManager.trustManager[0]
                )
                .hostnameVerifier(SSLSocketManager.hostnameVerifier)
                .addInterceptor(ResponseHeaderInterceptor())
                .addInterceptor(RequestHeaderInterceptor())
                .addInterceptor(ResponseErrorInterceptor())
                .build()

        val contentType = "application/json; charset=UTF8".toMediaType()
        val jsonConfig = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            coerceInputValues = true
        }
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    /**
     * 请求 Header/cookie统一处理
     */
    class RequestHeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
            MMKVUtil.getStringSet("cookies")?.forEach { cookie ->
                request.addHeader("Cookie", cookie)
            }
            return chain.proceed(request.build())
        }
    }

    /**
     * 响应 cookie持久化保存
     */
    class ResponseHeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())
            val cookies = originalResponse.headers("Set-Cookie").toSet()
            MMKVUtil.put("cookies", cookies)
            return originalResponse
        }
    }

    /**
     * 响应 异常处理
     */
    class ResponseErrorInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful) {
                    return response
                } else {
                    val errorResponse = buildErrorResponse(request, -100, "请求失败")
                    return errorResponse
                }
            } catch (e: Exception) {
                return buildErrorResponse(request, -200, "请求异常")
            }
        }
    }

    /**
     * 响应 异常响应构造
     */
    private fun buildErrorResponse(
        request: Request, errorCode: Int, errorMsg: String
    ): Response {
        val errorResponseBean =
            BaseResponse(errorMsg = errorMsg, errorCode = errorCode, data = null)
        val errorResponseJson = Json.encodeToJsonElement(errorResponseBean).toString()
        val errorBody = errorResponseJson.toResponseBody("application/json".toMediaType())
        return Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(200)
            .message(errorMsg).body(errorBody).build()
    }
}
