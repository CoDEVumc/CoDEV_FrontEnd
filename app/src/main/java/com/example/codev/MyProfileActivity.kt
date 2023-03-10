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
        viewBinding.toolbarMy.toolbarText.text = "??? ????????? ??????"
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


        //?????? ??????
        viewBinding.warnName.isGone = true
        val nameRegex = Regex("""^[???-???|???-???|a-z|A-Z|]+${'$'}""")
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

        //????????? ??????
        viewBinding.warnNickname.isGone = true
        val nickNameRegex = Regex("""^[???-???|???-???|a-z|A-Z|0-9|]+${'$'}""")
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
            //??????????????? ????????????
            getBottomMenu()
        }

        //????????? ?????? ???????????? ?????? ??????
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
                Toast.makeText(this, "png ??? jpg(jpeg)????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
            }


        }
    }
    //imageSection - End

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadUserInfo(context: Context){
        //?????? ????????????
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)

        val finalName = viewBinding.etName.text.toString()
        val finalNickname = viewBinding.etNickname.text.toString()

        //????????? ?????? ????????? ???????????????
        val finalUserChangedClass = ReqUserInfoChanged(finalName, finalNickname)
        Log.d("finalUserChangedClass", "$finalUserChangedClass")
        val jsonObject = Gson().toJson(finalUserChangedClass)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)

        //?????? ???????????? ??? ???????????????
        val emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("file", null, emptyFileBody)
        var finalFile = emptyFilePart
        if(!isDefaultImg){
            //?????? ???????????? ????????? ?????????????????? ?????????
            if(selectedImageItem!!.imageUri == Uri.EMPTY){
                Glide.with(this).asFile().load(selectedImageItem!!.imageUrl).into(object : CustomTarget<File>(){
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        val filename = selectedImageItem!!.imageUrl.split("http://semtle.catholic.ac.kr:8080/image?name=", limit = 2)[1]
                        Log.d("filename", "run: filename is: $filename")
                        val newNameFile = File(resource.parent!!, filename)
                        resource.renameTo(newNameFile)
                        selectedImageItem = ImageItem(Uri.EMPTY, newNameFile.path, "")
                        //????????? ??????????????? ?????????
                        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),newNameFile)
                        val filePart = MultipartBody.Part.createFormData("file", newNameFile.name.replace("[^???-??????-??????-???a-zA-Z0-9.]".toRegex(), "_"), fileBody)
                        finalFile = filePart
                        Log.d("TestUploadNewImage", "OldImageUploaded ")
                        uploadUserChange(context, userToken, requestBody, finalFile)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Toast.makeText(context, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                    }
                })
            }else{ //?????? ???????????? ?????????, ?????? ???????????? ????????? ????????? -> ????????? ?????? -> copyPath??? ?????? ??????
                val fileImage = File(selectedImageItem!!.imageCopyPath)
                val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileImage)
                val filePart = MultipartBody.Part.createFormData("file", fileImage.name.replace("[^???-??????-??????-???a-zA-Z0-9.]".toRegex(), "_"), fileBody)
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
        //retrofit?????? ????????? ?????? ????????????
        RetrofitClient.service.changeUserInfo(userToken, requestBody, imageFile).enqueue(object :
            Callback<ResUserInfoChanged> {
            override fun onResponse(
                call: Call<ResUserInfoChanged>,
                response: Response<ResUserInfoChanged>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("fail", "?????? ?????? ?????? ????????? ??????: ${response.toString()}")
                    Toast.makeText(context, "????????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                    viewBinding.btnSave.isEnabled = true
                    viewBinding.btnSave.isSelected = true
                }
                when(response.code()){
                    200 -> {
                        response.body().let {
                            Log.d("success", "?????? ?????? ?????? ????????? ??????: ${it.toString()}")
                            if(!isDefaultImg){
                                //??????????????? ????????? ??????
                            }
                            finish()
                        }
                    }
                }

            }

            override fun onFailure(call: Call<ResUserInfoChanged>, t: Throwable) {
                Log.d("uploadUserInfoFail", "onFailure: $t")
                Toast.makeText(context, "????????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                viewBinding.btnSave.isEnabled = true
                viewBinding.btnSave.isSelected = true

            }
        })
    }

    private fun getBottomMenu(){
        // BottomSheetDialog ?????? ??????. param : Context
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
        dialog.setCanceledOnTouchOutside(true)  // BottomSheetdialog ?????? ??????(??????) ?????? ??? ?????? ?????? boolean(false : ??????, true : ????????????!)
        dialog.create() // create()??? show()??? ?????? ??????!
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
                    // ????????? ?????? ??????/?????? ?????? ????????? ??????
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }
                    // ????????? ????????? ?????? ????????????.
                    if (isAllGranted) {
                        // ?????? step?????? ~
                        openCamera()
                    }
                    // ???????????? ?????? ????????? ??????. ????????????/???????????? ????????? ????????? ?????? ????????? ???????????? ???.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)){
                            //?????? ??????????????? ?????? ?????? ????????? ?????????.
                            Toast.makeText(this, "???????????? ????????? ????????? ?????? ??????????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                            startActivity(intent)
                        } else {
                            //?????? ?????? ???????????????.
                            Toast.makeText(this, "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        }

    }

    //3. ????????? OPEN
    private fun openCamera() {
        //MediaStore.ACTION_IMAGE_CAPTURE
        //  - ??? Intent filter??? ???????????? camea app??? ???????????? ??? ??????.
        //intent.resolveActivity(packageManager)
        //  - ?????? ?????? MediaStore.ACTION_IMAGE_CAPTURE  ????????? ?????? ????????? ?????? ??? ??????
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //???????????? ????????? ????????? ????????? ???????????????.
        //Environment.DIRECTORY_PICTURES: ????????? ?????? ??? ??? ?????? ?????? ????????? ??????
        //   - val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //externalCacheDir :  ?????? ?????????
        val dir = externalCacheDir

        //File.createTempFile : ??????????????? (photo_ + random ?????? + .jpg)
        val file = File.createTempFile("photo_", ".jpg", dir)

        //photoFile
        //  - onActivityResult( )??? callback???????????? ????????? file id
        cameraFile = file

        //FileProvider.getUriForFile : provider??? ?????? uri??? ?????? ??????.
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        //startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
        //getResult.launch()??? ?????? ??????
        //  - callback?????? registerForActivityResult( )??? onActivityResult( ) ????????? ?????? ??????
        //  - ActivityResultLauncher class ?????? ?????? ????????? requestCode ????????? ?????? ?????? ??????. ?????? ?????? ????????? ?????? ??????.
        //    ?????????, registerForActivityResult( ) ??????????????? ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ????????????.
        //  - requestCode??? ????????? ??? ?????? ????????? onActivityResult( ) ?????? ????????? ????????? ?????? ??? ??? ?????? ?????????.
        //  - ????????? Activity Request?????? registerForActivityResult??? ???????????? ????????? requestCode??? ?????? ?????? ?????????.
        getResult.launch(intent)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val uri = cameraFile.toUri()
            checkNextBtn()
            val nowImageItem = ImageItem(uri, cameraFile.path)

            if(selectedImageItem.imageUri != Uri.EMPTY){ //?????? ????????? ????????? ?????? ????????? ?????? ??????
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
            Toast.makeText(this, "?????? ???????????????", Toast.LENGTH_SHORT).show()
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