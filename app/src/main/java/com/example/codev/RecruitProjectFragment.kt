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
    //lateinit var pdataList: ArrayList<Set<PData>>
    private var sdataList: ArrayList<SData> = ArrayList()

    private var coLocationTag:String = ""
    private var coPartTag:String = ""
    private var coKeyword:String = ""
    private var coProcessTag:String = ""
    private var coSortingTag:String = ""





    var downpage: Int = 0
    var lastPage: Boolean = false

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

        //프로젝트인지 스터딘지 구분해줄려고
        var now : Int = 0 // 0-프로젝트 / 1- 스터디

        pdataList = ArrayList() //초기화
        sdataList = ArrayList()

        loadData_p(downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag) //기본으로 0page PData 가져오기




        //자동페이징 처리 부분
        viewBinding.listviewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1
                if(lastVisibleItemPosition == lastPosition && !lastPage){
                    downpage += 1

                    loadData_p(downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)

                }

            }
        })


        viewBinding.toolbarRecruit.toolbarImg.setOnClickListener {
            downpage = 0
            popupMenu.setOnMenuItemClickListener {
                if(now == 0){ //현재 프로젝트 화면이면
                    sdataList = ArrayList()
                    when (it.itemId) { //프->스
                        R.id.m_study -> {
                            now = 1
                            Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()

                            viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)
                            loadData_s(downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
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
                            loadData_p(downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                            Log.d("test: (0나와야 돼) now : ",now.toString())

                            true
                        }
                        else -> false
                    }
                }
            }
            popupMenu.show()

        }

        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project) //기본으로 logo_project띄워놓기

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
            when(now){ //플 스 검사
                0 -> loadData_p(downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                1 -> loadData_s(downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
            }

        }

        val bottomSheetLoc = BottomSheetLoc()
        //지역 버튼 --> 선택 한거로 바껴있어야 됨(내용&색)
        viewBinding.loc.setOnClickListener {
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }
        viewBinding.filterLoc.setOnClickListener {
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }
        //지역 고르고 적용하기 누르면 -> 그 지역으로 "" 내용 바껴야됨
//        popupLocBinding.btnSelect.setOnClickListener {
//            //loadData_p_loc(0,p_loc)
//            //bottomSheetLoc.dismiss()
//            Log.d("test: ","적용하기 버튼 누름")
//            bottomSheetLoc.dismiss()
//        }


        //분야 버튼 --> 선택 한거로 바껴있어야 됨(내용&색)
        viewBinding.part.setOnClickListener {
            val bottomSheetPart = BottomSheetPart()
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
            //뭐눌렀는지 떠야돼 --> 누른 값 인자로 전달 coPart의 ""안에 넣으면 됨
            viewBinding.loc.text = bottomSheetLoc.loc
        }
        viewBinding.filterPart.setOnClickListener {
            val bottomSheetPart = BottomSheetPart()
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }
        //분야 고르고 적용하기 누르면



        return viewBinding.root
    }



    //전체 프로젝트 조회
    //문제점 : loadData_P() 불러올때 마다 계속 pdataList에 결과를 추가함
    private fun loadData_p(int: Int, coLocationTag:String, coPartTag:String, coKeyword:String, coProcessTag:String, coSortingTag:String) {
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
                                Log.d("test: 플젝 전체 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 플젝 전체 데이터 : ", "\n${it.result.success}")

                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    Log.d("test: success: ", "[] 라서 비어있어용")
                                    lastPage = true
                                }
                                //페이지에 내용이 있으면
                                else {
                                    pdataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setAdapter_p(pdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewMain.adapter!!.notifyDataSetChanged()

                                        // 한 페이지당 게시물이 10개씩 들어있음.
                                        // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                                        //인덱스 범위 알려줌
                                        //변경된 첫번째 아이템 위치 , 변경된 아이템 개수
                                        //loadData(0) -> page = 1 부터 /
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

    //    //전체 스터디 조회
    private fun loadData_s(int: Int, coLocationTag:String, coPartTag:String, coKeyword:String, coProcessTag:String, coSortingTag:String) {
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
                                Log.d("test: 스터디 전체 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 스터디 전체 데이터 :", "\n${it.result.success}")
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    Log.d("test: success: ", "[] 라서 비어있어용")
                                    lastPage = true
                                }
                                //페이지에 내용이 있으면
                                else {
                                    sdataList.addAll(it.result.success)
                                    if(int == 0) {
                                        setAdapter_s(sdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewMain.adapter!!.notifyDataSetChanged()
                                        //viewBinding.listviewMain.adapter!!.notifyItemRangeInserted((downpage-1)*10,10)
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


    private fun setAdapter_p(projectList: ArrayList<PData>){
        val adapter = AdapterProject(projectList, mainAppActivity)
        viewBinding.listviewMain.adapter = adapter
    }

    private fun setAdapter_s(studyList: ArrayList<SData>){
        val adapter = AdapterStudy(studyList, mainAppActivity)
        viewBinding.listviewMain.adapter = adapter
    }








}


//
//    //모집중인 프로젝트만 조회
//    private fun loadData_p_ing(int: Int) {
//        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
//            int,"","","","ING","").enqueue(object: Callback<ResGetProjectList>{
//            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
//                if(response.isSuccessful.not()){
//                    Log.d("test: 조회실패",response.toString())
//                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }else{
//                    when(response.code()){
//                        200->{
//                            response.body()?.let {
//                                Log.d("test: 모집중인 플젝 조회 성공! ", "\n${it.toString()}")
//                                Log.d("test: 모집중 플젝 데이터 : ", "\n${it.result.success}")
//
//                                //페이지가 비어있으면
//                                if(it.result.success.toString() == "[]") {
//                                    Log.d("test: success: ", "[] 라서 비어있어용")
//                                    lastPage = true
//                                }
//                                //페이지에 내용이 있으면
//                                else {
//                                    pdataList.addAll(it.result.success)
//                                    if(int == 0) {
//                                        setAdapter_p(pdataList) //projectAdapter
//                                    }
//                                    else{
//                                        //viewBinding.listviewMain.adapter!!.notifyDataSetChanged()
//                                        viewBinding.listviewMain.adapter!!.notifyItemRangeInserted((downpage-1)*10,10)
//                                    }
//                                }
//                            }
//
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
//                Log.d("test: 조회실패 - RPF > loadData_p_ing(모집중 플젝조회) : ", "[Fail]${t.toString()}")
//                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//
//        })
//
//    }
//    //모집중인 스터디만 조회
//    private fun loadData_s_ing(int: Int) {
//        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
//            int,"","","","ING","").enqueue(object: Callback<ResGetStudyList>{
//            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
//                if(response.isSuccessful.not()){
//                    Log.d("test: 조회실패",response.toString())
//                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }else{
//                    when(response.code()){
//                        200->{
//                            response.body()?.let {
//                                Log.d("test: 모집중인 스터디 조회 성공! ", "\n${it.toString()}")
//                                Log.d("test: 모집중 스터디 데이터 : ", "\n${it.result.success}")
//
//                                //페이지가 비어있으면
//                                if(it.result.success.toString() == "[]") {
//                                    Log.d("test: success: ", "[] 라서 비어있어용")
//                                    lastPage = true
//                                }
//                                //페이지에 내용이 있으면
//                                else {
//                                    sdataList.addAll(it.result.success)
//                                    if(int == 0) {
//                                        setAdapter_s(sdataList) //projectAdapter
//                                    }
//                                    else{
//                                        //viewBinding.listviewMain.adapter!!.notifyDataSetChanged()
//                                        viewBinding.listviewMain.adapter!!.notifyItemRangeInserted((downpage-1)*10,10)
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResGetStudyList>, t: Throwable) {
//                Log.d("test: 조회실패 - RPF > loadData_s_ing(모집중 스터디 조회): ", "[Fail]${t.toString()}")
//                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//    //+지역조건+전체 프로젝트 조회
//    private fun loadData_p_loc(int: Int,p_loc: String) {
//        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
//            int,p_loc,"","","","").enqueue(object: Callback<ResGetProjectList>{
//            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
//                if(response.isSuccessful.not()){
//                    Log.d("test: 조회실패",response.toString())
//                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }else{
//                    when(response.code()){
//                        200->{
//                            response.body()?.let {
//                                Log.d("test: 플젝 전체 조회 성공! ", "\n${it.toString()}")
//                                Log.d("test: 플젝 전체 데이터 : ", "\n${it.result.success}")
//
//                                //페이지가 비어있으면
//                                if(it.result.success.toString() == "[]") {
//                                    Log.d("test: success: ", "[] 라서 비어있어용")
//                                    lastPage = true
//                                }
//                                //페이지에 내용이 있으면
//                                else {
//
//                                    setAdapter_p(it.result.success) //projectAdapter
//                                }
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







//버튼 클릭 시 함수를 호출하는 함수
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        BUTTON.setOnClickListener { //<--프로젝트 버튼
//            (parentFragment as RecruitFragment).r_project()
//        }
//
//        BUTTON.setOnClickListener { //<--스터디 버튼
//            (parentFragment as RecruitFragment).r_study()
//        }
//    }






//private fun showPopup(v: View){
//    val popup = PopupMenu(this,v) //PopupMenu 객체 선언
//    popup.menuInflater.inflate(R.menu.menu_recruit, popup.menu)
//    popup.show() //팝업 보여주기
//}
//
//// 팝업 메뉴 아이템 클릭 시 실행되는 메소드
//override fun onMenuItemClick(item: MenuItem?): Boolean {
//    when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
//        R.id.m_project -> r_project()
//        R.id.m_study -> r_study()
//    }
//
//    return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
//}
//
//
////팝업 선택 메뉴 때문에 추가
//override fun onActivityCreated(savedInstanceState: Bundle?) {
//    super.onActivityCreated(savedInstanceState)
//    view?.findViewById<Button>(R.id.filter_loc)?.setOnClickListener{
//        //dismiss()
//    }
//}
//
//
////프로젝트,스터디 Fragment 전환 함수 r_project, r_study
//fun r_project(){
//    childFragmentManager.beginTransaction()
//        .replace(R.id.frameLayout, projectFragment)
//        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//        .commit()
//}
//
//fun r_study(){
//    childFragmentManager.beginTransaction()
//        .replace(R.id.frameLayout, studyFragment)
//        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//        .commit()
//}

//최하단 도착 시
//                if (!viewBinding.listviewMain.canScrollVertically(1)) { //1 : 최하단, -1 : 최상단
//                    //스크롤 할 수 있으면 true, 못하면 false 반환
//                    //스크롤 못해서 false -> !false --> true >> 스크롤이 마지막에 닿으면
//
//                    Log.d("SCROLL", "last Position...")
//                    //lastPage여부 check
//                    if(lastPage == false){
//                        downpage += 1
//                        loadData_p(downpage)
//                    }
//                    else{ //불러온 페이지에 더이상 값이 없으면
//                        Log.d("SCROLL", "맨 마지막 페이지 도착. ")
//                    }
//                }
//최상단 도착 시
//                if (!viewBinding.listviewMain.canScrollVertically(-1)){ //1 : 최하단, -1 : 최상단
//                    uppage -= 1
//                    if(uppage != -1){ //가장 최상단 0 페이지 제외
//                        loadData_p(uppage)
//                        uppage -= 1
//                    }else{
//                        //가장 최상단 0페이지
//                        Log.d("SCROLL", "맨 위 페이지 도착. ")
//                    }
//
//                }