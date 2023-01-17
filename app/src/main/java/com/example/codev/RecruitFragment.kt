package com.example.codev

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.codev.databinding.FragmentRecruitBinding

var projectFragment = Recruit_ProjectFragment()
var studyFragment = Recruit_StudyFragment()

class RecruitFragment:Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var viewBinding: FragmentRecruitBinding
    var mainAppActivity: MainAppActivity? = null

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
        viewBinding = FragmentRecruitBinding.inflate(layoutInflater)
        val temp = viewBinding.toolbarRecruit.toolbar1
        viewBinding.toolbarRecruit.toolbar1.inflateMenu(R.menu.menu_toolbar_2)
        viewBinding.toolbarRecruit.toolbar1.title = ""
        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)
        viewBinding.toolbarRecruit.toolbarImg.setOnClickListener {
            var popupMenu = PopupMenu(mainAppActivity, temp)
            popupMenu.inflate(R.menu.menu_recruit_project)
            popupMenu.show()
            Toast.makeText(mainAppActivity,"확장",Toast.LENGTH_SHORT).show()
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

        viewBinding.btnRecruit.setOnClickListener {
            showPopup(viewBinding.btnRecruit) //btnRecruit 클릭 시 팝업 메뉴 보임
        }


        return viewBinding.root
    }


    fun showPopup(v: View) {
        val popup = PopupMenu(mainAppActivity, v) // PopupMenu 객체 선언
        popup.menuInflater.inflate(R.menu.menu_recruit_project, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this) // 메뉴 아이템 클릭 리스너 달아주기
        popup.show() // 팝업 보여주기
    }


    //프로젝트,스터디 Fragment 전환 함수 r_project, r_study
    fun r_project(){
        childFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, projectFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    fun r_study(){
        childFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, studyFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    // 팝업 메뉴 아이템 클릭 시 실행되는 메소드
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.m_project -> r_project()
            R.id.m_study -> r_study()
        }

        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
    }


}