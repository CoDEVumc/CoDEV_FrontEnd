package com.codev.android

import java.io.Serializable

data class ResGetPFDetail2(
    val code: Int,
    val result: CompletePF2
)

data class CompletePF2(
    val message: RealDataPF2
)

data class RealDataPF2(
    val co_studyId: Int,
    val co_portfolioId: Int,
    val co_email: String,
    val co_title: String,
    val co_name: String,
    val co_nickName: String,
    val profileImg: String,
    val co_gender: String,
    val co_birth: String,
    val co_rank: String,
    val co_headLine: String,
    val co_introduction: String,
    val co_motivation: String,
    val co_languages: String, //split(",")
    val co_links: String, //split(",")
    //val co_languageList: List<LanguageData>,
    val co_linkList: String,
    val createdAt: String,
    val updatedAt: String,
    val status: Boolean,
)
