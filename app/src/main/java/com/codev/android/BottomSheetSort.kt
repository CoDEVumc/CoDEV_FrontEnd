package com.codev.android

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.codev.android.databinding.PopupSortBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetSort(private val returnSort: (String) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var popupSortBinding: PopupSortBinding
    private lateinit var dlg : BottomSheetDialog

    var sort: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupSortBinding = PopupSortBinding.inflate(layoutInflater)

        popupSortBinding.chkLike.visibility = View.INVISIBLE //처음에 최신순으로 적용된거 처럼 보일라고 추천순에 체크표시 안보이게 함

        popupSortBinding.radioGroupSort.setOnCheckedChangeListener { radioGroup, checkID ->
            when(checkID) {
                R.id.btn_new -> {
                    sort = ""
                    popupSortBinding.chkLike.visibility = View.INVISIBLE
                    Log.d("test: ","최신순")
                }
                R.id.btn_like -> {
                    sort = "populaRity"
                    popupSortBinding.chkNew.visibility = View.INVISIBLE
                    popupSortBinding.chkLike.visibility = View.VISIBLE
                    Log.d("test: ","추천순")
                }
            }

            returnSort(sort)
        }




        return popupSortBinding.root
        //return inflater.inflate(R.layout.popup_loc, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        popupSortBinding.btnNew.setOnClickListener {
            dismiss()
        }
        popupSortBinding.btnLike.setOnClickListener {
            dismiss()
        }
        popupSortBinding.btnClose.setOnClickListener {
            dismiss()
        }
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