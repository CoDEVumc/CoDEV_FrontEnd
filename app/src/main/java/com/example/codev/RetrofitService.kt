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

    @PUT("project/update/{id}")
    @Multipart
    fun updateProject(
        @Path("id") id: String
        ,@Header("CoDev_Authorization") authToken: String
        ,@Part("project") project: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewProject>

    @POST("study")
    @Multipart
    fun createNewStudy(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("study") study: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewStudy>

    @PUT("study/update/{id}")
    @Multipart
    fun updateStudy(
        @Path("id") id: String
        ,@Header("CoDev_Authorization") authToken: String
        ,@Part("study") study: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewStudy>

    @POST("my-page/portfolio")
    fun createNewPF(
        @Header("CoDev_Authorization") authToken: String
        ,@Body params: ReqCreateNewPF
    ): Call<ResCreateNewPF>

    @PATCH("my-page/portfolio/{id}")
    fun updatePF(
        @Path("id") id: String
        ,@Header("CoDev_Authorization") authToken: String
        ,@Body params: ReqCreateNewPF
    ): Call<ResCreateNewPF>

}