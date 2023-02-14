package com.example.codev

data class ReqApplyProject(
    val co_portfolioId : Int,
    val co_partId : String,
    val co_motivation : String,
    val co_recruitStatus: Boolean,
    val co_writer: String,
    val co_process: String
)
