package com.example.codev

data class ResPortFolioList(
    val code: Int,
    val result: ArrayList<PortFolio>
)

data class PortFolio(
    val co_title: String,
    val updatedAt: String
)
