package com.codev.android.addpage

data class AddProjectData(
    var title: String,
    var content: String,
    //TODO: 사진 전달 구성
    var pmNumber: Int,
    var designNumber: Int,
    var frontNumber: Int,
    var backNumber: Int,
    var etcNumber: Int,
    var stackList: ArrayList<String>, //TODO: 기술스택 전달 구성
    var locationName: String,
    var deadlineString: String
)
