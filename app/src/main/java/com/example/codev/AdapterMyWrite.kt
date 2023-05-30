package com.example.codev

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterMyWrite(fragmentActivity: Fragment): FragmentStateAdapter(fragmentActivity) {
    //override fun getItemCount(): Int = 2
    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyWritePostedFragment()
            //1 -> MyWriteCommentedFragment()
            else -> MyWritePostedFragment() //기본이 프로젝트
        }
    }


}