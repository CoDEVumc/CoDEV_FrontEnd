package com.codev.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codev.android.databinding.ActivityCommunitySearchBinding
import com.codev.android.databinding.ActivityRecruitSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunitySearchActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityCommunitySearchBinding
    private var dataList: ArrayList<QIData> = ArrayList()
    private var lastPage: Boolean = false

    private var searchTag: String = ""
    private var sortingTag: String = ""
    private var coMyBoard: Boolean = false

    private var downPage: Int = 0
    private var now : Int = 0 // 0-질문 / 1- 정보
    private val context = this

    private var qnaLastPage: Boolean = false
    private var infoLastPage: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCommunitySearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarSearch.toolbar5.title = ""

        setSupportActionBar(viewBinding.toolbarSearch.toolbar5)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.toolbarSearch.btnCancel.isGone = true

        viewBinding.categoryQuestion.isSelected = true

        val bottomSheetSort = BottomSheetSort(){
            sortingTag = it // ""이거나 populaRity
            if(sortingTag != "") { //populaRity : 추천순
                viewBinding.sort.text = "추천순"
            }
            else{ //아무것도 없 : 최신순
                viewBinding.sort.text = "최신순"
            }
            if (viewBinding.toolbarSearch.btnSearch.isGone){
                when(now){
                    0 -> {
                        dataList.clear()
                        qnaLastPage = false
                        search()
                    }
                    1 -> {
                        dataList.clear()
                        infoLastPage = false
                        search()
                    }
                }
            }
        }

        viewBinding.categoryInfo.setOnClickListener {
            Log.d("test","클릭")
            viewBinding.categoryQuestion.isSelected = false
            viewBinding.categoryInfo.isSelected = true
            now = 1
            viewBinding.categoryQuestion.setTextColor(getColor(R.color.black_300))
            viewBinding.categoryInfo.setTextColor(getColor(R.color.black_900))
            if (viewBinding.toolbarSearch.btnSearch.isGone){
                qnaLastPage = false
                dataList.clear()
                search()
            }
        }

        viewBinding.categoryQuestion.setOnClickListener {
            Log.d("test","클릭")
            viewBinding.categoryQuestion.isSelected = true
            viewBinding.categoryInfo.isSelected = false
            now = 0
            viewBinding.categoryQuestion.setTextColor(getColor(R.color.black_900))
            viewBinding.categoryInfo.setTextColor(getColor(R.color.black_300))
            if (viewBinding.toolbarSearch.btnSearch.isGone){
                infoLastPage = false
                dataList.clear()
                search()
            }
        }

        viewBinding.sort.setOnClickListener {
            bottomSheetSort.show(supportFragmentManager, bottomSheetSort.tag)
        }
        viewBinding.filterSort.setOnClickListener {
            bottomSheetSort.show(supportFragmentManager, bottomSheetSort.tag)
        }

        viewBinding.toolbarSearch.btnSearch.setOnClickListener {
            Log.d("test","검색")
            search()
            enableCancelBtn(true)
        }

        viewBinding.toolbarSearch.btnCancel.setOnClickListener {
            Log.d("test","검색어 삭제")
            dataList.clear()
            qnaLastPage = false
            infoLastPage = false
            setAdapter(dataList, now)
            viewBinding.rvList.adapter!!.notifyDataSetChanged()
            viewBinding.toolbarSearch.etKeyword.text.clear()
            enableCancelBtn(false)
        }

        viewBinding.myPost.setOnClickListener {
            coMyBoard = viewBinding.myPost.isChecked

            if (viewBinding.toolbarSearch.btnSearch.isGone){
                when(now){
                    0-> {
                        dataList.clear()
                        qnaLastPage = false
                        search()
                    }
                    1->{
                        dataList.clear()
                        infoLastPage = false
                        search()
                    }
                }

            }
        }

        viewBinding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1

                if((lastVisibleItemPosition == lastPosition) && (!qnaLastPage || !infoLastPage)){ //처음에 false
                    downPage += 1
                    when(now){
                        0 -> loadData(context, downPage, searchTag, coMyBoard, sortingTag, "QNA")
                        1 -> loadData(context, downPage, searchTag, coMyBoard, sortingTag, "INFO")
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search(){
        downPage = 0
        searchTag = viewBinding.toolbarSearch.etKeyword.text.toString()
        when(now){
            0 -> loadData(context, downPage, searchTag, coMyBoard, sortingTag, "QNA")
            1 -> loadData(context, downPage, searchTag, coMyBoard, sortingTag, "INFO")
        }
    }

    private fun setAdapter(dataList: ArrayList<QIData>, now: Int){
        if (now == 0){
            val adapter = AdapterCommunityQuestionList(this, dataList)
            viewBinding.rvList.adapter = adapter
        }else{
            val adapter = AdapterCommunityInfoList(this, dataList)
            viewBinding.rvList.adapter = adapter
        }

    }


    private fun enableCancelBtn(boolean: Boolean){
        viewBinding.toolbarSearch.btnSearch.isGone = boolean
        viewBinding.toolbarSearch.btnCancel.isGone = !boolean
    }

    private fun loadData(context: Context, int: Int, searchTag: String, coMyBoard: Boolean, sortingTag: String, type: String){
        RetrofitClient.service.searchCommunityDataList(int, searchTag,sortingTag, coMyBoard, type).enqueue(object: Callback<ResGetSearchCommunity> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ResGetSearchCommunity>,
                response: Response<ResGetSearchCommunity>
            ) {
                if (response.isSuccessful.not()) {
                    Log.d("test: 검색 실패", response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 검색 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 검색 데이터 : ", "\n${it.result.success}")

                                //페이지가 비어있으면
                                if (it.result.boards.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context, "이 글의 끝입니다.", Toast.LENGTH_SHORT).show()
                                    if (type == "QNA"){
                                        qnaLastPage = true
                                    }else{
                                        infoLastPage = true
                                    }
                                    if (int == 0) { //0page
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setAdapter(dataList, now) //projectAdapter
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    dataList.addAll(it.result.boards)
                                    if (int == 0) { //0page
                                        setAdapter(dataList, now) //projectAdapter
                                    } else {
                                        viewBinding.rvList.adapter!!.notifyDataSetChanged()
                                        //viewBinding.listviewMain.adapter!!.notifyItemRangeInserted(downpage*10-1,10)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetSearchCommunity>, t: Throwable) {
                Log.d("test: 검색 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

//class CommunitySearchActivity: AppCompatActivity() {
//    private lateinit var viewBinding: ActivityRecruitSearchBinding
//    private var dataList: ArrayList<QIData> = ArrayList()
//    private var lastPage: Boolean = false
//
//    private var searchTag: String = ""
//    private var sortingTag: String = ""
//    private var coMyBoard: Boolean = false
//    private var type: String = "INFO"
//
//    private var downPage: Int = 0
//    private var now : Int = 0 // 0-질문 / 1- 정보
//    private val context = this
//
//    private val pDataList: ArrayList<PData> = ArrayList()
//    private val sDataList: ArrayList<SData> = ArrayList()
//    private var coKeyword: String = ""
//    private var coProcessTag: String = ""
//    private var coSortingTag: String = ""
//    private var projectLastPage: Boolean = false
//    private var studyLastPage: Boolean = false
//
//    @SuppressLint("NotifyDataSetChanged")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewBinding = ActivityRecruitSearchBinding.inflate(layoutInflater)
//        setContentView(viewBinding.root)
//
//        viewBinding.toolbarSearch.toolbar5.title = ""
//
//        setSupportActionBar(viewBinding.toolbarSearch.toolbar5)
//        supportActionBar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeAsUpIndicator(R.drawable.left2)
//        }
//
//        viewBinding.toolbarSearch.btnCancel.isGone = true
//
//        viewBinding.categoryProject.isSelected = true
//
//        val bottomSheetSort = BottomSheetSort(){
//            coSortingTag = it // ""이거나 populaRity
//            if(coSortingTag != "") { //populaRity : 추천순
//                viewBinding.sort.text = "추천순"
//            }
//            else{ //아무것도 없 : 최신순
//                viewBinding.sort.text = "최신순"
//            }
//            if (viewBinding.toolbarSearch.btnSearch.isGone){
//                when(now){
//                    0 -> {
//                        pDataList.clear()
//                        projectLastPage = false
//                        search()
//                    }
//                    1 -> {
//                        sDataList.clear()
//                        studyLastPage = false
//                        search()
//                    }
//                }
//            }
//        }
//
//        viewBinding.categoryStudy.setOnClickListener {
//            Log.d("test","클릭")
//            viewBinding.categoryProject.isSelected = false
//            viewBinding.categoryStudy.isSelected = true
//            now = 1
//            viewBinding.categoryProject.setTextColor(getColor(R.color.black_300))
//            viewBinding.categoryStudy.setTextColor(getColor(R.color.black_900))
//            if (viewBinding.toolbarSearch.btnSearch.isGone){
//                projectLastPage = false
//                pDataList.clear()
//                search()
//            }
//        }
//
//        viewBinding.categoryProject.setOnClickListener {
//            Log.d("test","클릭")
//            viewBinding.categoryProject.isSelected = true
//            viewBinding.categoryStudy.isSelected = false
//            now = 0
//            viewBinding.categoryProject.setTextColor(getColor(R.color.black_900))
//            viewBinding.categoryStudy.setTextColor(getColor(R.color.black_300))
//            if (viewBinding.toolbarSearch.btnSearch.isGone){
//                studyLastPage = false
//                sDataList.clear()
//                search()
//            }
//        }
//
//        viewBinding.sort.setOnClickListener {
//            bottomSheetSort.show(supportFragmentManager, bottomSheetSort.tag)
//        }
//        viewBinding.filterSort.setOnClickListener {
//            bottomSheetSort.show(supportFragmentManager, bottomSheetSort.tag)
//        }
//
//        viewBinding.toolbarSearch.btnSearch.setOnClickListener {
//            Log.d("test","검색")
//            search()
//            enableCancelBtn(true)
//        }
//
//        viewBinding.toolbarSearch.btnCancel.setOnClickListener {
//            Log.d("test","검색어 삭제")
//            pDataList.clear()
//            sDataList.clear()
//            projectLastPage = false
//            studyLastPage = false
//            setAdapter(pDataList, sDataList, now)
//            viewBinding.rvList.adapter!!.notifyDataSetChanged()
//            enableCancelBtn(false)
//        }
//
//        viewBinding.recruiting.setOnClickListener {
//            coProcessTag = if(viewBinding.recruiting.isChecked){
//                "ING"
//            }else{
//                ""
//            }
//            if (viewBinding.toolbarSearch.btnSearch.isGone){
//                when(now){
//                    0-> {
//                        pDataList.clear()
//                        projectLastPage = false
//                        search()
//                    }
//                    1->{
//                        sDataList.clear()
//                        studyLastPage = false
//                        search()
//                    }
//                }
//
//            }
//        }
//
//        viewBinding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
//                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1
//
//                if((lastVisibleItemPosition == lastPosition) && (!projectLastPage || !studyLastPage)){ //처음에 false
//                    downPage += 1
//                    when(now){
//                        0 -> loadProjectData(context, downPage, coKeyword, coProcessTag, coSortingTag, now)
//                        1 -> loadStudyData(context, downPage, coKeyword, coProcessTag, coSortingTag, now)
//                    }
//                }
//            }
//        })
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            android.R.id.home ->{
//                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun search(){
//        downPage = 0
//        coKeyword = viewBinding.toolbarSearch.etKeyword.text.toString()
//        when(now){
//            0 -> loadProjectData(context, downPage, coKeyword, coProcessTag, coSortingTag, now)
//            1 -> loadStudyData(context, downPage, coKeyword, coProcessTag, coSortingTag, now)
//        }
//    }
//
//    private fun setAdapter(projectList: ArrayList<PData>, studyList: ArrayList<SData>, now: Int){
//        val adapter = AdapterRecruitSearchList(this, projectList, studyList, now)
//        viewBinding.rvList.adapter = adapter
//    }
//
//    private fun loadProjectData(context: Context, downPage: Int, coKeyword:String, coProcessTag:String, coSortingTag: String, now: Int) {
//        Log.d("test","프로젝트")
//        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),
//            downPage, "", "", coKeyword, coProcessTag, coSortingTag).enqueue(object: Callback<ResGetProjectList> {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
//                if(response.isSuccessful.not()){
//                    Log.d("test: 조회실패",response.toString())
//                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }else{
//                    when(response.code()){
//                        200->{
//                            response.body()?.let {
//                                Log.d("test: 플젝 조회 성공! ", "\n${it.toString()}")
//                                Log.d("test: 플젝 데이터 : ", "\n${it.result.success}")
//                                Log.d("test: 매개변수: ",coKeyword)
//                                //페이지가 비어있으면
//                                if(it.result.success.toString() == "[]") {
//                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
//                                    projectLastPage = true
//                                    if(downPage == 0) {
//                                        setAdapter(pDataList, sDataList, now)
//                                    }
//                                }
//                                //페이지에 내용이 있으면
//                                else {
//                                    pDataList.addAll(it.result.success)
//                                    if(downPage == 0) {
//                                        setAdapter(pDataList, sDataList, now)
//                                    }
//                                }
//                                viewBinding.rvList.adapter!!.notifyDataSetChanged()
//                            }
//
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
//                Log.d("test: 조회실패 - RPF > loadData_p(플젝 전체조회): ", "[Fail]${t.toString()}")
//                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun loadStudyData(context: Context, downPage: Int, coKeyword:String, coProcessTag: String,coSortingTag: String, now: Int) {
//        Log.d("test","스터디")
//        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),
//            downPage, "", "", coKeyword, coProcessTag, coSortingTag).enqueue(object: Callback<ResGetStudyList> {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
//                if(response.isSuccessful.not()){
//                    Log.d("test: 조회실패",response.toString())
//                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }else{
//                    when(response.code()){
//                        200->{
//                            response.body()?.let {
//                                Log.d("test: 스터디 조회 성공! ", "\n${it.toString()}")
//                                Log.d("test: 스터디 데이터 :", "\n${it.result.success}")
//                                Log.d("test: 매개변수: ",coKeyword)
//                                //페이지가 비어있으면
//                                //페이지가 비어있으면
//                                if(it.result.success.toString() == "[]") {
//                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
//                                    studyLastPage = true
//                                    if(downPage == 0) {
//                                        setAdapter(pDataList, sDataList, now)
//                                    }
//                                }
//                                //페이지에 내용이 있으면
//                                else {
//                                    sDataList.addAll(it.result.success)
//                                    if(downPage == 0) {
//                                        setAdapter(pDataList, sDataList, now)
//                                    }
//                                }
//                                viewBinding.rvList.adapter!!.notifyDataSetChanged()
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResGetStudyList>, t: Throwable) {
//                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
//                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun enableCancelBtn(boolean: Boolean){
//        viewBinding.toolbarSearch.btnSearch.isGone = boolean
//        viewBinding.toolbarSearch.btnCancel.isGone = !boolean
//    }
//}