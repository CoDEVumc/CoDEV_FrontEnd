package com.example.codev

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
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
    private var addPageFunction = AddPageFunction()

    private var selectedImageItem = ImageItem(Uri.EMPTY, "", defaultImgUrl)
    private var cameraFile = File("")

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
        }

        //프로필 변경 저장하기 기능 필요
        viewBinding.btnSave.setOnClickListener {
            uploadUserInfo(this)
        }

    }

    //ImageSection - Start
    override fun onDestroy() {
        super.onDestroy()
        if(File(selectedImageItem.imageCopyPath).exists()) File(selectedImageItem.imageCopyPath).delete()
        if(cameraFile.exists()) cameraFile.delete()
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null){
            val cR: ContentResolver = this.contentResolver
            val mime: MimeTypeMap = MimeTypeMap.getSingleton()
            val type: String? = cR.getType(uri)
            if(type == "image/png" || type == "image/jpg" || type == "image/jpeg"){
                checkNextBtn()
                val imageInfo = addPageFunction.getInfoFromUri(this, uri)
                val imageName = imageInfo[0]
                val imageSize = imageInfo[1].toInt()
                val imageSizeLimitByte = 2e+7
                if(imageSize <= imageSizeLimitByte){
                    val copyImagePath = addPageFunction.createCopyAndReturnPath(this, uri, imageName)
                    val nowImageItem = ImageItem(uri, copyImagePath)

                    if(File(selectedImageItem.imageCopyPath).exists()) File(selectedImageItem.imageCopyPath).delete()

                    selectedImageItem = nowImageItem
                    isDefaultImg = false
                    Glide.with(this)
                        .load(uri).circleCrop()
                        .into(viewBinding.profileImg)
                }
            }else{
                Toast.makeText(this, "png 및 jpg(jpeg)형식의 사진만 지원합니다.", Toast.LENGTH_SHORT).show()
            }


        }
    }
    //imageSection - End

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

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
                        val filePart = MultipartBody.Part.createFormData("file", newNameFile.name.replace("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9.]".toRegex(), "_"), fileBody)
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
                val filePart = MultipartBody.Part.createFormData("file", fileImage.name.replace("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9.]".toRegex(), "_"), fileBody)
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

    private fun uploadUserChange(context: Context, userToken: String, requestBody: RequestBody, imageFile: MultipartBody.Part){
        viewBinding.btnSave.isEnabled = false
        viewBinding.btnSave.isSelected = false
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
                    viewBinding.btnSave.isEnabled = true
                    viewBinding.btnSave.isSelected = true
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
                viewBinding.btnSave.isEnabled = true
                viewBinding.btnSave.isSelected = true

            }
        })
    }

    private fun getBottomMenu(){
        // BottomSheetDialog 객체 생성. param : Context
        val dialog = BottomSheetDialog(this)
        val dialogLayout = ProfileAddImageLayoutBinding.inflate(layoutInflater)
        dialogLayout.cameraSection.setOnClickListener {
            Log.d("testClick", "getBottomMenu: Click Camera")
            getImageFromCamera()
            isDefaultImg = false
            checkNextBtn()
            dialog.dismiss()
        }
        dialogLayout.imageSection.setOnClickListener {
            addPageFunction.checkSelfPermission(this, this)
            getContent.launch("image/*")

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
        dialogLayout.cancelButton.setOnClickListener {
            dialog.cancel()
        }
        dialog.setContentView(dialogLayout.root)
        dialog.setCanceledOnTouchOutside(true)  // BottomSheetdialog 외부 화면(회색) 터치 시 종료 여부 boolean(false : ㄴㄴ, true : 종료하자!)
        dialog.create() // create()와 show()를 통해 출력!
        dialog.show()
    }

    private fun getImageFromCamera(){
        addPageFunction.checkSelfCameraPermission(this, this) {openCamera()}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            0 -> {
                if (grantResults.isNotEmpty()){
                    var isAllGranted = true
                    // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }
                    // 요청한 권한을 모두 허용했음.
                    if (isAllGranted) {
                        // 다음 step으로 ~
                        openCamera()
                    }
                    // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)){
                            //권한 거부하면서 다시 묻지 않기를 체크함.
                            Toast.makeText(this, "설정에서 카메라 권한을 직접 추가하셔야 카메라를 실행할 수 있습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                            startActivity(intent)
                        } else {
                            //접근 권한 거부하였음.
                            Toast.makeText(this, "카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        }

    }

    //3. 카메라 OPEN
    private fun openCamera() {
        //MediaStore.ACTION_IMAGE_CAPTURE
        //  - 이 Intent filter를 이용하면 camea app을 실행시킬 수 있다.
        //intent.resolveActivity(packageManager)
        //  - 현재 폰에 MediaStore.ACTION_IMAGE_CAPTURE  기능이 존재 하는지 한번 더 확인
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //저장소에 저장할 파일을 임시로 만들어준다.
        //Environment.DIRECTORY_PICTURES: 사진을 저장 할 수 있는 일반 저장소 위치
        //   - val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //externalCacheDir :  캐시 저장소
        val dir = externalCacheDir

        //File.createTempFile : 파일명생성 (photo_ + random 숫자 + .jpg)
        val file = File.createTempFile("photo_", ".jpg", dir)

        //photoFile
        //  - onActivityResult( )인 callback함수에서 사용될 file id
        cameraFile = file

        //FileProvider.getUriForFile : provider에 대한 uri를 얻어 온다.
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        //startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
        //getResult.launch()을 호출 하면
        //  - callback으로 registerForActivityResult( )와 onActivityResult( ) 함수가 호출 된다
        //  - ActivityResultLauncher class 사용 이후 부터는 requestCode 코드를 넘길 수가 없다. 아니 넘길 필요가 없어 졌다.
        //    이유는, registerForActivityResult( ) 함수내부에 직접 기술 하는 방식으로 개발 하면 되기 때문이다.
        //  - requestCode를 지정할 수 없기 때문에 onActivityResult( ) 함수 내부에 소스를 개발 할 수 없게 되었다.
        //  - 원하는 Activity Request마다 registerForActivityResult를 실행하기 때문에 requestCode가 존재 하지 않는다.
        getResult.launch(intent)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val uri = cameraFile.toUri()
            checkNextBtn()
            val nowImageItem = ImageItem(uri, cameraFile.path)

            if(selectedImageItem.imageUri != Uri.EMPTY){ //전에 선택된 사진이 원래 사진이 아닌 경우
                File(selectedImageItem.imageCopyPath).delete()
                Log.d("TestPhoto", "Photo deleted")
            }
            selectedImageItem = nowImageItem
            isDefaultImg = false
            Glide.with(this)
                .load(uri).circleCrop()
                .into(viewBinding.profileImg)
//            Glide.with(this).load(cameraFile).circleCrop().into(viewBinding.profileImg)
        } else {
            Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_SHORT).show()
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
}