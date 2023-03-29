package com.example.codev.addpage

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.codev.databinding.ProfileAddImageLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.util.*


class AddImage {

    //카메라 권한 확인
    fun checkCameraPermission(context: Context, nowActivity: AppCompatActivity, runFunction: () -> Unit) {
        var temp = ""

        //카메라 권한 확인
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.CAMERA + " "
        }
        if (!TextUtils.isEmpty(temp)) {
            Log.d("permission", "checkCameraPermission: 권한을 요청합니다.")
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

    //사진 라이브러리 권한 확인
    fun checkPhotoLibraryPermission(context: Context, nowActivity: AppCompatActivity) {
        var temp = ""

        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
        }

        if (!TextUtils.isEmpty(temp)) {
            Log.d("permission", "checkPhotoLibraryPermission: 권한을 요청합니다.")
            // 권한 요청
            ActivityCompat.requestPermissions(
                nowActivity,
                temp.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray(),
                1)
        }
    }

    //bitmap를 입력하고 파일 경로 반환
    fun createImageCachePath(context:Context, bitmap: Bitmap, quality: Int): String{
        val cachePath = File(context.externalCacheDir, "addImages")
        if(!cachePath.exists()) cachePath.mkdirs() // don't forget to make the directory
        val imageCachePath  ="$cachePath/${getTodayMillis()}.png"
        val stream = FileOutputStream(imageCachePath) // overwrites this image every time
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
        stream.close()
        return imageCachePath;
    }

    //uri 파일의 크기를 반환
    fun getUriSize(context: Context, uri: Uri?): String {
        val cursor: Cursor = context.contentResolver.query(uri!!, null, null, null, null)!!
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor))
        cursor.moveToFirst()

        val pathIdx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val fileName = cursor.getString(pathIdx)

        val sizeIdx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)
        val finalSize = cursor.getString(sizeIdx)

        Log.d("testImagePath + Size", "$fileName + $finalSize")
        return finalSize
    }

    //uri를 받고 실행할 콜백 함수: uri받고 크기를 확인하고 임시 경로를 반환해준다.
    fun getCachePathUseUri(context: Context, uri: Uri, quality: Int, sizeLimit: Double): String{
        val cR: ContentResolver = context.contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        var type: String? = cR.getType(uri)

        return if(type == "image/png" || type == "image/jpeg"){
            val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            if(bitmap.byteCount <= sizeLimit){
                createImageCachePath(context, bitmap, quality)
            }else {
                Toast.makeText(context, "사진 사이즈가 너무 큽니다.", Toast.LENGTH_SHORT).show()
                Log.d("TestImagePath", "getCachePathUseUri: 사진 사이즈가 너무 큽니다.")
                ""
            }
        }else{
            Toast.makeText(context, "png 및 jpg(jpeg)형식의 사진만 지원합니다.", Toast.LENGTH_SHORT).show()
            Log.d("TestImagePath", "getCachePathUseUri: png 및 jpg(jpeg)형식의 사진만 지원합니다" + type)
            ""
        }
    }

    //카메라로 사진을 찍기 위한 임시 파일 경로 및 url를 만들기
    fun returnEmptyUri(context: Context): Uri {
        val cachePath = File(context.externalCacheDir, "addImages")
        if(!cachePath.exists()) cachePath.mkdirs() // don't forget to make the directory
        val imageCachePath  ="$cachePath/${getTodayMillis()}.jpeg"
        val file = File(imageCachePath)
        file.createNewFile()
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    //오늘의 밀리세컨드 반환 함수
    private fun getTodayMillis(): Long{
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Log.d("testTime", "getTodayMillis:${calendar.time} ")
        return System.currentTimeMillis() - calendar.timeInMillis;
    }

}