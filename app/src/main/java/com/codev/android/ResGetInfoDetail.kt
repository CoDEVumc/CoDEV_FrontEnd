package com.codev.android

import java.io.Serializable
import java.sql.Timestamp

data class ResGetInfoDetail(
    val code:Int,
    val result: ResponseOfGetInfoDetail
)

data class ResponseOfGetInfoDetail(
    val Complete: InfotDetail
)

data class InfotDetail(
    val co_infoId: Int,
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
    val co_photos: ArrayList<InfoDetailPhoto>,
    val co_comment: ArrayList<InfoDetailComment>
)

data class InfoDetailPhoto(
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

data class InfoDetailComment(
    val co_coib: Int,
    val co_email:String,
    val co_nickname:String,
    val profileImg: String,
    val co_infoId:Int,
    val content:String,
    val createdAt: Timestamp,
    val coReCommentOfInfoBoardList: ArrayList<InfoDetailChildComment>
)

data class InfoDetailChildComment(
    val co_rcoib:Int,
    val co_email:String,
    val profileImg: String,
    val co_nickname: String,
    val co_coib: Int,
    val content: String,
    val createdAt: Timestamp
)
