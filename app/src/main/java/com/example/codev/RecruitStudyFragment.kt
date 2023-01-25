package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import com.example.codev.databinding.FragmentRecruitStudyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecruitStudyFragment : Fragment(){//, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewBinding: FragmentRecruitStudyBinding
    private var dataList: ArrayList<SData> = ArrayList()

    var mainAppActivity: MainAppActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentRecruitStudyBinding.inflate(layoutInflater)


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRecruitStudyBinding.inflate(layoutInflater)
        var temp = viewBinding.toolbarRecruit.toolbar1
        viewBinding.toolbarRecruit.toolbar1.inflateMenu(R.menu.menu_recruit_study)
        viewBinding.toolbarRecruit.toolbar1.title = ""
        viewBinding.toolbarRecruit.toolbar1.setOnClickListener {
            var popupMenu = PopupMenu(mainAppActivity, temp)
            popupMenu.inflate(R.menu.menu_recruit_study)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.m_project -> {
                        Toast.makeText(mainAppActivity, "프로젝트", Toast.LENGTH_SHORT).show()
                        //(parentFragment as RecruitFragment).r_project()
                        true
                    }
                    R.id.m_study -> {
                        Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()
                        //(parentFragment as RecruitFragment).r_study()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)

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

        //모집중 버튼
        viewBinding.recruitingStudyBtn.setOnClickListener {
            //
            loadData_s_ing(0)
        }

        loadData()

        return viewBinding.root
    }

    //모집중인 스터디만 조회
    private fun loadData_s_ing(int: Int) {
        //Retrofit 사용하기
        //1. retrofit 객체 만들기 <-- RetrofitClinet에서 함

        //2. api 호출하기
        RetrofitClient.service.requestPDataList("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsImlhdCI6MTY3NDU2MTk5NSwiZXhwIjoxNjc0NzM0Nzk1fQ.pcLLCZxM4_9MQoDqqn2KUvpuSBCm-B6yjbr2TLx1USw",
            int,"","","","ING","").enqueue(object: Callback<ResGetProjectList>{
            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 조회 성공", "\n${it.toString()}")
                                Log.d("test: 조회 성공!!!!!!", "\n${it.result.success}")

                                //dataList = it.result.success
                                //Log.d("test: 조회 성공 dataList ", "\n${dataList}")

                                //val adapter = ProjectAdapter(it.result.success)
                                //viewBinding.listviewMain.adapter = adapter

                                setAdapter_p(it.result.success) //projectAdapter
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
                Log.d("test: 조회실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun setAdapter_p(projectList: ArrayList<PData>){
        //val adapter = ProjectAdapter(it.result.success)
        //viewBinding.listviewMain.adapter = adapter
        val adapter = AdapterProject(projectList, mainAppActivity!!)
        viewBinding.listviewMain.adapter = adapter
    }
    private fun loadData() {
        //Retrofit 사용하기
        //1. retrofit 객체 만들기

        //2. api 호출하기
        RetrofitClient.service.requestSDataList("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsImlhdCI6MTY3NDI5MzY3NSwiZXhwIjoxNjc0NDY2NDc1fQ.4-My4vE-zJRrHucOIY0_bWPJB3N6uhVZqChs8nztmZ4",
            0,"","","","","").enqueue(object: Callback<ResGetStudyList> {
            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 조회 성공", "\n${it.toString()}")
                                Log.d("test: 조회 성공!!!!!!", "\n${it.result.success}")

                                //dataList = it.result.success

                                //Log.d("test: 조회 성공 dataList ", "\n${dataList}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetStudyList>, t: Throwable) {
                Log.d("test: 조회실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setAdapter(studyList: ArrayList<SData>){
        //val adapter = ProjectAdapter(it.result.success)
        //viewBinding.listviewMain.adapter = adapter
        val adapter = AdapterStudy(studyList, mainAppActivity!!)
        viewBinding.listviewMain.adapter = adapter
    }

//
//
//    //추가
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        view?.findViewById<Button>(R.id.filter_loc)?.setOnClickListener{
//            //dismiss()
//        }
//    }
//
//    // 팝업 메뉴 아이템 클릭 시 실행되는 메소드
//    override fun onMenuItemClick(item: MenuItem?): Boolean {
//        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
//            R.id.m_project -> r_project()
//            R.id.m_study -> r_study()
//        }
//
//        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
//    }
//
//    //프로젝트,스터디 Fragment 전환 함수 r_project, r_study
//    fun r_project(){
//        childFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, projectFragment)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
//    }
//
//    fun r_study(){
//        childFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, studyFragment)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
//    }
}