package com.example.codev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityFragment: Fragment() { //검색, 북마크, 알림 여기서 활성화
    private lateinit var viewBinding: FragmentCommunityBinding

    private var write : String = ""
    private var page : Int = 2 //커뮤니티Fragment에서 질문,정보 = 2, 공모전 = 3


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
        viewBinding = FragmentCommunityBinding.inflate(layoutInflater)
        var temp = viewBinding.toolbarCommunity.toolbar1
        viewBinding.toolbarCommunity.toolbar1.inflateMenu(R.menu.menu_toolbar_4)
        viewBinding.toolbarCommunity.toolbar1.title = ""
        viewBinding.toolbarCommunity.toolbarImg.setImageResource(R.drawable.logo_community)

        // TabLayout의 선택 리스너 등록
        viewBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener { //object : TabLayout.OnTabSelectedListener
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 선택된 탭의 position 가져오기
                val selectedTabPosition = tab?.position ?: 0
                //Log.d("CommunityFragment", "선택된 탭의 position: $selectedTabPosition")
                page = selectedTabPosition + 2
                Log.d("CommunityFragment", "page: $page")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택 해제될 때 호출되는 콜백
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 탭이 다시 선택될 때 호출되는 콜백
            }
        })

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
        viewBinding.toolbarCommunity.toolbar1.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search ->{
                    Toast.makeText(mainAppActivity, "검색", Toast.LENGTH_SHORT).show()
                    val intent = Intent(mainAppActivity, CommunitySearchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_bookmark ->{
                    Toast.makeText(mainAppActivity, "북마크", Toast.LENGTH_SHORT).show()

                    Log.d("CommunityFragment | page 값 : ", page.toString())

                    //page는 현재 tab의 position
                    when(page) { //now = 프로젝트 - 0 / 스터디 - 1 / 질문,정보 - 2 / 공모전 - 3
                        2 -> { //질문 -> 북마크 > 글
                            val intent = Intent(mainAppActivity, MyBookMarkActivity::class.java)
                            intent.putExtra("now", page)
                            startActivity(intent)
                        }
                        3 -> { //정보 -> 북마크 > 글
                            val intent = Intent(mainAppActivity, MyBookMarkActivity::class.java)
                            page-=1
                            intent.putExtra("now", page)
                            startActivity(intent)
                        }
                        4 -> { //공모전 -> 북마크 > 공모전
                            val intent = Intent(mainAppActivity, MyBookMarkActivity::class.java)
                            page-=1
                            intent.putExtra("now", page)
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

        val now = arguments?.getInt("now")

        //작성버튼 누르면 작성페이지로 이동
        val bottomSheetWrite = BottomSheetWrite2(){
            write = it
            Log.d("test :", write+" 버튼 누름")
        }
        //플로팅 작성버튼
        viewBinding.floatingActionButton.setOnClickListener {
            bottomSheetWrite.show(childFragmentManager, bottomSheetWrite.tag)
        }

        viewBinding.viewpager.adapter = AdapterCommunity(this)
        val tabTitleArray = arrayOf(
            "질문",
            "정보",
            "공모전"
        )

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewpager){tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()

        if (now != null) {
            viewBinding.tabLayout.getTabAt(now)?.select()
        }




        return viewBinding.root
    }


}