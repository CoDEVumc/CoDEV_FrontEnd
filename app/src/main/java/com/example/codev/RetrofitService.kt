package com.example.codev

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("login")
    fun signUp(@Body params: HashMap<String, Any>) : Call<response>


}