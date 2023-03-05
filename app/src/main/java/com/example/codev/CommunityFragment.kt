package com.example.codev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentCommunityBinding

class CommunityFragment:Fragment() {
    private lateinit var viewBinding: FragmentCommunityBinding
    var mainAppActivity: MainAppActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

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

        val intent = Intent(mainAppActivity, CommunityActivity::class.java)
        startActivity(intent)


        return viewBinding.root
    }
}