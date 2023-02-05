package com.example.codev

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
import com.example.codev.addpage.AddNewProjectActivity
import com.example.codev.addpage.AddNewStudyActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.codev.databinding.PopupWriteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetWrite(private val returnWrite: (String) -> Unit) : DialogFragment(){
    private lateinit var popupWriteBinding: PopupWriteBinding
    //private lateinit var dlg : BottomSheetDialog


    var write: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupWriteBinding = PopupWriteBinding.inflate(layoutInflater)

        //다이얼로그가 아래에서 뜨게
        //dialog?.window?.setGravity(Gravity.BOTTOM)

        dialog?.window?.setGravity(Gravity.getAbsoluteGravity(-300,150))
        //dialog?.window?.setGravity(Gravity.BOTTOM or Gravity.RIGHT)
        //레이아웃 배경 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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

    fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    // pop을 관리하기 싫어서 closure로 생성함
    data class PopupInfo(
        val v : View,
        val width  : Int,
        val height : Int,
        val x  : Int,
        val y : Int
    )

    // makePopupClosure를 한 번에 실행
    fun Context.quickPopup(toView: View, fnSetup : (()->Unit ) -> PopupInfo){
        fun makePopupClosure(toView: View, fnSetup : (()->Unit ) -> PopupInfo) : ()->Unit{
            var pop : PopupWindow? = null
            fun dismiss() = pop?.dismiss()
            return {

                val popInfo = fnSetup(::dismiss)

                val width  = if(popInfo.width == 0 ){
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else dpToPx(popInfo.width.toFloat())

                val height = if(popInfo.height == 0 ){
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else dpToPx(popInfo.height.toFloat())

                pop = PopupWindow(
                    popInfo.v,
                    width,
                    height,
                    true
                ).apply {
                    showAsDropDown(toView, popInfo.x, popInfo.y)
                }
            }
        }

        makePopupClosure(toView){
                dismiss ->
            fnSetup(dismiss)
        }.apply { this() }
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
private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

