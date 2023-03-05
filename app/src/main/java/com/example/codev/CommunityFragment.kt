package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityFragment: Fragment() {
    private lateinit var viewBinding: FragmentCommunityBinding

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


        val now = arguments?.getInt("now")

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