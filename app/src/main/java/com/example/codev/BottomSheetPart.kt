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

    var part: String=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupPartBinding = PopupPartBinding.inflate(layoutInflater)

        popupPartBinding.btnSelect2.setOnClickListener { view ->
            Log.d("test: ","적용하기 버튼 누름")
            dismiss()
            when(popupPartBinding.radioGroupPart.checkedRadioButtonId){
                R.id.btn01 -> {
                    part = resources.getString(R.string.filter_part_1_text)
                }
                R.id.btn02 -> {
                    part = resources.getString(R.string.filter_part_2_text)
                }
                R.id.btn03 -> {
                    part = resources.getString(R.string.filter_part_3_text)
                }
                R.id.btn04 -> {
                    part = resources.getString(R.string.filter_part_4_text)
                }
                R.id.btn05 -> {
                    part = resources.getString(R.string.filter_part_5_text)
                }
            }


            Log.d("test: 클릭한 파트: ",part)
        }

        return popupPartBinding.root
        //return inflater.inflate(R.layout.popup_loc, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}