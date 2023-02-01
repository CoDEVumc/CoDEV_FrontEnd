package com.example.codev

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.codev.addpage.AddNewProjectActivity
import com.example.codev.addpage.AddNewStudyActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.codev.databinding.PopupWriteBinding

class BottomSheetWrite(private val returnWrite: (String) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var popupWriteBinding: PopupWriteBinding

    var write: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupWriteBinding = PopupWriteBinding.inflate(layoutInflater)

        popupWriteBinding.radioGroupWrite.setOnCheckedChangeListener { radioGroup, checkID ->
            when(checkID) {
                R.id.btn_write_project -> {
                    write = "project"
                    //Log.d("test: ","최신순")
                    Toast.makeText(this.context, "프로젝트 버튼 클릭!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(getActivity(), AddNewProjectActivity::class.java)
                    startActivity(intent)
                    popupWriteBinding.radioGroupWrite.clearCheck()
                }
                R.id.btn_write_study -> {
                    write = "study"
                    //Log.d("test: ","추천순")
                    Toast.makeText(this.context, "스터디 버튼 클릭!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(getActivity(), AddNewStudyActivity::class.java)
                    startActivity(intent)
                    popupWriteBinding.radioGroupWrite.clearCheck()
                }
            }

            returnWrite(write)
        }



        return popupWriteBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        popupWriteBinding.btnWriteProject.setOnClickListener {
            dismiss()
        }
        popupWriteBinding.btnWriteStudy.setOnClickListener {
            dismiss()
        }
    }

}