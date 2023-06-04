package com.example.codev

import java.util.*
import kotlin.collections.ArrayList

data class ResGetPSList(
    val code : Int,
    val result: ResponseOfGetPS
)
data class ResponseOfGetPS(
    val co_page: Int,
    val Complete: ArrayList<PSData>
)
data class PSData(
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
    val co_parts: String,
    val co_part: String,
    val co_languages: String,
    val co_photos: String, //null <-- data type???
    val co_partList: String, //null <-- data type???
    //partList 받아오면 ,로 슬라이싱 --> 글자 사이 간격 추가 --> textView로 지정 하면 될듯
    val co_languageList: String, //null <-- data type???
    //co_partList랑 마찬가지

    val co_total: Int //총인원
)