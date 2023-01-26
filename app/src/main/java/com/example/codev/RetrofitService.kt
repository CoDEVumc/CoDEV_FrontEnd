package com.example.codev

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface RetrofitService {

    @POST("user/login")
    fun signIn(@Body params: ReqSignIn): Call<ResSignIn>

    @POST("user/token/refresh")
    fun refreshToken(@Body params: ReqRefreshToken): Call<ResRefreshToken>

    @POST("user/join")
    fun signUp(@Body params: ReqSignUp): Call<JsonObject>

    @GET("user/code/mail")
    fun getEmailCode(@Query("email") value1: String): Call<ResGetEmailCode>

    @GET("project/projects/{page}")
    fun requestPDataList(
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int, @Query("coLocationTag") coLocationTag: String,
        @Query("coPartTag") coPartTag: String, @Query("coKeyword") coKeyword: String,
        @Query("coProcessTag") coProcessTag: String,
        @Query("coSortingTag") coSortingTag: String
    ): Call<ResGetProjectList>

    @GET("study/studies/{page}")
    fun requestSDataList(
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int, @Query("coLocationTag") coLocationTag: String,
        @Query("coPartTag") coPartTag: String, @Query("coKeyword") coKeyword: String,
        @Query("coProcessTag") coProcessTag: String,
        @Query("coSortingTag") coSortingTag: String
    ): Call<ResGetStudyList>

    @PATCH("project/heart/{coProjectId}")
    fun requestProjectBookMark(
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int
    ): Call<JsonObject>

    @PATCH("project/study/{coStudyId}")
    fun requestStudyBookMark(
        @Header("CoDev_Authorization") header: String,
        @Path("coStudyId") coStudyId: Int
    ): Call<JsonObject>

}
