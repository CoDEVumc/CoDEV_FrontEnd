package com.codev.android.addpage

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.codev.android.BuildConfig
import com.codev.android.R
import com.codev.android.databinding.ActivityAddPostBinding
import com.codev.android.databinding.ProfileAddImageLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MultipartBody
import java.io.File
import java.util.ArrayList

//Intent로 이 페이지를 호출할 때 다음과 같은 데이터를 추가하고 호출하세요
//1. postType: String -> 작성할 글의 유형: "info"==정보글, "qna"==질문글
//2. isOld: Boolean -> "게시글 새로 작성" or "기존 게시글 수정" 모드 선택
//isOld가 false일 때(게시글 새로 작성 모드) 2번까지만 보내주시면 됩니다!
//아래는 예시:
//val intent = Intent(this@MainAppActivity, AddPostActivity::class.java)
//intent.putExtra("postType", "qna")
//intent.putExtra("isOld", false)
//startActivity(intent)
//------------------------------------------------------------------
//isOld가 true일 경우(기존 게시글 수정 모드)에는 아래 3, 4, 5, 6번 정보도 같이 넘겨줘야 합니다.
//3. postId: Int -> 기존 게시글의 id를 알려주세요
//4. postTitle: String -> 기존 게시글의 제목
//5. postContent: String -> 기존 게시글의 내용
//6. postImageUrlsString: String -> 기존 게시글의 사진Url들을 콤마(,)로 구분된 String으로 보내주세요
//6-1 => 사진이 없으면 ""만 보내주새요 => 예시: intent.putExtra("postImageUrlsString", "")
//아래는 예시:
//val intent = Intent(this@MainAppActivity, AddPostActivity::class.java)
//intent.putExtra("postType", "info")
//intent.putExtra("isOld", true)
//intent.putExtra("postId", 9)
//intent.putExtra("postTitle", "이것이 수정된 정보글의 긴 제목입니다. 생각보다 길게")
//intent.putExtra("postContent", "여기는 수정된 정보글의 내용입니다. 이 내용이 매우매우매우 길기 때문에 테스트하기 좋습니다. 여기는 수정된 정보글의 내용입니다. 이 내용이 매우매우매우 길기 때문에 테스트하기 좋습니다. 여기는 수정된 정보글의 내용입니다. 이 내용이 매우매우매우 길기 때문에 테스트하기 좋습니다. 여기는 수정된 정보글의 내용입니다. 이 내용이 매우매우매우 길기 때문에 테스트하기 좋습니다. ")
//intent.putExtra("postImageUrlsString", "http://semtle.catholic.ac.kr:8080/image?name=633400420230410014714.png,http://semtle.catholic.ac.kr:8080/image?name=642900220230410014714.png")
//startActivity(intent)

class AddPostActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddPostBinding

    private var addPageFunction = AddPageFunction()
    var project2Server = Project2Server()

    //Important! 페이지 활성화 전에 항상 체크해야 할 값
    private var postType = "info" // "info"(정보글) or "question"(질문글),
    private var isOld = false
    private var oldPostId = 0
    private var isLoaded = false
    //Important! 페이지 활성화 전에 항상 체크해야 할 값

    //imageVar
    private val imageLimit = 5
    var imageItemList = ArrayList<ImageItem>()

    //upload조건
    private var isTitleOk = false
    private var isContentOk = false

    //Camera
    private var tempCameraFile = File("")
    private var tempUri = Uri.EMPTY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //기존 정보 가져오기 -> postType, isOld, oldPostId결정
        var oldInfoList = getInfoFromIntent(intent)
        initActivity()
        if(isOld){
            setOldInfo(oldInfoList[0], oldInfoList[1], oldInfoList[2])
        }

        //setSubmitButton
        viewBinding.submitButton.setOnClickListener {
            submitPost()
        }
    }

    // setBackButton
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ImageSection - Start
    override fun onDestroy() {
        super.onDestroy()
        for (i in imageItemList){
            Log.d("delete", i.toString())
            val deleteFile = File(i.imageCopyPath)
//            deleteFile.delete()
        }
    }
    //ImageSection - End

    private fun getInfoFromIntent(intent: Intent): List<String>{ //잔환할 리스트: [제목, 상세, 이미지UrlListString]
        //1. get post type
        postType = intent.getStringExtra("postType").toString()
        isOld = intent.getBooleanExtra("isOld", false)
//        initActivity()
        //2. getInfo
        val returnList = mutableListOf<String>()
        if(isOld){
            oldPostId = intent.getIntExtra("postId", 0)
            returnList.add(intent.getStringExtra("postTitle").toString())
            returnList.add(intent.getStringExtra("postContent").toString())
            returnList.add(intent.getStringExtra("postImageUrlsString").toString())
        }
        return returnList
    }

    private fun initActivity(){
        //가운데 정렬 글 작성 예시
        var postTypeString = "정보글"
        if(postType != "info"){
            postTypeString = "질문글"
            viewBinding.inputOfTitleLayout.hint = "Q. 질문"
        }
        val postModeString = if(isOld) "수정" else "작성";
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = "${postTypeString} ${postModeString}"
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        //TitleSection - Start
        val titleLimit = 30
        viewBinding.inputOfTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrBlank()) {
                    viewBinding.inputOfTitleLayout.error = "제목을 입력하세요."
                    isTitleOk = false
                }else if(s.length > titleLimit){
                    viewBinding.inputOfTitleLayout.error = "제목이 ${titleLimit}자를 초과할 수 없습니다."
                    isTitleOk = false
                }else{
                    viewBinding.inputOfTitleLayout.error = null
                    isTitleOk = true
                }
            }
        })
        //TitleSection - End

        //contentSection - Start
        val contentLimit = 500
        viewBinding.contentTextCounter.text = "0/${contentLimit}"
        viewBinding.inputOfContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrBlank()) {
                    viewBinding.contentTextCounter.text = "0/${contentLimit}"
                    viewBinding.inputOfContent.error = "상세 설명을 입력하세요."
                    isContentOk = false
                }else {
                    viewBinding.contentTextCounter.text = "${s!!.length}/${contentLimit}"
                    if(s.length > contentLimit){
                        viewBinding.inputOfContent.error = "상세 설명이 ${contentLimit}자를 초과할 수 없습니다."
                        isContentOk = false
                    }else{
                        viewBinding.inputOfContent.error = null
                        isContentOk = true
                    }
                }
            }
        })

        //addImageSection - Start
        viewBinding.addImageSection.adapter = ImageRVAdapter(this, imageItemList) {
            //subImageCounter
            viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
        }
        viewBinding.addImageSection.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewBinding.addImageSection.setHasFixedSize(true)
        viewBinding.addImageButton.setOnClickListener {
            if(imageItemList.size == imageLimit){
                Toast.makeText(this, "사진은 최대 ${imageLimit}개를 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }else {
                addPageFunction.checkSelfPermission(this, this)
                getBottomMenu()
            }
        }
        //addImageSection - End
        //TODO: 이미지 추가하기 기능을 따로 파일으로 빼놓기(팝업창, 권한 설정 등 모두 빼고, 반환값을 imamgList으로 통일)

    }

    private fun setOldInfo(oldTitle: String, oldContent: String, ImageUrlListString: String){
        //setTitle
        viewBinding.inputOfTitle.setText(oldTitle)
        //setDes
        viewBinding.inputOfContent.setText(oldContent)

        //setOldImage
        //TODO: 기존 이미지를 표시만 하고, 업로드 버튼이 누르면 그때 다시 다운로드하고 업로드하는 방식으로 하자
        if(ImageUrlListString != ""){
            val imageUrl = ImageUrlListString.split(",")
            for(nowUrl in imageUrl){
                val nowImageItem = ImageItem(Uri.EMPTY, "", nowUrl)
                imageItemList.add(nowImageItem)
                viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
                //subImageCounter
                viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
            }
        }
    }

    private fun submitPost(){
        changeButtonStatus(false)
        if(isTitleOk && isContentOk){
            //업로드 가능인 상태임
            val finalTitle = viewBinding.inputOfTitle.text.toString()
            Log.d("finalTitle", finalTitle)
            val finalDes = viewBinding.inputOfContent.text.toString()
            Log.d("finalDes", finalDes)

            if(isOld){
                val imageFilePathList = ArrayList<String>()
                //getOldImageNumber
                var allUrlNumber = 0
                var loadedImageNumber = 0

                for(i in imageItemList){
                    if(i.imageCopyPath == "") allUrlNumber+=1
                }

                for(i in imageItemList){
                    val nowIdx = imageItemList.indexOf(i)
                    if(i.imageCopyPath != "") imageFilePathList.add(i.imageCopyPath)
                    else{
                        Glide.with(this).asBitmap().load(i.imageUrl).into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                imageFilePathList.add(AddImage().createImageCachePath(this@AddPostActivity, resource, 50))
                                loadedImageNumber += 1
                                if(loadedImageNumber == allUrlNumber){
                                    val imageMultiPartList = project2Server.createImageMultiPartList(imageFilePathList)
                                    if(postType == "info"){
                                        uploadInfo(this@AddPostActivity, oldPostId.toString(), finalTitle, finalDes, imageMultiPartList)
                                    }
                                    else{
                                        uploadQNA(this@AddPostActivity, oldPostId.toString(), finalTitle, finalDes, imageMultiPartList)
                                    }
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                Log.e("oldtestimage", "onLoadCleared: ")
                            }
                        })

                        //TODO: 옛날 사진을 파일 형식으로 다운로드하고, 모든 옛날 사진들이 다운로드했으면
                        // -> {그 파일들을 가지고 멀티파트로 바꾸고 올리자} -> {}는 하나의 함수로 콜백 형식으로 glide안에 실행하자
                    }
                }

                if(allUrlNumber == 0){
                    val imageMultiPartList = project2Server.createImageMultiPartList(imageFilePathList)
                    if(postType == "info"){
                        uploadInfo(this@AddPostActivity, oldPostId.toString(), finalTitle, finalDes, imageMultiPartList)
                    }
                    else{
                        uploadQNA(this@AddPostActivity, oldPostId.toString(), finalTitle, finalDes, imageMultiPartList)
                    }
                }
            }else{
                var imageMultiPartList = listOf<MultipartBody.Part>()
                val finalImagePathList = ArrayList<String>()
                for(i in imageItemList) finalImagePathList.add(i.imageCopyPath)
                imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
                if(postType == "info"){
                    uploadInfo(this, "0", finalTitle, finalDes, imageMultiPartList)
                }
                else{
                    uploadQNA(this, "0", finalTitle, finalDes, imageMultiPartList)
                }
            }

        }else{
            //업로드 불가, 사용자에게 알리고 버튼 해제
            Toast.makeText(this, "제목 또는 상세 설명을 확인하세요.", Toast.LENGTH_LONG).show()
            //버튼 해제
            changeButtonStatus(true)
        }
    }

    //Bottom DiaLog - Start
    private fun getBottomMenu(){
        // BottomSheetDialog 객체 생성. param : Context
        val dialog = BottomSheetDialog(this)
        val dialogLayout = ProfileAddImageLayoutBinding.inflate(layoutInflater)
        dialogLayout.cameraSection.setOnClickListener {
            Log.d("testClick", "getBottomMenu: Click Camera")
            getImageFromCamera()
            dialog.dismiss()
        }
        dialogLayout.imageSection.setOnClickListener {
            getImageFromFile.launch("image/*")
            dialog.dismiss()
        }
        dialogLayout.cancelButton.setOnClickListener {
            dialog.cancel()
        }
        dialogLayout.defaultSection.visibility = View.GONE
        dialog.setContentView(dialogLayout.root)
        dialog.setCanceledOnTouchOutside(true)  // BottomSheetdialog 외부 화면(회색) 터치 시 종료 여부 boolean(false : ㄴㄴ, true : 종료하자!)
        dialog.create() // create()와 show()를 통해 출력!
        dialog.show()
    }

    //imageSection - Start

    private val getImageFromFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null){
            val copyImagePath = AddImage().getCachePathUseUri(this, uri, 50, 2e+7)
            if(copyImagePath != "") addImage2List(uri, copyImagePath)
            else Toast.makeText(this, "20MB이하 크기의 PNG, JPEG파일만 지원됩니다. ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addImage2List(imageUri: Uri, copyImagePath: String){
        Log.d("test", "newPath: $copyImagePath")
        val nowImageItem = ImageItem(imageUri, copyImagePath)
        imageItemList.add(nowImageItem)
        viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
        //subImageCounter
        viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
    }
    //imageSection - End

    private fun getImageFromCamera(){
        AddImage().checkCameraPermission(this, this){openCamera()}
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
                                .setData(Uri.parse("package:" + com.codev.android.BuildConfig.APPLICATION_ID))
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

    //카메라 OPEN
    private fun openCamera() {
        tempUri = AddImage().returnEmptyUri(this)
        getImageFromCameraCallback.launch(tempUri)
    }

    private val getImageFromCameraCallback = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if(it){
            val copyImagePath = AddImage().getCachePathUseUri(this, tempUri, 50, 2e+7)
            if(copyImagePath != "") addImage2List(tempUri, copyImagePath)
            else Toast.makeText(this, "사진을 추가하는데 알 수 없는 이유로 실패했습니다.", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
    //Bottom DiaLog - End

    private fun uploadInfo(context: Context, OLD_PROJECT_ID: String, POST_TITLE: String, POST_CONTENT: String, IMAGE_MULTIPART_LIST: List<MultipartBody.Part>){
        if(OLD_PROJECT_ID == "0"){ // 게시글 수정이 아니면
            project2Server.postNewInfo(this, POST_TITLE, POST_CONTENT, IMAGE_MULTIPART_LIST,
                { isPostSuccess() }, { isPostFail() })
            Log.d("testAddPostMode", "새로운 게시글 작성")
            Log.d("testAddPostString", POST_TITLE + POST_CONTENT)
            Log.d("testAddPostImage", IMAGE_MULTIPART_LIST.toString())
        }else{
            project2Server.updateInfo(this, OLD_PROJECT_ID, POST_TITLE, POST_CONTENT, IMAGE_MULTIPART_LIST,
                { isPostSuccess() }, { isPostFail() })
            Log.d("testUpdatePostMode", "기존 게시글 수정")
            Log.d("testUpdatePostString", POST_TITLE + POST_CONTENT)
            Log.d("testUpdatePostImage", IMAGE_MULTIPART_LIST.toString())
        }
    }

    private fun uploadQNA(context: Context, OLD_PROJECT_ID: String, POST_TITLE: String, POST_CONTENT: String, IMAGE_MULTIPART_LIST: List<MultipartBody.Part>){
        if(OLD_PROJECT_ID == "0"){ // 게시글 수정이 아니면
            project2Server.postNewQNA(this, POST_TITLE, POST_CONTENT, IMAGE_MULTIPART_LIST,
                { isPostSuccess() }, { isPostFail() })
            Log.d("testAddPostMode", "새로운 게시글 작성")
            Log.d("testAddPostString", POST_TITLE + POST_CONTENT)
            Log.d("testAddPostImage", IMAGE_MULTIPART_LIST.toString())
        }else{
            project2Server.updateQNA(this, OLD_PROJECT_ID, POST_TITLE, POST_CONTENT, IMAGE_MULTIPART_LIST,
                { isPostSuccess() }, { isPostFail() })
            Log.d("testUpdatePostMode", "기존 게시글 수정")
            Log.d("testUpdatePostString", POST_TITLE + POST_CONTENT)
            Log.d("testUpdatePostImage", IMAGE_MULTIPART_LIST.toString())
        }
    }

    private fun isPostSuccess(){
        finish()
    }

    private fun isPostFail(){
        Toast.makeText(this, "서버와 연결하는데 실패했습니다. 잠시 후 다시 한 번 시도해보세요.", Toast.LENGTH_SHORT).show()
        changeButtonStatus(true)
    }



    private fun changeButtonStatus(nowStatus: Boolean){
        viewBinding.submitButton.isEnabled = nowStatus
        viewBinding.submitButton.isSelected = nowStatus

        viewBinding.inputOfTitle.isFocusable = nowStatus
        viewBinding.inputOfTitle.isSelected = nowStatus

        viewBinding.inputOfContent.isFocusable = nowStatus
        viewBinding.inputOfContent.isSelected = nowStatus

        viewBinding.addPhotoArea.isClickable = nowStatus
        viewBinding.addPhotoArea.isFocusable = nowStatus
        viewBinding.addPhotoArea.isSelected = nowStatus
    }

}