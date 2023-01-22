package com.example.codev

import com.google.gson.annotations.SerializedName

data class TestItem(
    @SerializedName("code")
    val code : Int,

    @SerializedName("result")
    val result: List<Success>

)

data class Successssss(
    @SerializedName("success")
    val projectData: List<ResRecruitP>
)
