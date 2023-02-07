package com.example.codev

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.codev.addpage.AddPageFunction
import com.example.codev.addpage.ImageItem
import com.example.codev.databinding.ActivityRegisterProfileBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RegisterProfileActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterProfileBinding
    var addPageFunction = AddPageFunction()
    private lateinit var reqSignUp: ReqSignUp
    private var copyImagePath: String = ""
    private var file: MultipartBody.Part = MultipartBody.Part.createFormData("files", null, RequestBody.create(MediaType.parse("application/octet-stream"), ""))

    override fun onDestroy() {
        super.onDestroy()
        if (!copyImagePath.isNullOrBlank()){
            val deleteFile = File(copyImagePath)
            deleteFile.delete()
            Log.d("test","이미지 삭제")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp

        viewBinding.etNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    if (p0.length <= 8){
                        nextBtnEnable(true)
                    }else{
                        nextBtnEnable(false)
                    }
                    viewBinding.count.text = (8 - p0.length).toString()
                }
            }
        })

        //프로필 이미지 변경 필요
        viewBinding.btnChange.setOnClickListener {
            addPageFunction.checkSelfPermission(this, this)
            getContent.launch(arrayOf(
                "image/png",
                "image/jpg",
                "image/jpeg"
            ))
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            reqSignUp.co_nickName = viewBinding.etNickname.text.toString()
            var requestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(reqSignUp))
            signUp(this,requestBody,file)
        }
    }

    private fun signUp(context: Context, requestBody: RequestBody, file: MultipartBody.Part){
        RetrofitClient.service.signUp(requestBody,file).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 회원가입 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 회원가입 성공", "\n${it.toString()}")
                                val intent = Intent(context,RegisterDoneActivity::class.java)
                                finishAffinity()
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: 회원가입 실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnRegisterNext.isSelected != boolean){
            viewBinding.btnRegisterNext.isSelected = boolean
            viewBinding.btnRegisterNext.isEnabled = boolean
            if(boolean){
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.black_500))
            }
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if(uri != null){
            val imageInfo = addPageFunction.getInfoFromUri(this, uri)
            val imageName = imageInfo[0]
            val imageSize = imageInfo[1].toInt()
            val imageSizeLimitByte = 2e+7
            if(imageSize <= imageSizeLimitByte){
                copyImagePath = addPageFunction.createCopyAndReturnPath(this, uri, imageName)
                val nowImageItem = ImageItem(uri, copyImagePath)
                Glide.with(this)
                    .load(uri).circleCrop()
                    .into(viewBinding.profileImg)
                var fileFromPath = File(copyImagePath)
                var fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileFromPath)
                file = MultipartBody.Part.createFormData("file", fileFromPath.name, fileBody)
            }
        }
    }
}