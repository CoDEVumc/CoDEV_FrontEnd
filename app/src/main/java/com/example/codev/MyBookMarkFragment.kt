package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentMyBookmarkBinding
import com.example.codev.databinding.FragmentMyBookmarkProjectBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookMarkFragment: Fragment() {
    private lateinit var viewBinding: FragmentMyBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyBookmarkBinding.inflate(layoutInflater)

        val now = arguments?.getInt("now") //프,스


        viewBinding.viewpager.adapter = AdapterMyBookMark(this)
        val tabTitleArray = arrayOf(
            "프로젝트",
            "스터디",
            "글",
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