package com.codev.android.addpage

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.codev.android.databinding.ActivityAddNewProjectBinding
import com.codev.android.databinding.ProfileAddImageLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.util.*


class AddNewProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddNewProjectBinding

    //functionVar
    var addPageFunction = AddPageFunction()
    var project2Server = Project2Server()

    //imageVar
    private val imageLimit = 5
    var imageItemList = ArrayList<ImageItem>()

    //stackVar
    var allStackList = LinkedHashMap<Int, ArrayList<AddListItem> >()
    var chipList = LinkedHashMap<AddListItem, View>()

    //DeadLineTimeVar
    private var dateJsonString: String = ""

    //oldProject
    private var oldProjectId = ""

    //Camera
    private var tempCameraFile = File("")
    private var tempUri = Uri.EMPTY

    //모집 기간을 수정했는지 확인용
    private var isTimeChanged = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddNewProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = getString(R.string.add_new_project)
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

        //contentSection - Start
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
        //contentSection - End

        //

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

        //AddPartPeople - Start
        addPageFunction.setAddSubButton(this, viewBinding.pmSection)
        addPageFunction.setAddSubButton(this, viewBinding.designSection)
        addPageFunction.setAddSubButton(this, viewBinding.frontSection)
        addPageFunction.setAddSubButton(this, viewBinding.backSection)
        addPageFunction.setAddSubButton(this, viewBinding.etcSection)
        //AddPartPeople - End

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
        val stack1List = ArrayList<AddListItem>()
        for(i in stack1String) stack1List.add(AddListItem(false, i, 0))
        viewBinding.stack1Head.dropdownTitle.text = "분야를 선택하세요"

        var stack1Adapter = CallbackSingleRVAdapter(stack1List, selectedStack1Index) {
            Log.d("stack1Index", it.toString())
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

        //setDeadLine
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

        //setViewData
        var isOld = false
        if(intent.getSerializableExtra("project") != null){
            val oldProject = intent.getSerializableExtra("project") as EditProject

            //checkIsNew
            isOld = true
            oldProjectId = oldProject.projectId
            viewBinding.toolbarTitle.toolbarText.text = getString(R.string.edit_new_project)
            viewBinding.submitButton.text = "프로젝트 수정하기"

            //setTitle
            viewBinding.inputOfTitle.setText(oldProject.title)

            //setDes
            viewBinding.inputOfContent.setText(oldProject.content)

            //=================================

            //setImage
            for(nowUrl in oldProject.imageUrl){
                val nowImageItem = ImageItem(Uri.EMPTY, "", nowUrl.co_fileUrl)
                imageItemList.add(nowImageItem)
                viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
                //subImageCounter
                viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
            }

            //=================================

            //setPartNumber
            ////setPmNumber

            oldProject.partList
            for (i in 0 until oldProject.partList.size){
                val part = oldProject.partList[i]
                if (part.co_part == "프론트엔드"){
                    viewBinding.frontSection.peopleNum.text = part.co_limit.toString()
                    if(part.co_limit.toString() != "0"){
                        viewBinding.frontSection.peopleNum.setTextColor(resources.getColor(R.color.black_900))
                    }
                }else if (part.co_part == "백엔드"){
                    viewBinding.backSection.peopleNum.text = part.co_limit.toString()
                    if(part.co_limit.toString() != "0"){
                        viewBinding.backSection.peopleNum.setTextColor(resources.getColor(R.color.black_900))
                    }
                }else if (part.co_part == "디자인"){
                    viewBinding.designSection.peopleNum.text = part.co_limit.toString()
                    if(part.co_limit.toString() != "0"){
                        viewBinding.designSection.peopleNum.setTextColor(resources.getColor(R.color.black_900))
                    }
                }else if (part.co_part == "기획"){
                    viewBinding.pmSection.peopleNum.text = part.co_limit.toString()
                    if(part.co_limit.toString() != "0"){
                        viewBinding.pmSection.peopleNum.setTextColor(resources.getColor(R.color.black_900))
                    }
                }else if (part.co_part == "기타"){
                    viewBinding.etcSection.peopleNum.text = part.co_limit.toString()
                    if(part.co_limit.toString() != "0"){
                        viewBinding.etcSection.peopleNum.setTextColor(resources.getColor(R.color.black_900))
                    }
                }
            }

            //=================================

            //setChipGroup
            for(i in oldProject.languageIdNameMap.keys){
                val oldChip = AddListItem(false, oldProject.languageIdNameMap[i]!!, i)
                val listItem = addPageFunction.changeItem2True(allStackList, oldProject.languageIdNameMap[i]!!)
                addOrRemoveChip(listItem!!)
                viewBinding.stack2List.adapter!!.notifyDataSetChanged()
            }


            //setLocation
            viewBinding.locationHead.dropdownTitle.text = oldProject.location
            for (i in locationList){
                if(i.name == oldProject.location){
                    selectedLocationIndex = locationList.indexOf(i)
                    i.isSelected = true
                    //selectedLocationIndex 초기화를 위한 코드
                    viewBinding.locationList.adapter = CallbackSingleRVAdapter(locationList, selectedLocationIndex) {
                        Log.d("location", it.toString())
                        viewBinding.locationHead.dropdownTitle.text = locationList[it].name
                    }
                    break
                }
            }

            //setDeadline
            dateJsonString = oldProject.deadLine
            viewBinding.deadlineHead.dropdownTitle.text = oldProject.deadLine.replace("-", "/")

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

            val finalPartNumList = listOf(viewBinding.pmSection.peopleNum.text.toString().toInt(),
                                            viewBinding.designSection.peopleNum.text.toString().toInt(),
                                            viewBinding.frontSection.peopleNum.text.toString().toInt(),
                                            viewBinding.backSection.peopleNum.text.toString().toInt(),
                                            viewBinding.etcSection.peopleNum.text.toString().toInt())
            Log.d("finalPartNumList", finalPartNumList.toString())

            val finalStackList = ArrayList<Int>();
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

            var isPartPeopleOk = true
            if(finalPartNumList.sum() == 0){
                toastString += "총 모집 인원수, "
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
                val finalNumOfPartList = project2Server.createPartNumList(finalPartNumList)
                Log.d("finalPartList", finalNumOfPartList.toString())

//                var imageMultiPartList:  List<MultipartBody.Part> = listOf()

                if(isOld){
                    //getOldImageNumber
                    var allUrlNumber = 0
                    var loadedImageNumber = 0
                    for(i in imageItemList){
                        if(i.imageUrl != "") allUrlNumber+=1
                    }

                    if(allUrlNumber == 0){
                        updateWithFilePathList(oldProjectId, finalTitle, finalDes, finalLocation, finalStackList, finalDeadline, finalNumOfPartList, finalImagePathList)
                    }else{
                        for(i in imageItemList){
                            val nowIdx = imageItemList.indexOf(i)
                            if(i.imageUrl != ""){
                                Glide.with(this).asBitmap().load(i.imageUrl).into(object : CustomTarget<Bitmap>(){

                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        finalImagePathList[nowIdx] = AddImage().createImageCachePath(this@AddNewProjectActivity, resource, 50)
                                        loadedImageNumber += 1

                                        if(loadedImageNumber == allUrlNumber){
                                            updateWithFilePathList(oldProjectId, finalTitle, finalDes, finalLocation, finalStackList, finalDeadline, finalNumOfPartList, finalImagePathList)
                                        }
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        Log.d("finalDownloadOldImage", "onLoadCleared: $placeholder")
//                                    Toast.makeText(this@AddNewProjectActivity, "사진 수정 시 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    }

                                })
                            }
                        }
                    }
                }
                else{ //isNew
                    postWithFilePathList(finalTitle, finalDes, finalLocation, finalStackList, finalDeadline, finalNumOfPartList, finalImagePathList)
                }
            }else{ //don't ready to upload
                toastString = toastString.substring(0, toastString.length-2) + "을 확인하세요."
                Log.d("string", toastString)
                Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun postWithFilePathList(finalTitle: String, finalDes: String, finalLocation: String, finalStackList: List<Int>, finalDeadline: String, finalNumOfPartList: List<PartNameAndPeople>, finalImagePathList: ArrayList<String> ){
        val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
        Log.d("finalImageMultiPartList", imageMultiPartList.toString())
        Log.d("deadlineServerJson", dateJsonString)
        changeButtonStatus(false)
        project2Server.postNewProject(this, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalNumOfPartList, imageMultiPartList, whenPostEnd)
    }

    private fun updateWithFilePathList(oldProjectId: String, finalTitle: String, finalDes: String, finalLocation: String, finalStackList: List<Int>, finalDeadline: String, finalNumOfPartList: List<PartNameAndPeople>, finalImagePathList: ArrayList<String> ){
        val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
        changeButtonStatus(false)
        var finalProcess = intent.getStringExtra("process")
        if(isTimeChanged) finalProcess = "ING"
        project2Server.updateProject(this, oldProjectId, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalNumOfPartList, imageMultiPartList, finalProcess!!, whenPostEnd)
    }

    private val whenPostEnd: (Int) -> Unit = {
        if(it == 1) finish()
        else changeButtonStatus(true)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //SetStack2Function - Start
    private fun getStack2Adapter(itemList: ArrayList<AddListItem>): Stack2RVAdapter{
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
    private fun addImage2List(imageUri: Uri, copyImagePath: String){
        Log.d("test", "newPath: $copyImagePath")
        val nowImageItem = ImageItem(imageUri, copyImagePath)
        imageItemList.add(nowImageItem)
        viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
        //subImageCounter
        viewBinding.addImageNum.text = "${imageItemList.size}/${imageLimit}"
    }

    private val getImageFromFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null){
            val copyImagePath = AddImage().getCachePathUseUri(this, uri, 50, 2e+7)
            if(copyImagePath != "") addImage2List(uri, copyImagePath)
            else Toast.makeText(this, "사진을 추가하는데 알 수 없는 이유로 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
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

    private fun changeButtonStatus(nowStatus: Boolean){
        viewBinding.submitButton.isEnabled = nowStatus
        viewBinding.submitButton.isSelected = nowStatus
    }

}