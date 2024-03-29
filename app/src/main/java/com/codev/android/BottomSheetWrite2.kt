package com.codev.android

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.GravityInt
import androidx.fragment.app.DialogFragment
import com.codev.android.addpage.AddNewProjectActivity
import com.codev.android.addpage.AddNewStudyActivity
import com.codev.android.addpage.AddPostActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.codev.android.databinding.PopupWrite2Binding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetWrite2(private val returnWrite: (String) -> Unit) : DialogFragment(){
    private lateinit var popupWriteBinding: PopupWrite2Binding
    //private lateinit var dlg : BottomSheetDialog


    var postType: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupWriteBinding = PopupWrite2Binding.inflate(layoutInflater)

        //다이얼로그가 아래에서 뜨게
        //dialog?.window?.setGravity(Gravity.BOTTOM)

        dialog?.window?.setGravity(Gravity.getAbsoluteGravity(-300,150))
        //dialog?.window?.setGravity(Gravity.BOTTOM or Gravity.RIGHT)
        //레이아웃 배경 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        popupWriteBinding.radioGroupWrite.setOnCheckedChangeListener { radioGroup, checkID ->
            when(checkID) {
                R.id.btn_write_question -> {
                    postType = "qna"
                    //Log.d("test: ","최신순")
                    Toast.makeText(this.context, "질문 버튼 클릭!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(getActivity(), AddPostActivity::class.java)
                    intent.putExtra("postType", "qna")
                    intent.putExtra("isOld", false)
                    startActivity(intent)
                    popupWriteBinding.radioGroupWrite.clearCheck()
                }
                R.id.btn_write_info -> {
                    postType = "info"
                    //Log.d("test: ","추천순")
                    Toast.makeText(this.context, "정보 버튼 클릭!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(getActivity(), AddPostActivity::class.java)
                    intent.putExtra("postType", "info")
                    intent.putExtra("isOld", false)
                    startActivity(intent)
                    popupWriteBinding.radioGroupWrite.clearCheck()
                }
            }

            returnWrite(postType)
        }



        return popupWriteBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        popupWriteBinding.btnWriteQuestion.setOnClickListener {
            dismiss()
        }
        popupWriteBinding.btnWriteInfo.setOnClickListener {
            dismiss()
        }
        popupWriteBinding.floatingActionButton.setOnClickListener{
            dismiss()
        }
    }



    //배경 투명처리
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        // 이 코드를 실행하지 않으면
//        // XML에서 round 처리를 했어도 적용되지 않는다.
//        dlg = ( super.onCreateDialog(savedInstanceState).apply {
//            // window?.setDimAmount(0.2f) // Set dim amount here
//            setOnShowListener {
//                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
//                bottomSheet.setBackgroundResource(android.R.color.transparent)
//
//                // 아래와 같이하면 Drag를 불가능하게 한다.
//                //val behavior = BottomSheetBehavior.from(bottomSheet!!)
//                //behavior.isDraggable = false
//            }
//        } ) as BottomSheetDialog
//        return dlg
//    }

}

