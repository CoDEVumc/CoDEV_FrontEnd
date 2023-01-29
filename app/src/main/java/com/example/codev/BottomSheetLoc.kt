package com.example.codev

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.codev.databinding.PopupLocBinding

class BottomSheetLoc : BottomSheetDialogFragment(){
    private lateinit var popupLocBinding: PopupLocBinding

    var p_loc: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupLocBinding = PopupLocBinding.inflate(layoutInflater)

//        popupLocBinding.btnSelect.setOnClickListener {
//            Log.d("test: ","적용하기 버튼 누름")
//            dismiss()
//            Log.d("test: 클릭한 지역: ",p_loc)
//        }

        popupLocBinding.radioGroupLoc.setOnClickListener{ checkedId ->
            when(checkedId){//true랑 else->false 달아줘야함??
                popupLocBinding.btn1 -> { p_loc = "${popupLocBinding.btn1.text}" }
                popupLocBinding.btn2 -> { p_loc = "${popupLocBinding.btn2.text}" }
                popupLocBinding.btn3 -> { p_loc = "${popupLocBinding.btn3.text}" }
                popupLocBinding.btn4 -> { p_loc = "${popupLocBinding.btn4.text}" }
                popupLocBinding.btn5 -> { p_loc = "${popupLocBinding.btn5.text}" }
                popupLocBinding.btn6 -> { p_loc = "${popupLocBinding.btn6.text}" }
                popupLocBinding.btn7 -> { p_loc = "${popupLocBinding.btn7.text}" }
                popupLocBinding.btn8 -> { p_loc = "${popupLocBinding.btn8.text}" }
                popupLocBinding.btn9 -> { p_loc = "${popupLocBinding.btn9.text}" }
                popupLocBinding.btn10 -> { p_loc = "${popupLocBinding.btn10.text}" }
                popupLocBinding.btn11 -> { p_loc = "${popupLocBinding.btn11.text}" }
                popupLocBinding.btn12 -> { p_loc = "${popupLocBinding.btn12.text}" }
                popupLocBinding.btn13 -> { p_loc = "${popupLocBinding.btn13.text}" }
                popupLocBinding.btn14 -> { p_loc = "${popupLocBinding.btn14.text}" }
                popupLocBinding.btn15 -> { p_loc = "${popupLocBinding.btn15.text}" }
                popupLocBinding.btn16 -> { p_loc = "${popupLocBinding.btn16.text}" }
                popupLocBinding.btn17 -> { p_loc = "${popupLocBinding.btn17.text}" }
                popupLocBinding.btn18 -> { p_loc = "${popupLocBinding.btn18.text}" }
            }
        }



        return popupLocBinding.root
        //return inflater.inflate(R.layout.popup_loc, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        popupLocBinding.btnSelect.setOnClickListener {
            Log.d("test: ","적용하기 버튼 누름")
            dismiss()
            Log.d("test: 클릭한 지역: ",p_loc)
        }
    }

    //지역 고르고 적용하기 누르면 -> 그 지역으로 "" 내용 바껴야됨




}