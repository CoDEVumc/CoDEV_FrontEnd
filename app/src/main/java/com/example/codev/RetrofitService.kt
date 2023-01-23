package com.example.codev


import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @POST("user/login")
    fun signIn(@Body params: ReqSignIn) : Call<ResSignIn>

    @POST("user/join")
    fun signUp(@Body params: ReqSignUp) : Call<JsonObject>

    @GET("user/code/mail")
    fun getEmailCode(@Query("email") value1: String) : Call<ResGetEmailCode>
}