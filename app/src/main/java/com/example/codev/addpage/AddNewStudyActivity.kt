package com.example.codev.addpage

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
import com.example.codev.AndroidKeyStoreUtil
import com.example.codev.BuildConfig
import com.example.codev.R
import com.example.codev.databinding.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddNewStudyBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //????????? ?????? ??? ?????? ??????
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
                    viewBinding.inputOfTitleLayout.error = "????????? ???????????????."
                    isTitleOk = false
                }else if(s.length > titleLimit){
                    viewBinding.inputOfTitleLayout.error = "????????? ${titleLimit}?????? ????????? ??? ????????????."
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
                    viewBinding.inputOfContent.error = "?????? ????????? ???????????????."
                    isContentOk = false
                }else {
                    viewBinding.contentTextCounter.text = "${s!!.length}/${contentLimit}"
                    if(s.length > contentLimit){
                        viewBinding.inputOfContent.error = "?????? ????????? ${contentLimit}?????? ????????? ??? ????????????."
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
                Toast.makeText(this, "????????? ?????? ${imageLimit}?????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
            }else {
                addPageFunction.checkSelfPermission(this, this)
                getBottomMenu()
            }
        }
        //addImageSection - End

        // START-setNewStack2
        allStackList = addPageFunction.createAllStackHashMap(resources)

        var selectedStack1Index = -1

        viewBinding.stack2Head.dropdownTitle.text = "?????? ????????? ???????????????"

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
        viewBinding.stack1Head.dropdownTitle.text = "????????? ???????????????"

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
        viewBinding.locationHead.dropdownTitle.text = "????????? ???????????????"

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
        viewBinding.deadlineHead.dropdownTitle.text = "?????? ????????? ???????????????"
        viewBinding.deadlineHead.dropdownRound.setOnClickListener {
            val cal = Calendar.getInstance()    //???????????? ?????????
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var dateShowString = "${year}/${month+1}/${dayOfMonth}"
                dateJsonString = String.format("%d-%02d-%02d", year, month+1, dayOfMonth)
                viewBinding.deadlineHead.dropdownTitle.text = dateShowString
            }
            var dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.datePicker.maxDate = (System.currentTimeMillis() + 3.156e+10).toLong() //1??? ??????
            dpd.show()
        }
        //setDeadLine - End

        var isOld = false
        if(intent.getSerializableExtra("study") != null){
            val oldStudy = intent.getSerializableExtra("study") as EditStudy

            //checkIsNew
            isOld = true
            oldStudyId = oldStudy.studyId
            viewBinding.toolbarTitle.toolbarText.text = getString(R.string.edit_new_study)
            viewBinding.submitButton.text = "????????? ????????????"

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
                toastString += "??????, "
            }
            if(!isContentOk){
                toastString += "?????? ??????, "
            }

            var isStack1Ok = true
            if(finalStack1Name == "????????? ???????????????"){
                toastString += "?????? ??????, "
                isStack1Ok = false
            }


            var isPartPeopleOk = true
            if(finalPartNum == 0){
                toastString += "?????? ?????????, "
                isPartPeopleOk = false
            }

            var isLocationOk = true
            if(finalLocation == "????????? ???????????????"){
                toastString += "??????, "
                isLocationOk = false
            }

            var isDeadlineOk = true
            if(dateJsonString.isEmpty()){
                toastString+= "?????? ??????, "
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
                                        project2Server.updateStudy(this@AddNewStudyActivity, oldStudyId, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalStack1Name, finalPartNum, imageMultiPartListUsingFile, viewBinding.submitButton) {
                                            for(deleteFile in imageFileList) deleteFile.delete()
                                            finish()
                                        }
                                    }
                                }
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    Toast.makeText(this@AddNewStudyActivity, "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }

                    if(allUrlNumber == 0){
                        val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
                        viewBinding.submitButton.isEnabled = false
                        viewBinding.submitButton.isSelected = false
                        project2Server.updateStudy(this@AddNewStudyActivity, oldStudyId, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalStack1Name, finalPartNum, imageMultiPartList, viewBinding.submitButton) { finish() }
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
                toastString = toastString.substring(0, toastString.length-2) + "??? ???????????????."
                Log.d("string", toastString)
                Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()

//                //TODO: ???????????????
//                if(finalPartNum == 11 && finalStack1Name == "??????"){
//                    val finalStackMap = testStackMap(listOf(1, 2, 36 ,37), listOf("JavaScript", "TypeScript", "Blender", "Cinema4D"))
//                    val testStudy = EditStudy(
//                        44.toString(),
//                        "edit44",
//                        "please44",
//                        listOf("http://semtle.catholic.ac.kr:8080/image?name=104947-JpegJihyo20230201105005.jpeg"),
//                        "?????????",
//                        1,
//                        finalStackMap,
//                        "??????",
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
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "png ??? jpg(jpeg)????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
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
        //list?????? ?????? ???????????? ?????? ???????????? ???????????? ?????? ??????
        val chipItemInList = AddListItem(false, chipItem.name, chipItem.numberInServer)

        //?????? ?????? ???????????????
        if(chipItemInList !in chipList.keys) {
            Log.d("iWillAddChip", chipItemInList.name)
            //??? ??????
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
            //??? ??????
            Log.d("iWillDeleteChip", chipItemInList.name)

            viewBinding.stackChipGroup.removeView(chipList[chipItemInList])
            chipList.remove(chipItemInList)
        }
    }
    //SetStack2Function - End

    //Bottom DiaLog - Start
    private fun getBottomMenu(){
        // BottomSheetDialog ?????? ??????. param : Context
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
        tempCameraFile = file

        //FileProvider.getUriForFile : provider??? ?????? uri??? ?????? ??????.
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        Log.d("testUri", "openCamera: $uri")
        tempUri = uri
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
            Toast.makeText(this, "?????? ???????????????", Toast.LENGTH_SHORT).show()
        }
    }

    //Bottom DiaLog - End

//    //TODO: ????????? ??????
//    private fun testStackMap(numberList: List<Int>, nameList: List<String>): LinkedHashMap<Int, String>{
//        val returnMap = LinkedHashMap<Int, String>()
//        for(i in numberList){
//            returnMap[i] = nameList[numberList.indexOf(i)]
//        }
//        return returnMap
//    }


}