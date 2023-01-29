package com.example.codev

import java.io.Serializable
import java.lang.reflect.Constructor
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
    val co_name: String,
    val co_gender: String,
    val co_birth: String,
    val co_loginType: String
) : Serializable
