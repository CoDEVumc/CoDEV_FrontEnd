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
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.FragmentRecruitProjectBinding
import com.example.codev.databinding.RecycleRecruitProjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecruitProjectFragment : Fragment() {
    private lateinit var viewBinding: FragmentRecruitProjectBinding

    //여기
    private lateinit var dataList : ArrayList<ResponseOfGetProject>

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


        loadData_p(0) //기본으로 0page PData 가져오기
        var page: Int = 0

        //페이징 처리 부분
        viewBinding.listviewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //**********************
                if (!viewBinding.listviewMain.canScrollVertically(1)) {
                    //스크롤 할 수 있으면 true, 못하면 false 반환
                    Log.d("SCROLL", "last Position...")

//                    page += 1
//                    while (){ //success != [] 이면
//                        loadData_p(page)
//                    }

                }
                //**********************
            }
        })


        viewBinding.toolbarRecruit.toolbarImg.setOnClickListener {
            var popupMenu = PopupMenu(mainAppActivity, temp)
            popupMenu.inflate(R.menu.menu_recruit)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.m_project -> {
                        Toast.makeText(mainAppActivity, "프로젝트", Toast.LENGTH_SHORT).show()

                        //Adatper 호출부분
                        loadData_p(0)
                        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)
                        true
                    }
                    R.id.m_study -> {
                        Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()
                        //(parentFragment as RecruitFragment).r_study()

                        loadData_s(0)
                        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)

                        //모집중 버튼이 (스)로 바껴야됨

                        true
                    }
                    else -> false
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

        //지역 버튼
        viewBinding.loc.setOnClickListener {
            val bottomSheetLoc = BottomSheetLoc()
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }
        viewBinding.filterLoc.setOnClickListener {
            val bottomSheetLoc = BottomSheetLoc()
            bottomSheetLoc.show(childFragmentManager, bottomSheetLoc.tag)
        }

        //분야 버튼
        viewBinding.part.setOnClickListener {
            val bottomSheetPart = BottomSheetPart()
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }
        viewBinding.filterPart.setOnClickListener {
            val bottomSheetPart = BottomSheetPart()
            bottomSheetPart.show(childFragmentManager, bottomSheetPart.tag)
        }

        //(프로젝트에서)모집중만 보기 버튼
        var chk : Boolean = false //처음엔 버튼 안눌림
        viewBinding.recruitingProjectBtn.setOnClickListener {
            //state_selected false면 true로 바꾸기 / true면 false로 바꾸기
            chk = viewBinding.recruitingProjectBtn.isChecked
            if(chk == true) {
                loadData_p_ing(0)
            }
            else{
                loadData_p(0)
            }
            Log.d("test: RPF - 모집중인 플젝 조회실패",it.toString())
        }




        return viewBinding.root
    }

    //전체 프로젝트 조회
    private fun loadData_p(int: Int) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int,"","","","","").enqueue(object: Callback<ResGetProjectList>{
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
                                setAdapter_p(it.result.success) //projectAdapter
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
    private fun loadData_s(int: Int) {
        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int,"","","","","").enqueue(object: Callback<ResGetStudyList> {
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
                                setAdapter_s(it.result.success) //projectAdapter
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

    //모집중인 프로젝트만 조회
    private fun loadData_p_ing(int: Int) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int,"","","","ING","").enqueue(object: Callback<ResGetProjectList>{
            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 모집중인 플젝 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 모집중 플젝 데이터 : ", "\n${it.result.success}")
                                setAdapter_p(it.result.success) //projectAdapter
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_p_ing(모집중 플젝조회) : ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }
    //모집중인 스터디만 조회
    private fun loadData_s_ing(int: Int) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int,"","","","ING","").enqueue(object: Callback<ResGetProjectList>{
            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 모집중인 스터디 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 모집중 스터디 데이터 : ", "\n${it.result.success}")
                                setAdapter_p(it.result.success) //projectAdapter
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_s_ing(모집중 스터디 조회): ", "[Fail]${t.toString()}")
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