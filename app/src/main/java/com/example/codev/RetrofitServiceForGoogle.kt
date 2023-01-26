package com.example.codev


import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitServiceForGoogle {
    @GET("")
    fun googleSignIn() : Call<ResGoogle>
}