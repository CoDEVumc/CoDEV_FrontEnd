package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
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
            /*"댓글 단 글"*/
        )

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewpager){tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()

        if (now != null) {
            viewBinding.tabLayout.getTabAt(now)?.select()
        }


        //여기부터
        viewBinding.radioGroup.setOnCheckedChangeListener { radioGroup, checkID ->

            when(checkID) {
                R.id.btn_project -> {
                    //context: Context, int: Int, coMyBoard: Boolean, sortingTag: String
                    /*now = 0
                    bundle.putInt("now", now)
                    val myWritePostedFragment = MyWritePostedFragment()
                    myWritePostedFragment.arguments = bundle*/

                    val bundle = Bundle()
                    bundle.putInt("toggle", 0)

                    val receiverFragment = MyWritePostedFragment()
                    receiverFragment.arguments = bundle

                    //val toggle = "toggle"
                    //setFragmentResult("requestKey", bundleOf("bundleKey" to toggle))

                    Log.d("MyWritePostedFragment ", "프로젝트 버튼 클릭"+bundle)
                }
                R.id.btn_study -> {
                    val bundle = Bundle()
                    bundle.putInt("toggle", 1)
                    Log.d("MyWritePostedFragment ", "스터디 버튼 클릭")
                }
                R.id.btn_question -> {
                    val bundle = Bundle()
                    bundle.putInt("toggle", 2)
                    Log.d("MyWritePostedFragment ", "질문 버튼 클릭")
                }
                R.id.btn_info -> {
                    val bundle = Bundle()
                    bundle.putInt("toggle", 3)
                    Log.d("MyWritePostedFragment ", "정보 버튼 클릭")
                }
                R.id.btn_contest -> {
                    val bundle = Bundle()
                    bundle.putInt("toggle", 4)
                    Log.d("MyWritePostedFragment ", "공모전 버튼 클릭")
                }
            }
        }
        //여기까지

        return viewBinding.root
    }




}