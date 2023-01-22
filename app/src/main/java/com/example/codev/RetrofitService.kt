package com.example.codev

import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("user/login")
    fun signUp(@Body params: ReqSignUp): Call<ResSignUp>

    @GET("project/projects/{page}")
    fun requestPDataList(
        @Header("CoDev_Authorization") header: String = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsImlhdCI6MTY3NDI5MzY3NSwiZXhwIjoxNjc0NDY2NDc1fQ.4-My4vE-zJRrHucOIY0_bWPJB3N6uhVZqChs8nztmZ40e22L8B0ym0yV3EQwMk",
        @Path("page") page: Int, @Query("coLocationTag") coLocationTag: String,
        @Query("coPartTag") coPartTag: String, @Query("coKeyword") coKeyword: String,
        @Query("coProcessTag") coProcessTag: String
    ): Call<ResGetProjectList>

    @GET("study/studies/{page}")
    fun requestSDataList(
        @Header("CoDev_Authorization") header: String = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsImlhdCI6MTY3NDI5MzY3NSwiZXhwIjoxNjc0NDY2NDc1fQ.4-My4vE-zJRrHucOIY0_bWPJB3N6uhVZqChs8nztmZ40e22L8B0ym0yV3EQwMk",
        @Path("page") page: Int, @Query("coLocationTag") coLocationTag: String,
        @Query("coPartTag") coPartTag: String, @Query("coKeyword") coKeyword: String,
        @Query("coProcessTag") coProcessTag: String
    ): Call<ResGetStudyList>


}