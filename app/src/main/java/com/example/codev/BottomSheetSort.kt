package com.example.codev

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.codev.databinding.PopupSortBinding

class BottomSheetSort(private val returnSort: (String) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var popupSortBinding: PopupSortBinding

    var sort: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupSortBinding = PopupSortBinding.inflate(layoutInflater)

        popupSortBinding.radioGroupSort.setOnCheckedChangeListener { radioGroup, checkID ->

            when(checkID) {
                R.id.btn_new -> {
                    sort = ""
                    Log.d("test: ","최신순")
                }
                R.id.btn_like -> {
                    sort = "populaRity"
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
    }

}