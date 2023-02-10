package com.example.codev

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.codev.addpage.AddPageFunction
import com.example.codev.addpage.ImageItem
import com.example.codev.databinding.ActivityMyProfileBinding
import com.example.codev.databinding.ProfileAddImageLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.File

class MyProfileActivity:AppCompatActivity() {
    lateinit var viewBinding: ActivityMyProfileBinding
    private var isDefaultImg = false
    private val defaultImgUrl = "http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png"
    private var selectedImageItem: ImageItem? = null

    private var addPageFunction = AddPageFunction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "내 프로필 관리"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val userinfo = intent.getSerializableExtra("userinfo") as Userinfo
        viewBinding.etName.setText(userinfo.co_name)
        viewBinding.etNickname.setText(userinfo.co_nickName)
        Glide.with(this)
            .load(userinfo.profileImg).circleCrop()
            .into(viewBinding.profileImg)
        if(userinfo.profileImg == defaultImgUrl){
            isDefaultImg = true
        }
        else{
            selectedImageItem = ImageItem(Uri.EMPTY, "", userinfo.profileImg)
        }
        viewBinding.countName.text = (5 - viewBinding.etName.text.length).toString()
        viewBinding.countNickname.text = (8 - viewBinding.etNickname.text.length).toString()


        //이름 검토
        viewBinding.warnName.isGone = true
        val nameRegex = Regex("""^[ㄱ-ㅎ|가-힣|a-z|A-Z|]+${'$'}""")
        viewBinding.etName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewBinding.countName.text = (5 - viewBinding.etName.text.length).toString()
                if(viewBinding.etName.text.matches(nameRegex)){
                    viewBinding.warnName.isGone = true
                    viewBinding.etName.setBackgroundResource(R.drawable.login_et)
                }else{
                    viewBinding.warnName.isGone = false
                    viewBinding.etName.setBackgroundResource(R.drawable.login_et_failed)
                }
                checkNextBtn()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        //닉네임 검토
        viewBinding.warnNickname.isGone = true
        val nickNameRegex = Regex("""^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+${'$'}""")
        viewBinding.etNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.countNickname.text = (8 - viewBinding.etNickname.text.length).toString()
                if(viewBinding.etNickname.text.matches(nickNameRegex)){
                    viewBinding.warnNickname.isGone = true
                    viewBinding.etNickname.setBackgroundResource(R.drawable.login_et)
                }else{
                    viewBinding.warnNickname.isGone = false
                    viewBinding.etNickname.setBackgroundResource(R.drawable.login_et_failed)
                }
                checkNextBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })


        viewBinding.profileImg.setOnClickListener {
            //다이아로그 띄워주기
            getBottomMenu()
            //팝업창이 뜨면서 해당 작업으로 넘어가
//            addPageFunction.checkSelfPermission(this, this)
//            getContent.launch(arrayOf(
//                "image/png",
//                "image/jpg",
//                "image/jpeg"
//            ))
        }

        //프로필 변경 저장하기 기능 필요
        viewBinding.btnSave.setOnClickListener {
            uploadUserInfo(this)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //imageSection - Start
    private val getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if(uri != null){
            checkNextBtn()
            val imageInfo = addPageFunction.getInfoFromUri(this, uri)
            val imageName = imageInfo[0]
            val imageSize = imageInfo[1].toInt()
            val imageSizeLimitByte = 2e+7
            if(imageSize <= imageSizeLimitByte){
                val copyImagePath = addPageFunction.createCopyAndReturnPath(this, uri, imageName)
                val nowImageItem = ImageItem(uri, copyImagePath)

                //원래 사진을 확인
                if(isDefaultImg){ //원래 사진이 기본 사진이었으면
                    isDefaultImg = false
                }else if(selectedImageItem!!.imageUri != Uri.EMPTY){ //전에 선택된 사진이 원래 사진이 아닌 경우
                    File(selectedImageItem!!.imageCopyPath).delete()
                }
                selectedImageItem = nowImageItem
                Glide.with(this)
                    .load(uri).circleCrop()
                    .into(viewBinding.profileImg)
            }
        }
    }
    //imageSection - End

    //ImageSection - Start
    override fun onDestroy() {
        super.onDestroy()
        if(selectedImageItem!=null) File(selectedImageItem!!.imageCopyPath).delete()
    }
    //ImageSection - End

    private fun uploadUserInfo(context: Context){
        //토큰 가져오기
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)

        val finalName = viewBinding.etName.text.toString()
        val finalNickname = viewBinding.etNickname.text.toString()

        //텍스트 객체 만들고 인코딩하기
        val finalUserChangedClass = ReqUserInfoChanged(finalName, finalNickname)
        Log.d("finalUserChangedClass", "$finalUserChangedClass")
        val jsonObject = Gson().toJson(finalUserChangedClass)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)

        //사진 다운로드 및 인코딩하기
        val emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("file", null, emptyFileBody)
        var finalFile = emptyFilePart
        if(!isDefaultImg){
            //원래 사진이면 임시로 다운로드하고 업로드
            if(selectedImageItem!!.imageUri == Uri.EMPTY){
                Glide.with(this).asFile().load(selectedImageItem!!.imageUrl).into(object : CustomTarget<File>(){
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        val filename = selectedImageItem!!.imageUrl.split("http://semtle.catholic.ac.kr:8080/image?name=", limit = 2)[1]
                        Log.d("filename", "run: filename is: $filename")
                        val newNameFile = File(resource.parent!!, filename)
                        resource.renameTo(newNameFile)
                        selectedImageItem = ImageItem(Uri.EMPTY, newNameFile.path, "")
                        //파일을 인코딩하고 올리자
                        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),newNameFile)
                        val filePart = MultipartBody.Part.createFormData("file", newNameFile.name, fileBody)
                        finalFile = filePart
                        Log.d("TestUploadNewImage", "OldImageUploaded ")
                        uploadUserChange(context, userToken, requestBody, finalFile)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Toast.makeText(context, "모집글 수정 시 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            }else{ //기본 이미지도 아니고, 원래 수정전의 샂니도 아니고 -> 새로운 사진 -> copyPath이 있는 사진
                val fileImage = File(selectedImageItem!!.imageCopyPath)
                val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileImage)
                val filePart = MultipartBody.Part.createFormData("file", fileImage.name, fileBody)
                finalFile = filePart
                Log.d("TestUploadNewImage", "NewImageUploaded ")
                uploadUserChange(context, userToken, requestBody, finalFile)
            }
        }else{
            Log.d("TestUploadNewImage", "NoImageUploaded ")
            Log.d("TestUploadNewImage",  "${emptyFilePart.body()}")

            uploadUserChange(context, userToken, requestBody, emptyFilePart)
        }

    }

    private fun checkNextBtn() {
        if(viewBinding.warnName.isGone and viewBinding.warnNickname.isGone){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
    }

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnSave.isSelected != boolean){
            viewBinding.btnSave.isSelected = boolean
            viewBinding.btnSave.isEnabled = boolean
            if(boolean){
                viewBinding.btnSave.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnSave.setTextColor(getColor(R.color.black_500))
            }
        }
    }

    private fun uploadUserChange(context: Context, userToken: String, requestBody: RequestBody, imageFile: MultipartBody.Part){
        //retrofit으로 올리고 사진 삭제하기
        RetrofitClient.service.changeUserInfo(userToken, requestBody, imageFile).enqueue(object :
            Callback<ResUserInfoChanged> {
            override fun onResponse(
                call: Call<ResUserInfoChanged>,
                response: Response<ResUserInfoChanged>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("fail", "유저 변경 정보 업로드 실패: ${response.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body().let {
                            Log.d("success", "유저 변경 내용 업로드 성공: ${it.toString()}")
                            if(!isDefaultImg){
                                //업로드했던 이미지 삭제
                            }
                            finish()
                        }
                    }
                }

            }

            override fun onFailure(call: Call<ResUserInfoChanged>, t: Throwable) {
                Log.d("uploadUserInfoFail", "onFailure: $t")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun getBottomMenu(){
        // BottomSheetDialog 객체 생성. param : Context
        val dialog = BottomSheetDialog(this)

        val dialogLayout = ProfileAddImageLayoutBinding.inflate(layoutInflater)

        dialogLayout.cameraSection.setOnClickListener {
            Log.d("testClick", "getBottomMenu: Click Camera")


            isDefaultImg = false
            checkNextBtn()
            dialog.dismiss()
        }

        dialogLayout.imageSection.setOnClickListener {
            addPageFunction.checkSelfPermission(this, this)
            getContent.launch(arrayOf(
                "image/png",
                "image/jpg",
                "image/jpeg"
            ))
            isDefaultImg = false
            checkNextBtn()
            dialog.dismiss()
        }

        dialogLayout.defaultSection.setOnClickListener {
            Glide.with(this).load(defaultImgUrl).into(viewBinding.profileImg)
            isDefaultImg = true

            checkNextBtn()
            dialog.dismiss()

        }

        dialog.setContentView(dialogLayout.root)

// BottomSheetdialog 외부 화면(회색) 터치 시 종료 여부 boolean(false : ㄴㄴ, true : 종료하자!)
        dialog.setCanceledOnTouchOutside(true)

// create()와 show()를 통해 출력!
        dialog.create()
        dialog.show()
    }
}