package com.example.codev

data class ResPortFolioList(
    val code: Int,
    val result: ResofPortFolioList
)

data class ResofPortFolioList(
    val Complete: ArrayList<PortFolio>
)

data class PortFolio(
    val co_portfolioId: Int,
    val co_title: String,
    val updatedAt: String
)
