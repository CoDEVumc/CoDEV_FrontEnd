package com.example.codev

data class ProjectData(
    var ptitle: String,
    var pdday: String,
    var pheart: Boolean,
    var pnum: Int,
    var pcontent: String,
    var pimages: String

    //image 여러개 (json 파싱)
    // ㄴ리사이클러뷰 띄우는거 필요
    //pcontent 여러개 (json 파싱)
)
