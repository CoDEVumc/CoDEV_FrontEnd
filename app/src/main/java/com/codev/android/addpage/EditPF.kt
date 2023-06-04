package com.codev.android.addpage

import java.io.Serializable

data class EditPF(
    val pfId: String,
    val title: String,

    val name: String,
    val birth: String, //생일은 YYYY/MM/DD 형식으로 보내주세면 감사하겠습니다!
    val gender: String, //남자가 아니면 자동으로 여자로 취급

    val level: String, // 직무 능력

    //LinkedHashMap으로 모든 언어의 Id와 이름을 추가해서 보내주세요!, 없으면 null으로 보내주세요
    val languageIdNameMap: LinkedHashMap<Int, String>,
    val intro: String, //나를 표현하는 한 마디
    val content: String,

    val linkListString: String?, //리스트로 이미지 Url를 보내주세요, 없으면 null값으로
) : Serializable
