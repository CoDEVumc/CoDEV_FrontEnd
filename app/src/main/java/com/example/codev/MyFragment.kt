package com.example.codev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentMyBinding

class MyFragment:Fragment() {
    private lateinit var viewBinding: FragmentMyBinding
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
        viewBinding = FragmentMyBinding.inflate(layoutInflater)
        viewBinding.toolbarMy.toolbar1.inflateMenu(R.menu.menu_toolbar_1)
        viewBinding.toolbarMy.toolbar1.title = ""
        viewBinding.toolbarMy.toolbarImg.setImageResource(R.drawable.logo_my)


        val temp: ResPortFolio = ResPortFolio("temp","2021.10.11")
        val templist = listOf<ResPortFolio>(temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp)
        val adapter = PortfolioAdapter1(templist)
        viewBinding.recyclePortfolio.adapter = adapter

        viewBinding.btnMore.setOnClickListener {
            val intent = Intent(mainAppActivity, MyPortfolioActivity::class.java)
            startActivity(intent)
        }

        return viewBinding.root
    }
}