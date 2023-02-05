package com.example.codev

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface RetrofitService {

    @POST("user/sns/login")
    fun googleSignIn(@Body params: ReqGoogleSignIn) : Call<ResSignIn>

    @POST("user/sns/login")
    fun githubSignIn(@Body params: ReqGithubSignIn) : Call<ResSignIn>


    @POST("user/login")
    fun signIn(@Body params: ReqSignIn): Call<ResSignIn>

    @POST("user/token/refresh")
    fun refreshToken(@Body params: ReqRefreshToken): Call<ResRefreshToken>

    @POST("user/join")
    @Multipart
    fun signUp(@Part("user") user: RequestBody
               ,@Part file: MultipartBody.Part): Call<JsonObject>

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
        ,@Body params: ReqUpdatePF
    ): Call<ResCreateNewPF>

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

    @PATCH("study/heart/{coStudyId}")
    fun requestStudyBookMark(
        @Header("CoDev_Authorization") header: String,
        @Path("coStudyId") coStudyId: Int
    ): Call<JsonObject>

    @GET("my-page/portfolioList")
    fun getPortFolio(@Header("CoDev_Authorization") header: String) : Call<ResPortFolioList>

    @GET("my-page/hearts/projects")
    fun getHeartedProject(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkProjectList>
    @GET("my-page/hearts/studies")
    fun getHeartedStudy(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkStudyList>

    @GET("my-page/recruitment")
    fun getApplyList(
        @Header("CoDev_Authorization") header: String,
        @Query("type") type: String
    ) : Call<ResApplyList>

    @GET("my-page/participation")
    fun getJoinList(
        @Header("CoDev_Authorization") header: String,
        @Query("type") type: String
    ) : Call<ResJoinList>


    @GET("my-page/portfolio/{coPortfolioId}")
    fun getPortFolioDetail(@Header("CoDev_Authorization") authToken: String, @Path("coPortfolioId") id: String) : Call<ResGetPFDetail>

    @DELETE("my-page/portfolio/{coPortfolioId}")
    fun deletePortFolio(@Path("coPortfolioId") coPortfolioId:Int,@Header("CoDev_Authorization") header: String) : Call<ResDeletePortfolio>

    @GET("project/{coProjectId}")
    fun getProjectDetail(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int) : Call<ResGetRecruitDetail>

    @GET("study/{coStudyId}")
    fun getStudyDetail(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int) : Call<ResGetRecruitDetail>

    @DELETE("project/out/{coProjectId}")
    fun deleteProject(@Path("coProjectId") coProjectId:Int,@Header("CoDev_Authorization") header: String) : Call<JsonObject>

    @DELETE("study/{coStudyId}")
    fun deleteStudy(@Path("coStudyId") coStudyId:Int,@Header("CoDev_Authorization") header: String) : Call<JsonObject>

    @POST("project/submission/{coProjectId}")
    fun applyProject(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int, @Body params: ReqApplyProject) : Call<JsonObject>

    @POST("study/submission/{coStudyId}")
    fun applyStudy(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int, @Body params: ReqApplyStudy) : Call<JsonObject>

    @DELETE("project/recruitment/{coProjectId}")
    fun cancelProject(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int) : Call<JsonObject>

    @DELETE("study/recruitment/{coStudyId}")
    fun cancelStudy(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int) : Call<JsonObject>
}