package com.example.codev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.FragmentRecruitListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecruitListFragment : Fragment() {
    private lateinit var viewBinding: FragmentRecruitListBinding

    private var pdataList: ArrayList<PData> = ArrayList()
    private var sdataList: ArrayList<SData> = ArrayList()

    private var coLocationTag:String = ""
    private var coPartTag:String = ""
    private var coKeyword:String = ""
    private var coProcessTag:String = ""
    private var coSortingTag:String = ""

    private var downpage: Int = 0
    private var lastPage: Boolean = false
    private var now : Int = 0 // 0-프로젝트 / 1- 스터디

    private var write : String = ""

    private lateinit var mainAppActivity: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()

        pdataList = ArrayList() //초기화
        sdataList = ArrayList()

        when (now){
            0 -> loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag) //기본으로 0page PData 가져오기
            1 -> loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag) //기본으로 0page PData 가져오기
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRecruitListBinding.inflate(layoutInflater)
        var temp = viewBinding.toolbarRecruit.toolbar1
        //viewBinding.toolbarRecruit.toolbar1.inflateMenu(R.menu.menu_toolbar_2)
        viewBinding.toolbarRecruit.toolbar1.inflateMenu(R.menu.menu_toolbar_4)
        viewBinding.toolbarRecruit.toolbar1.title = ""
        var popupMenu = PopupMenu(mainAppActivity, temp)
        popupMenu.inflate(R.menu.menu_recruit)
        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project) //기본으로 logo_project띄워놓기


