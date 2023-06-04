package com.codev.android

import java.io.Serializable

data class ResGetPFDetail(
    val code: Int,
    val result: CompletePF
)

data class CompletePF(
    val Complete: RealDataPF
)

data class RealDataPF(
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
    val co_languages: String, //split(",")
    val co_links: String, //split(",")
    val co_languageList: List<LanguageData>,
    val co_linkList: String,
    val createdAt: String,
    val updatedAt: String,
    val status: Boolean,
)

data class LanguageData(
    val co_languageId: Int,
    val co_language: String,
    val co_logo: String,
    val createdAt: String,
    val updatedAt: String
)
