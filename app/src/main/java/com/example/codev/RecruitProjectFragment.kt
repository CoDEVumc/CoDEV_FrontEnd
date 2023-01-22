package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.databinding.FragmentRecruitProjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//var studyFragment = Recruit_StudyFragment()

class RecruitProjectFragment : Fragment()  { //PopupMenu.OnMenuItemClickListener
    private lateinit var viewBinding: FragmentRecruitProjectBinding
    private var dataList: ArrayList<PData> = ArrayList()

    var mainAppActivity: MainAppActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentRecruitProjectBinding.inflate(layoutInflater)

        loadData()

    }


    private fun loadData() {
        //Retrofit 사용하기
        //1. retrofit 객체 만들기

        //2. api 호출하기
        RetrofitClient.service.requestAllData("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsImlhdCI6MTY3NDI5MzY3NSwiZXhwIjoxNjc0NDY2NDc1fQ.4-My4vE-zJRrHucOIY0_bWPJB3N6uhVZqChs8nztmZ4",
        0,"","","","").enqueue(object: Callback<ProjectDataModel>{
            override fun onResponse(call: Call<ProjectDataModel>, response: Response<ProjectDataModel>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 조회 성공", "\n${it.toString()}")
                                Log.d("test: 조회 성공!!!!!!", "\n${it.result.success}")

                                dataList = it.result.success

                                Log.d("test: 조회 성공 dataList ", "\n${dataList}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProjectDataModel>, t: Throwable) {
                Log.d("test: 조회실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRecruitProjectBinding.inflate(layoutInflater)
        var temp = viewBinding.toolbarRecruit.toolbar1
        viewBinding.toolbarRecruit.toolbar1.inflateMenu(R.menu.menu_recruit_project)
        viewBinding.toolbarRecruit.toolbar1.title = ""
        viewBinding.toolbarRecruit.toolbar1.setOnClickListener {
            var popupMenu = PopupMenu(mainAppActivity, temp)
            popupMenu.inflate(R.menu.menu_recruit_project)
            popupMenu.show()
        }
        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)

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

        loadData()

        val adapter = ProjectAdapter(dataList)
        viewBinding.listviewMain.adapter = adapter

        adapter.notifyDataSetChanged()

        //추가
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_recruit_project, container, false)
        return viewBinding.root
    }


//    //팝업 선택 메뉴 때문에 추가
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


}