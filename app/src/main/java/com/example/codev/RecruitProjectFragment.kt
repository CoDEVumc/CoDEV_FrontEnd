package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.FragmentRecruitProjectBinding
import com.example.codev.databinding.PopupLocBinding
import com.example.codev.databinding.RecycleRecruitProjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecruitProjectFragment : Fragment() {
    private lateinit var viewBinding: FragmentRecruitProjectBinding

    private var pdataList: ArrayList<PData> = ArrayList()
    private var sdataList: ArrayList<SData> = ArrayList()

    private var coLocationTag:String = ""
    private var coPartTag:String = ""
    private var coKeyword:String = ""
    private var coProcessTag:String = ""
    private var coSortingTag:String = ""

    private var downpage: Int = 0
    private var lastPage: Boolean = false

    private lateinit var mainAppActivity: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRecruitProjectBinding.inflate(layoutInflater)
        var temp = viewBinding.toolbarRecruit.toolbar1
        viewBinding.toolbarRecruit.toolbar1.inflateMenu(R.menu.menu_toolbar_2)
        viewBinding.toolbarRecruit.toolbar1.title = ""
        var popupMenu = PopupMenu(mainAppActivity, temp)
        popupMenu.inflate(R.menu.menu_recruit)
        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project) //기본으로 logo_project띄워놓기

        //지역 태그
        //프 스 비교 해야됨
//        val bottomSheetLoc = BottomSheetLoc(){
//            coLocationTag = it
//            if(coLocationTag != "") {
//                viewBinding.loc.text = it
//                pdataList = ArrayList()
//                sdataList = ArrayList()
//                loadPData(mainAppActivity, downpage,coLocationTag,coPartTag,coKeyword,coProcessTag,coSortingTag)
//            }
//            else{
//                viewBinding.loc.text = "지역"
//                pdataList = ArrayList()
//                sdataList = ArrayList()
//            }
//            Log.d("coLocation: ",coLocationTag)
//        }
//        //분야 태그
//        //프 스 비교 해야됨
//        val bottomSheetPart = BottomSheetPart(){
//            coPartTag = it
//            if(coPartTag != "") {
//                viewBinding.part.text = it
//                pdataList = ArrayList()
//                sdataList = ArrayList()
//            }
//            else{
//                viewBinding.part.text = "분야"
//                pdataList = ArrayList()
//                sdataList = ArrayList()
//            }
//            Log.d("coPart: ",coPartTag)
//        }

        //프로젝트인지 스터딘지 구분해줄려고
        var now : Int = 0 // 0-프로젝트 / 1- 스터디

        pdataList = ArrayList() //초기화
        sdataList = ArrayList()

        loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag) //기본으로 0page PData 가져오기


        //자동페이징 처리 부분
        viewBinding.listviewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1
