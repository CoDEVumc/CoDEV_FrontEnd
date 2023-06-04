package com.codev.android

import java.io.Serializable
import java.sql.Timestamp

data class ResGetRecruitDetail(
    val code:Int,
    val result: ResponseOfGetRecruitDetail
)

data class ResponseOfGetRecruitDetail(
    val Complete: RecruitDetail
)

data class RecruitDetail(
    val co_studyId: Int,
    val co_projectId: Int,
    val co_nickname: String,
    val co_email: String,
    val co_viewer: String,
    val co_title: String,
    val co_location: String,
    val co_content: String,
    val co_mainImg: String,
    val co_process: String,
    val co_deadLine: String,
    val updatedAt: Timestamp,
    val status: Boolean,
    val co_heartCount: Int,
    val co_heart: Boolean,
    val co_part: String,
    val co_total: Int,
    val co_recruitStatus: Boolean,
    val co_partList: ArrayList<RecruitPartLimit>,
    val co_photos: ArrayList<RecruitPhoto>,
    val co_languageList: ArrayList<RecruitLanguage>
)

data class RecruitPartLimit(
    val co_part: String,
    val co_limit: Int
): Serializable

data class RecruitPhoto(
    val co_fileUrl : String
): Serializable

data class RecruitLanguage(
    val co_languageId: Int,
    val co_language: String,
    val co_logo: String
): Serializable
