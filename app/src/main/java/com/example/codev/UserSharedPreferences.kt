package com.example.codev

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserSharedPreferences {
    private const val ACCOUNT : String = "account"

    fun setFCMToken(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("token", input)
        editor.commit()
    }

    fun getFCMToken(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("token", "").toString()
    }

    fun setKey(context: Context, key: String){
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("key", key)
        editor.commit()
    }

    fun getKey(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("key", "").toString()
    }

    fun setAutoLogin(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("STATE", input)
        editor.commit()
    }

    fun getAutoLogin(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("STATE", "").toString()
    }

    fun setUserAccessToken(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("accessToken", input)
        editor.commit()
    }

    fun getUserAccessToken(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("accessToken", "").toString()
    }

    fun setUserRefreshToken(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("refreshToken", input)
        editor.commit()
    }

    fun getUserRefreshToken(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("refreshToken", "").toString()
    }

    fun clearUser(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }

    fun refreshAccessToken(context: Context){
        RetrofitClient.service.refreshToken(ReqRefreshToken(AndroidKeyStoreUtil.decrypt(getUserRefreshToken(context)))).enqueue(object:
            Callback<ResRefreshToken> {
            override fun onResponse(call: Call<ResRefreshToken>, response: Response<ResRefreshToken>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 토큰재발급 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else{
                    when(response.code()){
                        200->{
                            // 토큰 암호화
                            response.body()?.let {
                                setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                                Log.d("test: 토큰재발급 성공", "\n${it.toString()}")
                                Log.d("test: 토큰재발급 성공",AndroidKeyStoreUtil.decrypt(getUserAccessToken(context)))
                                Log.d("test: 토큰재발급 성공",AndroidKeyStoreUtil.decrypt(getUserRefreshToken(context)))
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResRefreshToken>, t: Throwable) {
                Log.d("test: 토큰재발급 실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}