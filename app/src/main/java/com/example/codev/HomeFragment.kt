package com.example.codev

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentHomeBinding

class HomeFragment:Fragment() {
    private lateinit var viewBinding: FragmentHomeBinding
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
        viewBinding = FragmentHomeBinding.inflate(layoutInflater)
        viewBinding.toolbarHome.toolbar1.inflateMenu(R.menu.menu_toolbar_1)
        viewBinding.toolbarHome.toolbar1.title = ""
        viewBinding.toolbarHome.toolbarImg.setImageResource(R.drawable.logo_home)

        return viewBinding.root
    }
}