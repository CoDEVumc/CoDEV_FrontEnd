package com.example.codev


import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("user/login")
    fun signIn(@Body params: ReqSignIn) : Call<ResSignIn>

    @POST("user/join")
    fun signUp(@Body params: ReqSignUp) : Call<JsonObject>

    @GET("user/code/mail")
    fun getEmailCode(@Query("email") value1: String) : Call<ResGetEmailCode>

    @POST("project")
    @Multipart
    fun createNewProject(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("project") project: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewProject>

}