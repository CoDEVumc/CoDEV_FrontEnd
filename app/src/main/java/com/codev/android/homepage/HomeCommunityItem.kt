package com.codev.android.homepage

data class HomeCommunityItem(
    val title: String,
    val imgUrl: String?,
    val time: String,
    val likeNumber: Int,
    val commentNumber: Int,
    val bookedNumber: Int
)
