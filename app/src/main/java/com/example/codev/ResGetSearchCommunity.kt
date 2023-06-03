package com.example.codev

import java.util.*
import kotlin.collections.ArrayList

data class ResGetSearchCommunity(
    val code : Int,
    val result: ResponseOfGetSearchCommunity
)
data class ResponseOfGetSearchCommunity(
    val co_page: Int,
    val success: ArrayList<SearchCount>,
    val boards: ArrayList<QIData>
)

data class SearchCount(
    val type: String,
    val boardCount: Int
)