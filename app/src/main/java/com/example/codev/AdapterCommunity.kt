package com.example.codev

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterCommunity(fragmentActivity: Fragment): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CommunityQuestionFragment()
            1 -> CommunityInfoFragment()
            2 -> CommunityContestFragment()
            else -> CommunityQuestionFragment() //기본이 질문글
        }
    }


}