//        pdataList = ArrayList() //초기화
//        sdataList = ArrayList()

        //loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag) //기본으로 0page PData 가져오기


        //자동페이징 처리 부분
        viewBinding.listviewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1

                if((lastVisibleItemPosition == lastPosition) && !lastPage){ //처음에 false
                    downpage += 1
                    when(now){ //프/스 분리 해야 됨
                        0 -> loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                        1 -> loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                    }
                }
            }
        })


        //툴바에서 프 <-> 스 전환
        var chg: Int
        val popupChangeFragment = PopupChangeFragment(){
            chg = it //누른게 넘어와
            Log.d("chg", chg.toString())
            Log.d("test : 0이면 플젝버튼누름 1이면 스터디버튼누름 :", now.toString())

            downpage = 0
            lastPage = false //없으면 페이지 전환시 무한스크롤 작동 x
            // (이유: 스터디 화면에서 페이지 끝까지 닿으면 lastpage = true -> addOnScrollListener의 조건에 의해 더이상 downpage가 증가하지 x)
            // S화면 끝까지 닿았다가 P화면 전환 시 P화면 무한스크롤 x
            when(chg){
                0 -> { //0 넘어옴 스->프
                    Log.d("여기  0", "onCreateView: ")
                    pdataList = ArrayList()
                    sdataList = ArrayList()
                    now = 0
                    Toast.makeText(mainAppActivity, "프로젝트", Toast.LENGTH_SHORT).show()
                    viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)
                    loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                    Log.d("test: (0나와야 돼) now : ",now.toString())
                    true
                }
                1 -> { //1넘어옴 프->스
                    Log.d("여기  1", "onCreateView: ")
                    pdataList = ArrayList()
                    sdataList = ArrayList()
                    now = 1
                    Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()
                    viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)
                    loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
                    Log.d("test: (1나와야 돼) now : ",now.toString())
                    true
                }
            }
        }
        viewBinding.toolbarRecruit.toolbarImg.setOnClickListener {
            popupChangeFragment.show(childFragmentManager, popupChangeFragment.tag)
        }

        //알림 (알람) 버튼에 popup sample 프래그먼트 연결
        var clicked: String = ""
        val popupSampleFragment = PopupSampleFragment(){
            clicked = it
            Log.d("test : ", clicked+"버튼 누름")
            when(clicked){
                "yes" -> {
                    Toast.makeText(mainAppActivity, "계속하기 누름", Toast.LENGTH_SHORT).show()
                }
                "no" -> {
                    Toast.makeText(mainAppActivity, "취소하기 누름", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewBinding.toolbarRecruit.toolbar1.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search ->{
                    Toast.makeText(mainAppActivity, "검색", Toast.LENGTH_SHORT).show()
                    val intent = Intent(mainAppActivity, RecruitSearchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_bookmark ->{
                    Toast.makeText(mainAppActivity, "북마크", Toast.LENGTH_SHORT).show()
                    when(now) {
                        0 -> {
                            val intent = Intent(mainAppActivity, MyBookMarkActivity::class.java)
                            intent.putExtra("now", now)
                            startActivity(intent)
                        }
                        1 -> {
                            val intent = Intent(mainAppActivity, MyBookMarkActivity::class.java)
                            intent.putExtra("now", now)
                            startActivity(intent)
                        }
                    }
                    true
                }
                R.id.menu_alarm ->{
                    Toast.makeText(mainAppActivity, "알람", Toast.LENGTH_SHORT).show()
                    popupSampleFragment.show(childFragmentManager, popupSampleFragment.tag)
                    true
                }
                else -> false
            }
        }

        //모집중만 보기 버튼
        viewBinding.recruitingProjectBtn.setOnClickListener {
            //필터링 다른거 적용이 이중으로 안돼
            downpage = 0
            lastPage = false
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

        //지역,분야 고르고 적용하기 누르면
        val bottomSheetLoc = BottomSheetLoc(){ //now 값이 뭔지 알아야돼서 여기에 선언 해야 돼용
            downpage=0
            lastPage = false
            coLocationTag = it
            if(coLocationTag != "") {
                viewBinding.loc.text = coLocationTag
                viewBinding.loc.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.green_900))
            }
            else{
                viewBinding.loc.text = "지역"
                viewBinding.loc.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.black_500))
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

        val bottomSheetPart = BottomSheetPart(){
            downpage=0
            lastPage = false
            coPartTag = it
            if(coPartTag != "") {
                viewBinding.part.text = coPartTag
                viewBinding.part.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.green_900))

            }
            else{
                viewBinding.part.text = "분야"
                viewBinding.part.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.black_500))
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

        //정렬 누르고 최신순or추천순 선택하면
        val bottomSheetSort = BottomSheetSort(){
            downpage = 0
            lastPage = false
            coSortingTag = it // ""이거나 populaRity
            if(coSortingTag != "") { //populaRity : 추천순
                viewBinding.sort.text = "추천순"
            }
            else{ //아무것도 없 : 최신순
                viewBinding.sort.text = "최신순"
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
            Log.d("coSortingTag: ",coSortingTag)
        }

        //작성버튼 누르면 작성페이지로 이동
        val bottomSheetWrite = BottomSheetWrite(){
            write = it
            Log.d("test :", write+" 버튼 누름")
        }

        //지역 버튼 --> 선택 한거로 바껴있어야 됨(내용&색)
        viewBinding.loc.setOnClickListener {
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }
        viewBinding.filterLoc.setOnClickListener {
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }

        //분야 버튼 --> 선택 한거로 바껴있어야 됨(내용&색)
        viewBinding.part.setOnClickListener {
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }
        viewBinding.filterPart.setOnClickListener {
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }

        //정렬 버튼
        viewBinding.sort.setOnClickListener {
            bottomSheetSort.show(childFragmentManager, bottomSheetSort.tag)
        }
        viewBinding.filterSort.setOnClickListener {
            bottomSheetSort.show(childFragmentManager, bottomSheetSort.tag)
        }


        //플로팅 작성버튼
        viewBinding.floatingActionButton.setOnClickListener {
            bottomSheetWrite.show(childFragmentManager, bottomSheetWrite.tag)
        }




        return viewBinding.root
    }

    //전체 프로젝트 조회
    private fun loadPData(context: Context, int: Int, coLocationTag:String, coPartTag:String, coKeyword:String, coProcessTag:String, coSortingTag:String) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),
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
                                    if(int == 0) { //0page
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setPAdapter(pdataList) //projectAdapter
                                    }
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
        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),
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
                                    if(int == 0) {
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setSAdapter(sdataList) //projectAdapter
                                    }
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
        val adapter = AdapterRecruitProjectList(mainAppActivity,projectList)
        viewBinding.listviewMain.adapter = adapter
    }

    private fun setSAdapter(studyList: ArrayList<SData>){
        val adapter = AdapterRecruitStudyList(mainAppActivity,studyList)
        viewBinding.listviewMain.adapter = adapter
    }
}