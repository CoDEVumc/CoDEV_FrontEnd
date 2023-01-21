package com.example.codev.addpage

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat.*
import com.example.codev.R
import com.example.codev.databinding.AddSubSectionBinding
import com.google.android.material.chip.Chip


class AddPageFunction {

    fun setStackChip(
        context: Context,
        addItem: AddListItem,
        adapter: NewDropdownRVListAdapter
    ): Chip {
        var chipView = Chip(context)
        chipView.text = addItem.name
        chipView.setChipBackgroundColorResource(R.color.green_300)
        chipView.chipStrokeColor = getColorStateList(context, R.color.green_900)
        chipView.chipStrokeWidth = dpToPx(context, 1f)
        chipView.chipCornerRadius = dpToPx(context, 6f)
        chipView.isCloseIconVisible = true
        chipView.closeIcon = getDrawable(context, R.drawable.close_icon)
        chipView.closeIcon?.setTint(getColor(context, R.color.green_900))
        chipView.setTextColor(getColor(context, R.color.black_900))
        chipView.setTextAppearance(R.style.Text_Body4_SemiBold)

        return chipView
    }

    fun changeItem2False(allStackList: HashMap<Int, ArrayList<AddListItem>>, addItem: AddListItem){
        for(i in allStackList.values){
            for(j in i){
                if(j.name == addItem.name){
                    j.isSelected = false
                    return
                }
            }
        }
    }

    fun changeItem2True(allStackList: HashMap<Int, ArrayList<AddListItem>>, name: String){
        for(i in allStackList.values){
            for(j in i){
                if(j.name == name){
                    j.isSelected = true
                    return
                }
            }
        }
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    fun getRealPathFromURI(context: Context, uri: Uri?): String {
        val cursor: Cursor = context.contentResolver.query(uri!!, null, null, null, null)!!
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    fun setAddSubButton(context: Context, section: AddSubSectionBinding){
        //버튼
        section.subButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> section.subButton.background = getDrawable(context, R.drawable.sub_button_press)
                    MotionEvent.ACTION_UP -> section.subButton.background = getDrawable(context, R.drawable.sub_button)
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        section.subButton.setOnClickListener {
            var nowInt = Integer.parseInt(section.peopleNum.text.toString())

            if(nowInt > 0){
                section.peopleNum.text = (nowInt - 1).toString()

            }
            if(nowInt-1 == 0){
                section.peopleNum.setTextColor(getColor(context, R.color.black_300))
            }
        }

        section.addButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> section.addButton.background = getDrawable(context, R.drawable.add_button_press)
                    MotionEvent.ACTION_UP -> section.addButton.background = getDrawable(context, R.drawable.add_button)
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        section.addButton.setOnClickListener {
            var nowInt = Integer.parseInt(section.peopleNum.text.toString())

            if(nowInt >= 0){
                section.peopleNum.text = (nowInt + 1).toString()
            }
            if(nowInt+1 == 1){
                section.peopleNum.setTextColor(getColor(context, R.color.black_900))
            }
        }
        //버튼 끝
    }

}