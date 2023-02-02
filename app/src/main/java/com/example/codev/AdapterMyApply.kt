package com.example.codev

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterMyApply(fragmentActivity: Fragment): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyApplyProjectFragment()
            1 -> MyApplyStudyFragment()
            else -> MyApplyProjectFragment()
        }
    }


}