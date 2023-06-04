package com.codev.android

data class ReqCreateNewPF(
    val co_title: String,
    val co_rank: String,
    val co_headLine: String,
    val co_introduction: String,
    val co_languages: List<Int>,
    val co_links: List<String>
    )
