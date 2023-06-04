package com.codev.android.addpage

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.codev.android.AndroidKeyStoreUtil
import com.codev.android.BuildConfig
import com.codev.android.R
import com.codev.android.databinding.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.util.*

class AddNewStudyActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddNewStudyBinding

    //functionVar
    var addPageFunction = AddPageFunction()
    var project2Server = Project2Server()

    //imageVar
    val imageLimit = 5
    var imageItemList = ArrayList<ImageItem>()

    //stackVar
    var allStackList = LinkedHashMap<Int, java.util.ArrayList<AddListItem>>()
    var chipList = LinkedHashMap<AddListItem, View>()

    //DeadLineTimeVar
    private var dateJsonString: String = ""

    private var oldStudyId = ""

    //Camera
    private var tempCameraFile = File("")
    private var tempUri = Uri.EMPTY

    //모집 기간을 수정했는지 확인용
    private var isTimeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddNewStudyBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = getString(R.string.add_new_study)
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
        var isTitleOk = false
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

        //DesSection - Start
        val contentLimit = 500
        viewBinding.contentTextCounter.text = "0/${contentLimit}"
        var isContentOk = false
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

        viewBinding.inputOfContent.setOnTouchListener(View.OnTouchListener { v, event ->
            if (viewBinding.inputOfContent.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
        //DesSection - End

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

        // START-setNewStack2
        allStackList = addPageFunction.createAllStackHashMap(resources)

        var selectedStack1Index = -1

        viewBinding.stack2Head.dropdownTitle.text = "세부 기술을 선택하세요"

        viewBinding.stack2List.adapter = getStack2Adapter(allStackList[selectedStack1Index]!!)
        viewBinding.stack2List.layoutManager = LinearLayoutManager(this)
        var isStack2ListVisible = false
        viewBinding.stack2Head.root.setOnClickListener {
            if(!isStack2ListVisible){
                viewBinding.stack2List.visibility = View.VISIBLE
                isStack2ListVisible = true
            }else{
                viewBinding.stack2List.visibility = View.GONE
                isStack2ListVisible = false
            }
        }
        // END-setNewStack2

        // START-setNewStack1
        var isStack1ListVisible = false

        val stack1String = resources.getStringArray(R.array.stack1_list);
        val stack1List = java.util.ArrayList<AddListItem>()
        for(i in stack1String) stack1List.add(AddListItem(false, i, 0))
        viewBinding.stack1Head.dropdownTitle.text = "분야를 선택하세요"

        var stack1Adapter = CallbackSingleRVAdapter(stack1List, selectedStack1Index) {
            Log.d("stack1Index", it.toString())
            viewBinding.stack1Head.dropdownTitle.text = stack1List[it].name
            selectedStack1Index = it
            viewBinding.stack2List.adapter = getStack2Adapter(allStackList[it]!!)
            viewBinding.stack2List.visibility = View.VISIBLE
            isStack2ListVisible = true
        }

        viewBinding.stack1List.adapter = stack1Adapter
        viewBinding.stack1List.layoutManager = LinearLayoutManager(this)
        viewBinding.stack1Head.root.setOnClickListener{
            if(!isStack1ListVisible){
                viewBinding.stack1List.visibility = View.VISIBLE
                isStack1ListVisible = true
            }else{
                viewBinding.stack1List.visibility = View.GONE
                isStack1ListVisible = false
            }
        }
        // END-setNewStack1

        //AddPartPeople - Start
        addPageFunction.setAddSubButton(this, viewBinding.peopleSection)
        //AddPartPeople - End

        // START - setLocation
        var isLocationListVisible = false

        var locationString = resources.getStringArray(R.array.location_list);
        var locationList = ArrayList<AddListItem>()
        for(i in locationString) locationList.add(AddListItem(false, i, 0))
        var selectedLocationIndex = -1
        viewBinding.locationHead.dropdownTitle.text = "지역을 선택하세요"

        viewBinding.locationList.adapter = CallbackSingleRVAdapter(locationList, selectedLocationIndex) {
            Log.d("location", it.toString())
            viewBinding.locationHead.dropdownTitle.text = locationList[it].name
        }
        viewBinding.locationList.layoutManager = LinearLayoutManager(this)
        viewBinding.locationHead.root.setOnClickListener{
            if(!isLocationListVisible){
                viewBinding.locationList.visibility = View.VISIBLE
                isLocationListVisible = true
            }else{
                viewBinding.locationList.visibility = View.GONE
                isLocationListVisible = false
            }
        }
        // End - setLocationNew

        //setDeadLine - Start
        viewBinding.deadlineHead.dropdownTitle.text = "마감 일자를 선택하세요"
        viewBinding.deadlineHead.dropdownRound.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var dateShowString = "${year}/${month+1}/${dayOfMonth}"
                dateJsonString = String.format("%d-%02d-%02d", year, month+1, dayOfMonth)
                viewBinding.deadlineHead.dropdownTitle.text = dateShowString
                isTimeChanged = true
            }
            var dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.datePicker.maxDate = (System.currentTimeMillis() + 3.156e+10).toLong() //1년 설정
            dpd.show()
        }
        //setDeadLine - End

        var isOld = false
        if(intent.getSerializableExtra("study") != null){
            val oldStudy = intent.getSerializableExtra("study") as EditStudy

            //checkIsNew
            isOld = true
            oldStudyId = oldStudy.studyId
            Log.d("oldStudyId", oldStudyId)
            viewBinding.toolbarTitle.toolbarText.text = getString(R.string.edit_new_study)
            viewBinding.submitButton.text = "스터디 수정하기"

            //setTitle
            viewBinding.inputOfTitle.setText(oldStudy.title)

            //setDes
            viewBinding.inputOfContent.setText(oldStudy.content)

            //setImage
            for(nowUrl in oldStudy.imageUrl){
                val nowImageItem = ImageItem(Uri.EMPTY, "", nowUrl.co_fileUrl)
                imageItemList.add(nowImageItem)
                viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
                //subImageCounter
                viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
            }

            //setPartName(stack1Name)
            viewBinding.stack1Head.dropdownTitle.text = oldStudy.partName
            for (i in stack1List){
                if(i.name == oldStudy.partName){
                    selectedStack1Index = stack1List.indexOf(i)
                    i.isSelected = true
                    viewBinding.stack1List.adapter = CallbackSingleRVAdapter(stack1List, selectedStack1Index) {
                        Log.d("stack1Index", it.toString())
                        viewBinding.stack1Head.dropdownTitle.text = stack1List[it].name
                        selectedStack1Index = it
                        viewBinding.stack2List.adapter = getStack2Adapter(allStackList[it]!!)
                        viewBinding.stack2List.visibility = View.VISIBLE
                        isStack2ListVisible = true
                    }
                    viewBinding.stack2List.adapter = getStack2Adapter(allStackList[selectedStack1Index]!!)
                    break
                }
            }

            //setPartNumber
            ////setPartNumber
            viewBinding.peopleSection.peopleNum.text = oldStudy.partPeople.toString()
            if(oldStudy.partPeople.toString() != "0"){
                viewBinding.peopleSection.peopleNum.setTextColor(resources.getColor(R.color.black_900))
            }
            //=================================

            //setChipGroup
            for(i in oldStudy.languageIdNameMap.keys){
                val oldChip = AddListItem(false, oldStudy.languageIdNameMap[i]!!, i)
                val listItem = addPageFunction.changeItem2True(allStackList, oldStudy.languageIdNameMap[i]!!)
                addOrRemoveChip(listItem!!)
                viewBinding.stack2List.adapter!!.notifyDataSetChanged()
            }

            //setLocation
            viewBinding.locationHead.dropdownTitle.text = oldStudy.location
            for (i in locationList){
                if(i.name == oldStudy.location){
                    selectedLocationIndex = locationList.indexOf(i)
                    i.isSelected = true
                    viewBinding.locationList.adapter = CallbackSingleRVAdapter(locationList, selectedLocationIndex) {
                        Log.d("location", it.toString())
                        viewBinding.locationHead.dropdownTitle.text = locationList[it].name
                    }
                    break
                }
            }
            //setDeadline
            dateJsonString = oldStudy.deadLine
            viewBinding.deadlineHead.dropdownTitle.text = oldStudy.deadLine.replace("-", "/")

        }

        //setSubmitButton
        viewBinding.submitButton.setOnClickListener {
            val finalTitle = viewBinding.inputOfTitle.text.toString()
            Log.d("finalTitle", finalTitle)

            val finalDes = viewBinding.inputOfContent.text.toString()
            Log.d("finalDes", finalDes)

            val finalImagePathList = ArrayList<String>()
            for(i in imageItemList) finalImagePathList.add(i.imageCopyPath)
            Log.d("finalImagePaths", finalImagePathList.toString())

            val finalStack1Name = viewBinding.stack1Head.dropdownTitle.text.toString()
            Log.d("finalStack1Name", finalStack1Name)

            val finalPartNum = viewBinding.peopleSection.peopleNum.text.toString().toInt()
            Log.d("finalPartNumList", finalPartNum.toString())

            val finalStackList = java.util.ArrayList<Int>();
            for(i in chipList.keys){
                finalStackList.add(i.numberInServer)
            }
            Log.d("finalStackList", finalStackList.toString())

            val finalLocation = viewBinding.locationHead.dropdownTitle.text.toString()
            Log.d("finalLocation", finalLocation)

            val finalDeadline = dateJsonString
            Log.d("finalDeadline", finalDeadline)

            var toastString = ""
            if(!isTitleOk){
                toastString += "제목, "
            }
            if(!isContentOk){
                toastString += "상세 설명, "
            }

            var isStack1Ok = true
            if(finalStack1Name == "분야를 선택하세요"){
                toastString += "모집 분야, "
                isStack1Ok = false
            }


            var isPartPeopleOk = true
            if(finalPartNum == 0){
                toastString += "모집 인원수, "
                isPartPeopleOk = false
            }

            var isLocationOk = true
            if(finalLocation == "지역을 선택하세요"){
                toastString += "지역, "
                isLocationOk = false
            }

            var isDeadlineOk = true
            if(dateJsonString.isEmpty()){
                toastString+= "마감 시간, "
                isDeadlineOk = false
            }

            if(isTitleOk and isContentOk and isPartPeopleOk and isLocationOk and isDeadlineOk){
                if(isOld){
                    val imageFileList = ArrayList<File>()
                    //getOldImageNumber
                    var allUrlNumber = 0
                    var loadedImageNumber = 0
                    for(i in imageItemList){
                        if(i.imageUrl != "") allUrlNumber+=1
                    }

                    Log.d("finalOldNumber", "onCreate: $allUrlNumber")
                    for(i in imageItemList){
                        val nowIdx = imageItemList.indexOf(i)
                        imageFileList.add(File(i.imageCopyPath))

                        if(i.imageUrl != ""){ //when i == oldImage
                            Glide.with(this).asFile().load(i.imageUrl).into(object : CustomTarget<File>(){
                                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                                    val filename = i.imageUrl.split("http://semtle.catholic.ac.kr:8080/image?name=", limit = 2)[1]
                                    Log.d("filename", "run: filename is: $filename")
                                    val newNameFile = File(resource.parent!!, filename)
                                    resource.renameTo(newNameFile)

                                    imageFileList[nowIdx] = newNameFile
                                    loadedImageNumber += 1
                                    Log.d("finalLoadedOldNumber", "onCreate: $loadedImageNumber")
                                    Log.d("finalImage", "onResourceReady: ${imageFileList.toString()}")

                                    if(loadedImageNumber == allUrlNumber){
                                        val imageMultiPartListUsingFile = project2Server.createImageMultiPartListUsingFile(imageFileList)
                                        viewBinding.submitButton.isEnabled = false
                                        viewBinding.submitButton.isSelected = false
                                        var finalProcess = intent.getStringExtra("process")
                                        if(isTimeChanged) finalProcess = "ING"
                                        project2Server.updateStudy(this@AddNewStudyActivity, oldStudyId, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalStack1Name, finalPartNum, imageMultiPartListUsingFile, viewBinding.submitButton, finalProcess!!) {
                                            for(deleteFile in imageFileList) deleteFile.delete()
                                            finish()
                                        }
                                    }
                                }
                                override fun onLoadCleared(placeholder: Drawable?) {
//                                    Toast.makeText(this@AddNewStudyActivity, "모집글 수정 시 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }

                    if(allUrlNumber == 0){
                        val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
                        viewBinding.submitButton.isEnabled = false
                        viewBinding.submitButton.isSelected = false
                        var finalProcess = intent.getStringExtra("process")
                        if(isTimeChanged) finalProcess = "ING"
                        project2Server.updateStudy(this@AddNewStudyActivity, oldStudyId, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalStack1Name, finalPartNum, imageMultiPartList, viewBinding.submitButton, finalProcess!!) { finish() }
                    }

                    Log.d("deadlineServerJson", dateJsonString)

                }else{
                    val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
                    Log.d("finalImageMultiPartList", imageMultiPartList.toString())
                    Log.d("deadlineServerJson", dateJsonString)
                    viewBinding.submitButton.isEnabled = false
                    viewBinding.submitButton.isSelected = false
                    project2Server.postNewStudy(this, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalStack1Name, finalPartNum, imageMultiPartList, viewBinding.submitButton) { finish() }
                }
            }else{
                toastString = toastString.substring(0, toastString.length-2) + "을 확인하세요."
                Log.d("string", toastString)
                Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()

//                //TODO: 테스트코드
//                if(finalPartNum == 11 && finalStack1Name == "기획"){
//                    val finalStackMap = testStackMap(listOf(1, 2, 36 ,37), listOf("JavaScript", "TypeScript", "Blender", "Cinema4D"))
//                    val testStudy = EditStudy(
//                        44.toString(),
//                        "edit44",
//                        "please44",
//                        listOf("http://semtle.catholic.ac.kr:8080/image?name=104947-JpegJihyo20230201105005.jpeg"),
//                        "디자인",
//                        1,
//                        finalStackMap,
//                        "경기",
//                        "2022-02-07")
//                    val intent = Intent(this, AddNewStudyActivity::class.java)
//                    intent.putExtra("study", testStudy)
//                    startActivity(intent)
//                    finish()
//                }
            }
        }

//        val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
//        Log.d("finalImageMultiPartList", imageMultiPartList.toString())


    }

    //ImageSection - Start
    override fun onDestroy() {
        super.onDestroy()
        for (i in imageItemList){
            Log.d("delete", i.toString())
            val deleteFile = File(i.imageCopyPath)
            deleteFile.delete()
        }
    }
    //ImageSection - End

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
                    val copyImagePath = addPageFunction.createCopyAndReturnPath(this, uri, imageName)
                    val nowImageItem = ImageItem(uri, copyImagePath)
                    imageItemList.add(nowImageItem)
                    viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)

                    //subImageCounter
                    viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"

                }
            }else{
                Toast.makeText(this, "png 및 jpg(jpeg)형식의 사진만 지원합니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }
    //imageSection - End

    //SetStack2Function - Start
    private fun getStack2Adapter(itemList: java.util.ArrayList<AddListItem>): Stack2RVAdapter{
        return Stack2RVAdapter(itemList) {
            Log.d("stack2Clicked", it.name)
            addOrRemoveChip(it)
        }
    }
    private fun addOrRemoveChip(chipItem: AddListItem){
        //list안에 있는 클래스를 따로 만들어서 보관하는 것이 좋다
        val chipItemInList = AddListItem(false, chipItem.name, chipItem.numberInServer)

        //칩이 현재 존재하는지
        if(chipItemInList !in chipList.keys) {
            Log.d("iWillAddChip", chipItemInList.name)
            //칩 추가
            val chipView = addPageFunction.setStackChip(this, chipItemInList)
            chipView.setOnCloseIconClickListener{
                viewBinding.stackChipGroup.removeView(it)
                chipList.remove(chipItemInList)
                addPageFunction.changeItem2False(allStackList, chipItem)
                viewBinding.stack2List.adapter!!.notifyDataSetChanged()
            }
            viewBinding.stackChipGroup.addView(chipView)
            chipList[chipItemInList] = chipView
        }else{
            //칩 삭제
            Log.d("iWillDeleteChip", chipItemInList.name)

            viewBinding.stackChipGroup.removeView(chipList[chipItemInList])
            chipList.remove(chipItemInList)
        }
    }
    //SetStack2Function - End

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
            addPageFunction.checkSelfPermission(this, this)
            getContent.launch("image/*")
            dialog.dismiss()
        }
        dialogLayout.cancelButton.setOnClickListener {
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
        tempCameraFile = file

        //FileProvider.getUriForFile : provider에 대한 uri를 얻어 온다.
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        Log.d("testUri", "openCamera: $uri")
        tempUri = uri
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
            val uri = tempUri
            val imageInfo = addPageFunction.getInfoFromUri(this, uri)
            val imageName = imageInfo[0]
            val imageSize = imageInfo[1].toInt()
            val imageSizeLimitByte = 2e+7
            if(imageSize <= imageSizeLimitByte){
                val nowImageItem = ImageItem(uri, tempCameraFile.path)
                imageItemList.add(nowImageItem)
                viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
                //subImageCounter
                viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
            }
        } else {
            Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    //Bottom DiaLog - End

//    //TODO: 테스트 코드
//    private fun testStackMap(numberList: List<Int>, nameList: List<String>): LinkedHashMap<Int, String>{
//        val returnMap = LinkedHashMap<Int, String>()
//        for(i in numberList){
//            returnMap[i] = nameList[numberList.indexOf(i)]
//        }
//        return returnMap
//    }


}