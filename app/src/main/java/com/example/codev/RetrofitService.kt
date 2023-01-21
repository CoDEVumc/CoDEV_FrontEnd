package com.example.codev


import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface RetrofitService {
    @POST("codev/user/login")
    fun signUp(@Body params: ReqSignUp): Call<ResSignUp>

    @POST("codev/project")
    @Multipart
    fun createNewProject(
        @Header("Authorization") authToken: String
        ,@Part("project") project: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewProject>

}