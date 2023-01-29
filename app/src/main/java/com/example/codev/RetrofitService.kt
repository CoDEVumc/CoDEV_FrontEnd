package com.example.codev


import com.google.gson.JsonObject
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

interface RetrofitService {
    @POST("user/login")
    fun signIn(@Body params: ReqSignIn) : Call<ResSignIn>

    @POST("user/token/refresh")
    fun refreshToken(@Body params: ReqRefreshToken) : Call<ResRefreshToken>

    @POST("user/join")
    fun signUp(@Body params: ReqSignUp) : Call<JsonObject>

    @GET("user/code/mail")
    fun getEmailCode(@Query("email") value1: String) : Call<ResGetEmailCode>

    @POST("user/sns/login")
    fun googleSignIn(@Body params: ReqGoogleSignIn) : Call<ResSignIn>

    @POST("user/sns/login")
    fun githubSignIn(@Body params: ReqGithubSignIn) : Call<ResSignIn>

    @GET("my-page/portfolioList")
    fun getPortFolio(@Header("CoDev_Authorization") header: String) : Call<ResPortFolioList>

    @DELETE("my-page/portfolio/{coPortfolioId}")
    fun deletePortFolio(@Path("coPortfolioId") coPortfolioId:Int,@Header("CoDev_Authorization") header: String) : Call<ResDeletePortfolio>
}