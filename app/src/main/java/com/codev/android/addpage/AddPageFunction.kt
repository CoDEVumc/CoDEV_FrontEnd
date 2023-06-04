package com.codev.android.addpage

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.codev.android.R
import com.codev.android.databinding.AddSubSectionBinding
import com.google.android.material.chip.Chip
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


class AddPageFunction {

    fun checkSelfPermission(context: Context, nowActivity: AppCompatActivity) {
        var temp = ""

        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
        }
//        //파일 쓰기 권한 확인
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " "
//        }
        if (!TextUtils.isEmpty(temp)) {
            Log.d("permission", "checkSelfPermission: 권한을 요청합니다.")
            // 권한 요청
            ActivityCompat.requestPermissions(
                nowActivity,
                temp.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray(),
                1)
        }
    }

    fun checkSelfCameraPermission(context: Context, nowActivity: AppCompatActivity, runFunction: () -> Unit) {
        var temp = ""

        //카메라 권한 확인
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.CAMERA + " "
        }
//        //파일 쓰기 권한 확인
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " "
//        }
        if (!TextUtils.isEmpty(temp)) {
            Log.d("permission", "checkSelfPermission: 권한을 요청합니다.")
            // 권한 요청
            ActivityCompat.requestPermissions(
                nowActivity,
                temp.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray(),
                0)
        }else{
            runFunction()
        }
    }

    fun setStackChip(
        context: Context,
        addItem: AddListItem
    ): Chip {
        val chipView = Chip(context)
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

    fun returnStackChipWithPF(
        context: Context,
        addItem: String
    ): Chip {
        val chipView = Chip(context)
        chipView.text = addItem
        chipView.setChipBackgroundColorResource(R.color.green_300)
        chipView.chipStrokeColor = getColorStateList(context, R.color.green_900)
        chipView.chipStrokeWidth = dpToPx(context, 1f)
        chipView.chipCornerRadius = dpToPx(context, 6f)
        chipView.setTextColor(getColor(context, R.color.black_900))
        chipView.setTextAppearance(R.style.Text_Body4_SemiBold)
        return chipView
    }

    fun changeItem2False(allStackList: HashMap<Int, ArrayList<AddListItem>>, addItem: AddListItem){
        for(i in allStackList.values){
            for(j in i){
                if(j == addItem){
                    j.isSelected = false
                    return
                }
            }
        }
    }

    fun changeItem2True(allStackList: HashMap<Int, ArrayList<AddListItem>>, name: String): AddListItem?{
        for(i in allStackList.values){
            for(j in i){
                if(j.name == name){
                    j.isSelected = true
                    return j
                }
            }
        }
        return null
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
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

    fun getInfoFromUri(context: Context, uri: Uri?): List<String> {
        val cursor: Cursor = context.contentResolver.query(uri!!, null, null, null, null)!!
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor))
        cursor.moveToFirst()

        val pathIdx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val fileName = cursor.getString(pathIdx)

        val sizeIdx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)
        val finalSize = cursor.getString(sizeIdx)

        Log.d("testImagePath + Size", "$fileName + $finalSize")
        return listOf(fileName, finalSize)
    }

    /* 백그라운드에서 서버 이미지 Url로 기기에 저장하는 메소드 */
    fun createCopyAndReturnPath(context:Context, uri: Uri, fileName: String): String{
        val contentResolver: ContentResolver = context.contentResolver
        // 내부 저장소 안에 위치하도록 파일 생성
        val filePath: String =
            context.applicationInfo.dataDir + File.separator + SimpleDateFormat(
                "HHmmss",
                Locale.getDefault()
            ).format(Date()) + "-" + fileName.replace("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9.]".toRegex(), "_")
        val file = File(filePath)

        val inputStream = contentResolver.openInputStream(uri)!!
        val outputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return filePath
    }

    public fun createAllStackHashMap(resources: Resources): LinkedHashMap<Int, ArrayList<AddListItem>> {
        val allStackList = LinkedHashMap<Int, ArrayList<AddListItem>>()
        allStackList[-1] = ArrayList<AddListItem>()

        val pmStackStringList = resources.getStringArray(R.array.pm_stack);
        allStackList[0] = ArrayList<AddListItem>()
        for(i in pmStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[0]?.add(AddListItem(false, stackName, stackInt))
        }

        val designStackStringList = resources.getStringArray(R.array.design_stack);
        allStackList[1] = ArrayList<AddListItem>()
        for(i in designStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[1]?.add(AddListItem(false, stackName, stackInt))
        }

        val frontStackStringList = resources.getStringArray(R.array.front_stack);
        allStackList[2] = ArrayList<AddListItem>()
        for(i in frontStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[2]?.add(AddListItem(false, stackName, stackInt))
        }

        val backStackStringList = resources.getStringArray(R.array.back_stack);
        allStackList[3] = ArrayList<AddListItem>()
        for(i in backStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[3]?.add(AddListItem(false, stackName, stackInt))
        }

        val etcStackStringList = resources.getStringArray(R.array.etc_stack);
        allStackList[4] = ArrayList<AddListItem>()
        for(i in etcStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[4]?.add(AddListItem(false, stackName, stackInt))
        }
        return allStackList
    }

}