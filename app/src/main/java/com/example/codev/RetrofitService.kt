package com.example.codev


import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RetrofitService {
    @POST("user/login")
    fun signUp(@Body params: ReqSignUp) : Call<ResSignUp>

    @GET("my-page/portfolio/{coPortfolioId}")
    fun getPortFolio(@Header("CoDev_Authorization") header: String,
                     @Path("coPortfolioId") coPortfolioId: Int) : Call<ResPortFolioList>
}