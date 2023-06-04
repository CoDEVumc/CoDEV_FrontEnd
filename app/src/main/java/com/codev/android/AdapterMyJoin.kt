package com.codev.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterMyJoin(fragmentActivity: Fragment): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyJoinProjectFragment()
            1 -> MyJoinStudyFragment()
            else -> MyJoinProjectFragment()
        }
    }


}