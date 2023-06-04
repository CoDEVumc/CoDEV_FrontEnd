package com.codev.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


@SuppressLint("StaticFieldLeak")
object RetrofitClient {
    private const val BASE_URL = "http://semtle.catholic.ac.kr:8080/codev/"
    var service: RetrofitService
    private lateinit var context: Context

    init {
        val interceptorClient = OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor())
            .addInterceptor(ResponseInterceptor())
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(interceptorClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    fun initialize(context: Context) {
        this.context = context
    }

    internal class RequestInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()
            if (UserSharedPreferences.getUserAccessToken().isNotEmpty()){
                val auth = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())
                builder.header("CoDev_Authorization", auth)
                Log.d("인증", auth)
            }

            return chain.proceed(builder.build())
        }
    }

    internal class ResponseInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            when (response.code()) {
                400 -> {
                    // todo Control Error
                }
                401 -> {
                    // todo Control Error
                    val responseBody = response.body()?.string()
                    val json = responseBody?.let { JSONObject(it) }
                    val code = json?.getInt("code")

                    Log.d("401에러", responseBody.toString())

                    if (code == 444){
                        CoroutineScope(Dispatchers.Main).launch {
                            UserSharedPreferences.refreshAccessToken()
                        }
                        val refreshedRequest = chain.request().newBuilder()
                        refreshedRequest.header("CoDev_Authorization", AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()))
                        return chain.proceed(refreshedRequest.build())
                    }

                    if (code == 445){
                        CoroutineScope(Dispatchers.Main).launch {
                            UserSharedPreferences.clearUser() // 로그아웃
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent) // 로그인 액티비티 실행
                            (context as Activity).finish() // 현재 액티비티 및 상위 액티비티 종료
                        }
                        return chain.proceed(request)
                    }

                    return chain.proceed(request)
                }
                402 -> {
                    // todo Control Error
                }
            }
            return response
        }
    }
}