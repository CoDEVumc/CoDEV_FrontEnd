package com.codev.android

import java.sql.Timestamp

data class ResGetQnaDetail(
    val code:Int,
    val result: ResponseOfGetQnaDetail
)

data class ResponseOfGetQnaDetail(
    val Complete: QnaDetail
)

data class QnaDetail(
    val co_qnaId: Int,
    val co_email: String,
    val profileImg: String,
    val viewerImg: String,
    val co_viewer: String,
    val co_nickname: String,
    val co_title: String,
    val content: String,
    val co_mainImg: String,
    val co_likeCount: Int,
    val co_commentCount: Int,
    val co_markCount: Int,
    val co_mark: Boolean,
    val co_like: Boolean,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val co_photos: ArrayList<QnaDetailPhoto>,
    val co_comment: ArrayList<QnaDetailComment>
)

data class QnaDetailPhoto(
    val co_photoId:Int,
    val co_targetId:String,
    val co_type: String,
    val co_uuId: String,
    val co_fileName: String,
    val co_filePath: String,
    val co_fileUrl: String,
    val co_fileDownloadPath: String,
    val co_fileSize: Int,
    val createdAt: Timestamp
)

data class QnaDetailComment(
    val co_coqb: Int,
    val co_email:String,
    val co_nickname:String,
    val profileImg: String,
    val co_qnaId:Int,
    val content:String,
    val createdAt: Timestamp,
    val coReCommentOfQnaBoardList: ArrayList<QnaDetailChildComment>
)

data class QnaDetailChildComment(
    val co_rcoqb:Int,
    val co_email:String,
    val profileImg: String,
    val co_nickname: String,
    val co_coqb: Int,
    val content: String,
    val createdAt: Timestamp
)
