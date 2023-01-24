package com.example.codev.addpage

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.codev.*
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class Project2Server {

    fun createPartNumList(
        pm: Int,
        design: Int,
        front: Int,
        back: Int,
        etc: Int
    ): List<PartNameAndPeople> {
        var partList = ArrayList<PartNameAndPeople>()
        partList.add(PartNameAndPeople("기획", pm))
        partList.add(PartNameAndPeople("디자인", design))
        partList.add(PartNameAndPeople("프론트엔드", front))
        partList.add(PartNameAndPeople("백엔드", back))
        partList.add(PartNameAndPeople("기타", etc))
        return partList.toList()
    }

    fun createImageMultiPartList(imagePathList: ArrayList<File>): List<MultipartBody.Part>{
        var fileMultipartList = ArrayList<MultipartBody.Part>()
        for (i in imagePathList) {
            var fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), i)
            val filePart = MultipartBody.Part.createFormData("files", i.name, fileBody)
            fileMultipartList.add(filePart)
        }
        return fileMultipartList.toList()
    }

    fun postNewProject(context: Context, title: String, content: String, location: String, stackList: List<Int>, deadLine: String, numPerPart: List<PartNameAndPeople>, imagePartList: List<MultipartBody.Part>){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqCreateNewProject = ReqCreateNewProject(
            title,
            content,
            location,
            stackList,
            deadLine,
            numPerPart
        )
        val jsonObject = Gson().toJson(reqCreateNewProject)
        var requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)
        Log.d("postJson", jsonObject.toString())

        var emptyList = ArrayList<MultipartBody.Part>()
        var emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("files", null, emptyFileBody)
        emptyList.add(emptyFilePart)

        var finalArray: List<MultipartBody.Part> = emptyList.toList()
        if(imagePartList.isNotEmpty()) {
            finalArray = imagePartList.toList()
        }

        RetrofitClient.service.createNewProject(userToken, requestBody, finalArray).enqueue(object:
            Callback<ResCreateNewProject> {
            override fun onResponse(
                call: Call<ResCreateNewProject>,
                response: Response<ResCreateNewProject>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("Fail",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }
                response.body()?.let { Log.d("Success: testCreateNewProject", "\n${it.toString()}")}
            }

            override fun onFailure(call: Call<ResCreateNewProject>, t: Throwable) {
                Log.d("FAIL", "[Fail]${t.toString()}")
            }
        })
    }
}
