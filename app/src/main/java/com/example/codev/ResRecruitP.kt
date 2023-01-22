package com.example.codev

import com.google.gson.annotations.SerializedName

data class ResRecruitP(
    @SerializedName("co_projectId")
    val co_projectId: Int,

    @SerializedName("co_email")
    val co_email: String,

    @SerializedName("co_title")
    val co_title: String,

    @SerializedName("co_location")
    val co_location: String,

    @SerializedName("co_content")
    val co_content: String,

    @SerializedName("co_mainImg")
    val co_mainImg: String,

    @SerializedName("co_process")
    val co_process: String,

    @SerializedName("co_deadLine")
    val co_deadLine: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("status")
    val status: Boolean,

    @SerializedName("co_heartCount")
    val co_heartCount: Int,

    @SerializedName("co_heart")
    val co_heart: Boolean,

    @SerializedName("co_parts")
    val co_parts: String,

    @SerializedName("co_languages")
    val co_languages: String,

    @SerializedName("co_photos")
    val co_photos: String,

    @SerializedName("co_partList")
    val co_partList: String,

    @SerializedName("co_languageList")
    val co_languageList: String,

    @SerializedName("co_totalnum")
    val co_totalnum: String,
)