//                if(lastVisibleItemPosition == lastPosition){
//                    downpage += 1
//                    when(now){ //프/스 분리 해야 됨
//                        0 -> loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
//                        1 -> loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
//                    }
//                }
                if(lastVisibleItemPosition == lastPosition && !lastPage){
                    downpage += 1
                    when(now){ //프/스 분리 해야 됨
                        0 -> loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                        1 -> loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                    }
                }
            }
        })


        //툴바에서 프 <-> 스 전환
        viewBinding.toolbarRecruit.toolbarImg.setOnClickListener {
            popupMenu.setOnMenuItemClickListener {
                downpage = 0
                lastPage = false //없으면 페이지 전환시 무한스크롤 작동x
                // (이유: 스터디 화면에서 페이지 끝까지 닿으면 lastpage = true -> addOnScrollListener의 조건에 의해 더이상 downpage가 증가하지 x)
                // S화면 끝까지 닿았다가 P화면 전환 시 P화면 무한스크롤 x
                if(now == 0){ //현재 프로젝트 화면이면
                    sdataList = ArrayList()
                    when (it.itemId) { //프->스
                        R.id.m_study -> {
                            now = 1
                            Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()
                            viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)
                            loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                            Log.d("test: (1나와야 돼) now : ",now.toString())
                            true
                        }
                        else -> false
                    }
                }
                else { //현재 스터디 화면이면
                    pdataList = ArrayList()
                    when (it.itemId) { //스->프
                        R.id.m_project -> {
                            now = 0
                            Toast.makeText(mainAppActivity, "프로젝트", Toast.LENGTH_SHORT).show()
                            viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)
                            loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                            Log.d("test: (0나와야 돼) now : ",now.toString())
                            true
                        }
                        else -> false
                    }
                }
            }
            popupMenu.show()
        }


        viewBinding.toolbarRecruit.toolbar1.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search ->{
                    Toast.makeText(mainAppActivity, "검색", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_alarm ->{
                    Toast.makeText(mainAppActivity, "알람", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        //모집중만 보기 버튼
        viewBinding.recruitingProjectBtn.setOnClickListener {
            downpage = 0
            if(viewBinding.recruitingProjectBtn.isChecked){
                coProcessTag = "ING"
            }
            else{
                coProcessTag = ""
            }
            when(now){
                0 -> {
                    pdataList = ArrayList()
                    loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                }
                1 -> {
                    sdataList = ArrayList()
                    loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                }
            }
        }

        //분야 고르고 적용하기 누르면
        val bottomSheetLoc = BottomSheetLoc(){
            downpage=0
            coLocationTag = it
            if(coLocationTag != "") {
                viewBinding.loc.text = coLocationTag
            }
            else{
                viewBinding.loc.text = "지역"
            }
            when(now){
                0 -> {
                    pdataList = ArrayList()
                    loadPData(mainAppActivity, downpage,coLocationTag,coPartTag,coKeyword,coProcessTag,coSortingTag)
                }
                1 -> {
                    sdataList = ArrayList()
                    loadSData(mainAppActivity, downpage,coLocationTag,coPartTag,coKeyword,coProcessTag,coSortingTag)
                }
            }
            Log.d("coLocation: ",coLocationTag)
        }
        //분야 태그
        //프 스 비교 해야됨
        val bottomSheetPart = BottomSheetPart(){
            downpage=0
            coPartTag = it
            if(coPartTag != "") {
                viewBinding.part.text = coPartTag
            }
            else{
                viewBinding.part.text = "분야"
            }
            when(now){
                0 -> {
                    pdataList = ArrayList()
                    loadPData(mainAppActivity, downpage,coLocationTag,coPartTag,coKeyword,coProcessTag,coSortingTag)
                }
                1 -> {
                    sdataList = ArrayList()
                    loadSData(mainAppActivity, downpage,coLocationTag,coPartTag,coKeyword,coProcessTag,coSortingTag)
                }
            }
            Log.d("coPart: ",coPartTag)
        }
        //지역 버튼 --> 선택 한거로 바껴있어야 됨(내용&색)
        viewBinding.loc.setOnClickListener {
            downpage = 0
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }
        viewBinding.filterLoc.setOnClickListener {
            downpage = 0
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }

        //분야 버튼 --> 선택 한거로 바껴있어야 됨(내용&색)
        viewBinding.part.setOnClickListener {
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }
        viewBinding.filterPart.setOnClickListener {
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }




        return viewBinding.root
    }

    //전체 프로젝트 조회
    private fun loadPData(context: Context, int: Int, coLocationTag:String, coPartTag:String, coKeyword:String, coProcessTag:String, coSortingTag:String) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag).enqueue(object: Callback<ResGetProjectList>{
            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 플젝 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 플젝 데이터 : ", "\n${it.result.success}")
                                Log.d("test: 매개변수: ",coLocationTag+coPartTag+coKeyword+coProcessTag+coSortingTag)
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    lastPage = true
                                }
                                //페이지에 내용이 있으면
                                else {
                                    pdataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setPAdapter(pdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewMain.adapter!!.notifyDataSetChanged()
                                        //viewBinding.listviewMain.adapter!!.notifyItemRangeInserted(downpage*10-1,10)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_p(플젝 전체조회): ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //전체 스터디 조회
    private fun loadSData(context: Context, int: Int, coLocationTag:String, coPartTag:String, coKeyword:String, coProcessTag:String, coSortingTag:String) {
        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag).enqueue(object: Callback<ResGetStudyList> {
            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 스터디 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 스터디 데이터 :", "\n${it.result.success}")
                                Log.d("test: 매개변수: ",coLocationTag+coPartTag+coKeyword+coProcessTag+coSortingTag)
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    lastPage = true
                                }
                                //페이지에 내용이 있으면
                                else {
                                    sdataList.addAll(it.result.success)
                                    if(int == 0) {
                                        setSAdapter(sdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewMain.adapter!!.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetStudyList>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setPAdapter(projectList: ArrayList<PData>){
        val adapter = AdapterProject(projectList, mainAppActivity)
        viewBinding.listviewMain.adapter = adapter
    }

    private fun setSAdapter(studyList: ArrayList<SData>){
        val adapter = AdapterStudy(studyList, mainAppActivity)
        viewBinding.listviewMain.adapter = adapter
    }
}