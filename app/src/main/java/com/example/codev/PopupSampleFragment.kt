package com.example.codev

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.example.codev.addpage.AddNewProjectActivity
import com.example.codev.addpage.AddNewStudyActivity
import com.example.codev.databinding.PopupChangeFragmentBinding
import com.example.codev.databinding.PopupSampleBinding
import com.example.codev.databinding.PopupWriteBinding

class PopupSampleFragment(private val returnWrite: (String) -> Unit): DialogFragment() {
    private lateinit var popupSampleBinding: PopupSampleBinding

    var clicked: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        popupSampleBinding = PopupSampleBinding.inflate(layoutInflater)

        //레이아웃 배경 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //다이얼로그 크기 조절 부분 가로 80%(0.9f)차게, 세로 40%(0.4f)차게
        //context?.dialogFragmentResize(this@PopupSampleFragment,0.9f,0.4f)

        popupSampleBinding.btnContinue.setOnClickListener {
            clicked = "yes"
            returnWrite(clicked)
            dismiss()
        }
        popupSampleBinding.btnCancel.setOnClickListener {
            clicked = "no"
            returnWrite(clicked)
            dismiss()
        }


        return popupSampleBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    //다이얼로그 사이즈 조절
    fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {

            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {

            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }




}
