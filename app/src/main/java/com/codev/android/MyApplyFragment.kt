package com.codev.android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codev.android.databinding.FragmentMyApplyBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplyFragment: Fragment() {
    private lateinit var viewBinding: FragmentMyApplyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyApplyBinding.inflate(layoutInflater)

        viewBinding.viewpager.adapter = AdapterMyApply(this)
        val tabTitleArray = arrayOf(
            "지원한 프로젝트",
            "지원한 스터디"
        )

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewpager){tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()


        return viewBinding.root
    }
}