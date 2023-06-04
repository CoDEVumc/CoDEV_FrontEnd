package com.codev.android

import java.util.*
import kotlin.collections.ArrayList

data class ResBookMarkQuestionAndInfoList(
    val code : Int,
    val result: ResponseOfGetBookMarkList1
)
data class ResponseOfGetBookMarkList1(
    val Complete: ArrayList<BookmarkQIData>
)
data class BookmarkQIData(
    val co_qnaId: Int,
    val co_infoId: Int,
    val co_nickname: String,
    val co_email: String,
    val co_viewer: String, //상세화면에서 String으로 받아야 됨 (이메일 받아옴)
    val profileImg: String,
    val co_title: String,
    val content: String,
    val co_mainImg: String,
    val co_likeCount: Int, //스마일 수
    val co_commentCount: Int, //댓글 수
    val commentCount: Int, //얘 어디쓰는애야?
    val co_markCount: Int, //북마크 수
    val co_mark: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val co_photos: String, //상세화면에서는 ArrayList로 받아야 됨
    val co_comment: String ///상세화면에서는 ArrayList로 받아야 됨


)