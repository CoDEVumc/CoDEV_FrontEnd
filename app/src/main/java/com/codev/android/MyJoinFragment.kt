package com.codev.android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codev.android.databinding.FragmentMyJoinBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyJoinFragment: Fragment() {
    private lateinit var viewBinding: FragmentMyJoinBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyJoinBinding.inflate(layoutInflater)

        viewBinding.viewpager.adapter = AdapterMyJoin(this)
        val tabTitleArray = arrayOf(
            "참여한 프로젝트",
            "참여한 스터디"
        )

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewpager){tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()


        return viewBinding.root
    }
}