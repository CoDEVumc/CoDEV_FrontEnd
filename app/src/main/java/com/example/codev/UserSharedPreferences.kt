package com.example.codev

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserSharedPreferences {
    private const val PREFERENCE_NAME : String = "account"
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setLoginType(input: String){
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("loginType", input)
        editor.apply()
    }

    fun getLoginType(): String{
        return sharedPreferences.getString("loginType", "").toString()
    }

    fun setFCMToken(input: String) {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("token", input)
        editor.apply()
    }

    fun getFCMToken(): String {
        return sharedPreferences.getString("token", "").toString()
    }

    fun setKey(key: String){
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("key", key)
        editor.apply()
    }

    fun getKey(): String {
        return sharedPreferences.getString("key", "").toString()
    }

    fun setAutoLogin(input: String) {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("STATE", input)
        editor.apply()
    }

    fun getAutoLogin(): String {
        return sharedPreferences.getString("STATE", "").toString()
    }

    fun setUserAccessToken(input: String) {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("accessToken", input)
        editor.apply()
    }

    fun getUserAccessToken(): String {
        return sharedPreferences.getString("accessToken", "").toString()
    }

    fun setUserRefreshToken(input: String) {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("refreshToken", input)
        editor.apply()
    }

    fun getUserRefreshToken(): String {
        return sharedPreferences.getString("refreshToken", "").toString()
    }

    fun clearUser() {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    suspend fun refreshAccessToken() = withContext(Dispatchers.Default){
        try {
            // Retrofit의 비동기 호출을 suspend 함수로 래핑하여 사용합니다.
            val response = RetrofitClient.service.refreshToken(ReqRefreshToken(AndroidKeyStoreUtil.decrypt(getUserRefreshToken()))).execute()

            if (response.isSuccessful.not()) {
                Log.d("test: 토큰재발급 실패1",response.toString())
            } else {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            setUserAccessToken(AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                            Log.d("test: 토큰재발급 성공", "\n${it.toString()}")
                            Log.d("test: 토큰재발급 성공",AndroidKeyStoreUtil.decrypt(getUserAccessToken()))
                            Log.d("test: 토큰재발급 성공",AndroidKeyStoreUtil.decrypt(getUserRefreshToken()))
                        }
                    }
                    else -> {

                    }
                }
            }

        } catch (t: Throwable) {
            Log.d("test: 토큰재발급 실패2", "[Fail]${t.toString()}")
        }
    }
}