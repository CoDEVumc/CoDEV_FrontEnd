package com.example.codev

import java.sql.Timestamp

data class ResPortFolioList(
    val code: Int,
    val result: ResofPortFolioList
)

data class ResofPortFolioList(
    val Complete: Userinfo,
    val Portfolio: ArrayList<PortFolio>
)

data class PortFolio(
    val co_portfolioId: Int,
    val co_title: String,
    val updatedAt: Timestamp
)

data class Userinfo(
    val co_nickName: String,
    val profileImg: String,
    val co_email: String,
    val co_name: String
)
