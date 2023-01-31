package com.example.codev

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.codev.databinding.PopupLocBinding

class BottomSheetLoc(private val returnLoc: (String) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var popupLocBinding: PopupLocBinding

    var loc: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupLocBinding = PopupLocBinding.inflate(layoutInflater)

        popupLocBinding.btnSelect.setOnClickListener { view ->
            Log.d("test: ","적용하기 버튼 누름")
            dismiss()
            when(popupLocBinding.radioGroupLoc.checkedRadioButtonId){
                R.id.btn1 -> {
                    loc = resources.getString(R.string.filter_loc_1_text)
                }
                R.id.btn2 -> {
                    loc = resources.getString(R.string.filter_loc_2_text)
                }
                R.id.btn3 -> {
                    loc = resources.getString(R.string.filter_loc_3_text)
                }
                R.id.btn4 -> {
                    loc = resources.getString(R.string.filter_loc_4_text)
                }
                R.id.btn5 -> {
                    loc = resources.getString(R.string.filter_loc_5_text)
                }
                R.id.btn6 -> {
                    loc = resources.getString(R.string.filter_loc_6_text)
                }
                R.id.btn7 -> {
                    loc = resources.getString(R.string.filter_loc_7_text)
                }
                R.id.btn8 -> {
                    loc = resources.getString(R.string.filter_loc_8_text)
                }
                R.id.btn9 -> {
                    loc = resources.getString(R.string.filter_loc_9_text)
                }
                R.id.btn10 -> {
                    loc = resources.getString(R.string.filter_loc_10_text)
                }
                R.id.btn11 -> {
                    loc = resources.getString(R.string.filter_loc_11_text)
                }
                R.id.btn12 -> {
                    loc = resources.getString(R.string.filter_loc_12_text)
                }
                R.id.btn13 -> {
                    loc = resources.getString(R.string.filter_loc_13_text)
                }
                R.id.btn14 -> {
                    loc = resources.getString(R.string.filter_loc_14_text)
                }
                R.id.btn15 -> {
                    loc = resources.getString(R.string.filter_loc_15_text)
                }
                R.id.btn16 -> {
                    loc = resources.getString(R.string.filter_loc_16_text)
                }
                R.id.btn17 -> {
                    loc = resources.getString(R.string.filter_loc_17_text)
                }
                R.id.btn18 -> {
                    loc = resources.getString(R.string.filter_loc_18_text)
                }
            }
            returnLoc(loc)
            //Log.d("test: 클릭한 지역: ",loc)
        }

        popupLocBinding.btnReset.setOnClickListener { view ->
            loc = ""
            popupLocBinding.radioGroupLoc.clearCheck()
            //Log.d("test: ", "초기화 버튼 누름")
        }


        return popupLocBinding.root
        //return inflater.inflate(R.layout.popup_loc, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }





}




//        popupLocBinding.btnSelect.setOnClickListener {
//            Log.d("test: ","적용하기 버튼 누름")
//            dismiss()
//            Log.d("test: 클릭한 지역: ",p_loc)
//        }

//        popupLocBinding.radioGroupLoc.setOnCheckedChangeListener { radioGroupLoc, checkedid ->
//            when (checkedid) { //popupLocBinding.radioGroupLoc.checkedRadioButtonId
//                R.id.btn1 -> {
//                    p_loc = R.string.filter_loc_1_text.toString()
//                }
//                R.id.btn2 -> {
//                    p_loc = "${R.string.filter_loc_2_text}"
//                }
//                R.id.btn3 -> {
//                    p_loc = "${R.string.filter_loc_3_text}"
//                }
//                R.id.btn4 -> {
//                    p_loc = "${R.string.filter_loc_4_text}"
//                }
//                R.id.btn5 -> {
//                    p_loc = "${R.string.filter_loc_5_text}"
//                }
//                R.id.btn6 -> {
//                    p_loc = "${R.string.filter_loc_6_text}"
//                }
//                R.id.btn7 -> {
//                    p_loc = "${R.string.filter_loc_7_text}"
//                }
//                R.id.btn8 -> {
//                    p_loc = "${R.string.filter_loc_8_text}"
//                }
//                R.id.btn9 -> {
//                    p_loc = "${R.string.filter_loc_9_text}"
//                }
//                R.id.btn10 -> {
//                    p_loc = "${R.string.filter_loc_10_text}"
//                }
//                R.id.btn11 -> {
//                    p_loc = "${R.string.filter_loc_11_text}"
//                }
//                R.id.btn12 -> {
//                    p_loc = "${R.string.filter_loc_12_text}"
//                }
//                R.id.btn13 -> {
//                    p_loc = "${R.string.filter_loc_13_text}"
//                }
//                R.id.btn14 -> {
//                    p_loc = "${R.string.filter_loc_14_text}"
//                }
//                R.id.btn15 -> {
//                    p_loc = "${R.string.filter_loc_15_text}"
//                }
//                R.id.btn16 -> {
//                    p_loc = "${R.string.filter_loc_16_text}"
//                }
//                R.id.btn17 -> {
//                    p_loc = "${R.string.filter_loc_17_text}"
//                }
//                R.id.btn18 -> {
//                    p_loc = "${R.string.filter_loc_18_text}"
//                }
//
//
//            }
//        }