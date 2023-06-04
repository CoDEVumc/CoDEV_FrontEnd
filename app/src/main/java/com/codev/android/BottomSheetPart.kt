package com.codev.android

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.codev.android.databinding.PopupLocBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.codev.android.databinding.PopupPartBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetPart(private val returnPart: (String) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var popupPartBinding: PopupPartBinding
    private lateinit var dlg : BottomSheetDialog

    var part: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupPartBinding = PopupPartBinding.inflate(layoutInflater)

        popupPartBinding.btnSelect.setOnClickListener { view ->
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

            returnPart(part)
            //Log.d("test: 클릭한 파트: ",part)
        }

        popupPartBinding.btnReset.setOnClickListener { view ->
            //Log.d("test: ", "초기화 버튼 누름")
            part = ""
            popupPartBinding.radioGroupPart.clearCheck()
        }

        popupPartBinding.btnClose.setOnClickListener {
            dismiss()
        }

        return popupPartBinding.root
        //return inflater.inflate(R.layout.popup_loc, container, false)
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