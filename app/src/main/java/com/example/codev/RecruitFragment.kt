package com.example.codev

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentRecruitBinding

class RecruitFragment:Fragment() {
    private lateinit var viewBinding: FragmentRecruitBinding
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
        viewBinding = FragmentRecruitBinding.inflate(layoutInflater)
        val temp = viewBinding.toolbarRecruit.toolbar
        viewBinding.toolbarRecruit.toolbar.inflateMenu(R.menu.menu_toolbar_2)
        viewBinding.toolbarRecruit.toolbar.title = "프로젝트"
        viewBinding.toolbarRecruit.toolbarExtend.setImageResource(R.drawable.down2)
        viewBinding.toolbarRecruit.toolbarExtend.setOnClickListener {
            var popupMenu = PopupMenu(mainAppActivity, temp)
            popupMenu.inflate(R.menu.menu_recruit_project)
            popupMenu.show()
            Toast.makeText(mainAppActivity,"확장",Toast.LENGTH_SHORT).show()
        }
        viewBinding.toolbarRecruit.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search ->{
                    Toast.makeText(mainAppActivity, "검색", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_alarm ->{
                    Toast.makeText(mainAppActivity, "알람", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        

        return viewBinding.root
    }

}