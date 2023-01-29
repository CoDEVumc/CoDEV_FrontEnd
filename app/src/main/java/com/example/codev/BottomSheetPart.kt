package com.example.codev

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codev.databinding.PopupLocBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.codev.databinding.PopupPartBinding

class BottomSheetPart : BottomSheetDialogFragment(){
    private lateinit var popupPartBinding: PopupPartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupPartBinding = PopupPartBinding.inflate(layoutInflater)

        return popupPartBinding.root
        //return inflater.inflate(R.layout.popup_loc, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        popupPartBinding.btnSelect2.setOnClickListener {
            Log.d("test: ","적용하기 버튼 누름")
            dismiss()
        }
    }

}