package com.example.codev

import java.util.*
import kotlin.collections.ArrayList

data class ResJoinList(
    val code : Int,
    val result: ResponseOfGetJoinList
)
data class ResponseOfGetJoinList(
    val Complete: ArrayList<JoinData>
)
data class JoinData(
    val co_projectId: Int,
    val co_studyId: Int,
    val co_email: String,
    val co_title: String,
    val co_location: String,
    val co_content: String,
    val co_mainImg: String,
    val co_process: String,
    val co_deadLine: String,
    val createdAt: String,
    val updatedAt: String,
    val status: Boolean,
    val co_heartCount: Int,
    var co_heart: Boolean,
    val co_part: String,
    val co_parts: String,
    val co_total: Int,
    val co_languages: String,
    val co_recruitStatus: Boolean,
    val co_photos: String, //확인 필요
    val co_languageList: String, //확인 필요
    val co_applicantList: String //확인 필요


)