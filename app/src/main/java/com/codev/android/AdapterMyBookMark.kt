package com.codev.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterMyBookMark(fragmentActivity: Fragment): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyBookMarkProjectFragment()
            1 -> MyBookMarkStudyFragment()
            2 -> MyBookMarkQuestionAndInfoFragment()
            3 -> MyBookMarkContestFragment()
            else -> MyBookMarkProjectFragment() //기본이 프로젝트
        }
    }


}