package com.codev.android

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.codev.android.addpage.AddNewProjectActivity
import com.codev.android.addpage.AddNewStudyActivity
import com.codev.android.databinding.PopupChangeFragmentBinding
import com.codev.android.databinding.PopupWriteBinding

class PopupChangeFragment(private val returnWrite: (Int) -> Unit): DialogFragment() {
    private lateinit var popupChangeFragmentBinding: PopupChangeFragmentBinding
    var fragment: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupChangeFragmentBinding = PopupChangeFragmentBinding.inflate(layoutInflater)

        //다이얼로그가 아래에서 뜨게
        dialog?.window?.setGravity(Gravity.TOP or Gravity.LEFT)
        //레이아웃 배경 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        popupChangeFragmentBinding.radioGroupChange.setOnCheckedChangeListener { radioGroup, checkID ->
            when(checkID) {
                R.id.btn_project -> {
                    fragment = 0
                    popupChangeFragmentBinding.radioGroupChange.clearCheck()
                    returnWrite(fragment)

                }
                R.id.btn_study -> {
                    fragment = 1
                    popupChangeFragmentBinding.radioGroupChange.clearCheck()
                    returnWrite(fragment)
                }
            }

        }



        return popupChangeFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        popupChangeFragmentBinding.btnProject.setOnClickListener {
            dismiss()
        }
        popupChangeFragmentBinding.btnStudy.setOnClickListener {
            dismiss()
        }
    }




}