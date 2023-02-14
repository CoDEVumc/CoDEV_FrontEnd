package com.example.codev

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.codev.addpage.AddPageFunction
import com.example.codev.addpage.ImageItem
import com.example.codev.databinding.ActivityRegisterProfileBinding
import com.example.codev.databinding.ProfileAddImageLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private var emptyFile: MultipartBody.Part = MultipartBody.Part.createFormData("file", null, RequestBody.create(MediaType.parse("application/octet-stream"), ""))

    private var isDefaultImg = true
    private val defaultImgUrl = "http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png"
    private var selectedImageItem = ImageItem(Uri.EMPTY, "", defaultImgUrl)
    private var cameraFile = File("")

    override fun onDestroy() {
        super.onDestroy()
        if (!copyImagePath.isNullOrBlank()){
            val deleteFile = File(copyImagePath)
            deleteFile.delete()
            Log.d("test","이미지 삭제")
        }
        if(File(selectedImageItem.imageCopyPath).exists()) File(selectedImageItem.imageCopyPath).delete()
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
            getBottomMenu()
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            reqSignUp.co_nickName = viewBinding.etNickname.text.toString()
            var requestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(reqSignUp))
            if(isDefaultImg){
                signUp(this,requestBody,emptyFile)
            }else{
                val fileImage = File(selectedImageItem!!.imageCopyPath)
                val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileImage)
                val filePart = MultipartBody.Part.createFormData("file", fileImage.name.replace("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9.]".toRegex(), "_"), fileBody)
                signUp(this,requestBody,filePart)
            }
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

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null){
            val cR: ContentResolver = this.contentResolver
            val mime: MimeTypeMap = MimeTypeMap.getSingleton()
            val type: String? = cR.getType(uri)
            if(type == "image/png" || type == "image/jpg" || type == "image/jpeg"){
                val imageInfo = addPageFunction.getInfoFromUri(this, uri)
                val imageName = imageInfo[0]
                val imageSize = imageInfo[1].toInt()
                val imageSizeLimitByte = 2e+7
                if(imageSize <= imageSizeLimitByte){
                    copyImagePath = addPageFunction.createCopyAndReturnPath(this, uri, imageName)
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

    private fun getBottomMenu(){
        // BottomSheetDialog 객체 생성. param : Context
        val dialog = BottomSheetDialog(this)
        val dialogLayout = ProfileAddImageLayoutBinding.inflate(layoutInflater)
        dialogLayout.cameraSection.setOnClickListener {
            Log.d("testClick", "getBottomMenu: Click Camera")
            getImageFromCamera()
            isDefaultImg = false
            dialog.dismiss()
        }
        dialogLayout.imageSection.setOnClickListener {
            addPageFunction.checkSelfPermission(this, this)
            getContent.launch("image/*")

            isDefaultImg = false
            dialog.dismiss()
        }
        dialogLayout.defaultSection.setOnClickListener {
            Glide.with(this).load(defaultImgUrl).into(viewBinding.profileImg)
            isDefaultImg = true
            dialog.dismiss()
        }
        dialogLayout.DefaultImageText.text = "기본 이미지 사용"
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

}