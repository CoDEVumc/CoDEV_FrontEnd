package com.example.codev

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentChatBinding

class ChatFragment:Fragment() {
    private lateinit var viewBinding: FragmentChatBinding
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
        viewBinding = FragmentChatBinding.inflate(layoutInflater)
        viewBinding.toolbarChat.toolbar.inflateMenu(R.menu.menu_toolbar_1)
        viewBinding.toolbarChat.toolbar.title = "채팅"

        return viewBinding.root
    }
}