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
import com.example.codev.databinding.FragmentMyWriteBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyWriteFragment: Fragment() {
    private lateinit var viewBinding: FragmentMyWriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyWriteBinding.inflate(layoutInflater)

        val now = arguments?.getInt("now")

        viewBinding.viewpager.adapter = AdapterMyWrite(this)
        val tabTitleArray = arrayOf(
            "내가 쓴 글",
            "댓글 단 글"
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