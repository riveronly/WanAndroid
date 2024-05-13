package com.riveronly.wanAndroid.net

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class FlowCallAdapterFactory private constructor(private var isAsync: Boolean) :
    CallAdapter.Factory() {
    companion object {
        /**
         * 同步
         */
        fun create(): FlowCallAdapterFactory = FlowCallAdapterFactory(false)

        /**
         * 异步
         */
        fun createAsync(): FlowCallAdapterFactory = FlowCallAdapterFactory(true)
    }

    override fun get(
        returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        return FlowCallAdapter<Any>(observableType, isAsync)
    }
}

internal class FlowCallAdapter<R>(
    private val responseType: Type, private val isAsync: Boolean
) : CallAdapter<R, Flow<R?>> {

    override fun responseType() = responseType

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun adapt(call: Call<R>): Flow<R?> {
        return callFlow(call, isAsync)
    }

    @ExperimentalCoroutinesApi
    private fun <R> callFlow(call: Call<R>, isAsync: Boolean): Flow<R> {
        val started = AtomicBoolean(false)
        return callbackFlow {
            if (started.compareAndSet(false, true)) {
                if (isAsync) callEnqueueFlow(call) else callExecuteFlow(call)
            }
            awaitClose { call.cancel() }
        }
    }
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<R>.callEnqueueFlow(call: Call<R>) {
    call.enqueue(object : Callback<R> {
        override fun onResponse(call: Call<R>, response: Response<R>) {
            processing(response)
        }

        override fun onFailure(call: Call<R>, throwable: Throwable) {
            cancel(CancellationException(throwable.localizedMessage, throwable))
        }
    })
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<R>.callExecuteFlow(call: Call<R>) {
    try {
        processing(call.execute())
    } catch (throwable: Throwable) {
        cancel(CancellationException(throwable.localizedMessage, throwable))
    }
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<R>.processing(response: Response<R>) {
    if (response.isSuccessful) {
        val body = response.body()
        if (body == null || response.code() == 204) {
            cancel(CancellationException("HTTP status code: ${response.code()}"))
        } else {
            trySendBlocking(body).onSuccess {
                close()
            }.onClosed { throwable ->
                cancel(
                    CancellationException(
                        throwable?.localizedMessage, throwable
                    )
                )
            }.onFailure { throwable ->
                cancel(
                    CancellationException(
                        throwable?.localizedMessage, throwable
                    )
                )
            }
        }
    } else {
        val msg = response.errorBody()?.string()
        cancel(
            CancellationException(
                if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                } ?: "unknown error"
            )
        )
    }
}



