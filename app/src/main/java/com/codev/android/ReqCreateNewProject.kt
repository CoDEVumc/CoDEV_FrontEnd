package com.codev.android

import com.codev.android.addpage.PartNameAndPeople

data class ReqCreateNewProject(
    val co_title: String,
    val co_content: String,
    val co_location: String,
    val co_languages: List<Int>,
    val co_deadLine: String,
    val co_parts: List<PartNameAndPeople>,
)

