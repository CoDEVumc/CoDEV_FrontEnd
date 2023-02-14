package com.example.codev

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class ResApplyerList(
    val code : Int,
    val result: ResponseOfGetApplyerList
)
data class ResponseOfGetApplyerList(
    val message: ApplyerData
)
data class ApplyerData(
    val co_part: String,
    val co_tempSavedApplicantsCount: Int, //선택한 지원자 전체 수 (헤더의 getItemCount)
    val co_applicantsCount: ArrayList<ApplicantData>, //파트별 지원자 수 (토글 정보)
    val co_appllicantsInfo: ArrayList<ApplicantInfoData> //파트별 지원자 정보 (바디 정보 :프로필+포트폴리오)
)
data class ApplicantData( //파트별 지원자 수 (토글 정보)
    val co_part: String,
    val co_limit: Int, //모집 제한 인원
    val co_applicantsCount: Int //선택한 지원자 수
)
data class ApplicantInfoData( //파트별 지원자 정보 (프로필+포트폴리오)
    val co_portfolioId: Int,
    val co_email: String,
    val co_title: String,
    val co_name: String,
    val profileImg: String,
    val co_part: String,
    val co_temporaryStorage: Boolean, //선택된 여부
    val createdAt: String
): Serializable