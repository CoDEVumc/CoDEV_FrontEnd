package com.codev.android

import android.app.Dialog
import android.app.ProgressDialog.show
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.codev.android.databinding.PopupLocBinding
import com.codev.android.databinding.PopupPartBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetLoc(private val returnLoc: (String) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var popupLocBinding: PopupLocBinding
    private lateinit var popupPartBinding: PopupPartBinding
    private lateinit var dlg : BottomSheetDialog

    var loc: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupLocBinding = PopupLocBinding.inflate(layoutInflater)


        //rg1
        popupLocBinding.btn1.setOnClickListener{
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn4.setOnClickListener{
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn7.setOnClickListener{
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn10.setOnClickListener{
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn13.setOnClickListener{
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn16.setOnClickListener{
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }

        //rg2
        popupLocBinding.btn2.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn5.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn8.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn11.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn14.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }
        popupLocBinding.btn17.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg3.clearCheck()
        }

        //rg3
        popupLocBinding.btn3.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
        }
        popupLocBinding.btn6.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
        }
        popupLocBinding.btn9.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
        }
        popupLocBinding.btn12.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
        }
        popupLocBinding.btn15.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
        }
        popupLocBinding.btn18.setOnClickListener{
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
        }


        popupLocBinding.btnSelect.setOnClickListener { view ->
            Log.d("test: ","적용하기 버튼 누름")
            dismiss()
            when(popupLocBinding.rg1.checkedRadioButtonId){ // or popupLocBinding.rg2.checkedRadioButtonId or popupLocBinding.rg3.checkedRadioButtonId
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
            when(popupLocBinding.rg2.checkedRadioButtonId){ // or popupLocBinding.rg2.checkedRadioButtonId or popupLocBinding.rg3.checkedRadioButtonId //<-- 안먹어
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
            when(popupLocBinding.rg3.checkedRadioButtonId){ // or popupLocBinding.rg2.checkedRadioButtonId or popupLocBinding.rg3.checkedRadioButtonId
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
            //Log.d("test: 적용한 지역: ",loc)
        }

        popupLocBinding.btnReset.setOnClickListener { view ->
            loc = ""
            popupLocBinding.rg1.clearCheck()
            popupLocBinding.rg2.clearCheck()
            popupLocBinding.rg3.clearCheck()
            //Log.d("test: ", "초기화 버튼 누름")
        }

        popupLocBinding.btnClose.setOnClickListener {
            dismiss()
        }


        return popupLocBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    //배경 투명처리
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 이 코드를 실행하지 않으면
        // XML에서 round 처리를 했어도 적용되지 않는다.
        dlg = ( super.onCreateDialog(savedInstanceState).apply {
            // window?.setDimAmount(0.2f) // Set dim amount here
            setOnShowListener {
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)

                // 아래와 같이하면 Drag를 불가능하게 한다.
                //val behavior = BottomSheetBehavior.from(bottomSheet!!)
                //behavior.isDraggable = false
            }
        } ) as BottomSheetDialog
        return dlg
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