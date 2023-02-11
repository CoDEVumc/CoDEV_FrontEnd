package com.example.codev

data class ResAppliedUserDetail(
    val code: Int,
    val result: AppliedUserDetailMessage
)

data class AppliedUserDetailMessage(
    val message: AllAppliedUserDetailMessage
)

data class AllAppliedUserDetailMessage(
    val co_studyId : Int,
    val co_portfolioId : Int,
    val co_email : String,
    val profileImg : String,
    val co_name : String,
    val co_gender : String,
    val co_birth : String,
    val co_title : String,
    val co_rank : String,
    val co_headLine : String,
    val co_introduction : String,
    val co_motivation : String,
    val co_links : String,
    val co_languages : String,
    val createdAt : String,
    val updatedAt : String
)
