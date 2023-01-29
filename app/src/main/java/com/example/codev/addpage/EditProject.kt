package com.example.codev.addpage

data class EditProject(
    val projectId: String,
    val title: String,
    val content: String,
    val imageUrl: List<String>, //리스트로 이미지 Url를 보내주세요, 없으면 null값으로

    // 사람이 없으면 0으로 보내주세요, 감사합니다!
    val pmPeople: Int,
    val designPeople: Int,
    val frontPeople: Int,
    val backPeople: Int,
    val etcPeople: Int,

    //hashMap으로 모든 언어의 Id와 이름을 추가해서 보내주세요!, 없으면 null으로 보내주세요
    val languageIdNameMap: HashMap<Int, String>,
    val location: String,
    val deadLine: String //마감시간은 YYYY-MM-DD 형식으로 보내주세면 감사하겠습니다!
)
