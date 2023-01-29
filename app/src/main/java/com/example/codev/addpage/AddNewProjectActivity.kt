package com.example.codev.addpage

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.codev.AndroidKeyStoreUtil
import com.example.codev.R
import com.example.codev.databinding.ActivityAddNewProjectBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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
                getContent.launch(arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                ))
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
                dateJsonString = String.format("%d-%02d-%d", year, month+1, dayOfMonth)
                viewBinding.deadlineHead.dropdownTitle.text = dateShowString
            }
            var dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.datePicker.maxDate = (System.currentTimeMillis() + 3.156e+10).toLong() //1년 설정
            dpd.show()
        }

        //setViewData
        val extras = intent.extras
        //TODO: 객체가 존재하면 객체 내용을 페이지에 적용하고, toolbar과 제출하기의 text도 "수정하기"로 바꾸자
        //checkIsNew
        var isOld = false
        if (extras != null) {
            isOld = true
            viewBinding.toolbarTitle.toolbarText.text = getString(R.string.edit_new_project)
            viewBinding.submitButton.text = "프로젝트 수정하기"

            val oldData: EditProject = extras.get("project") as EditProject

            //setTitle
            viewBinding.inputOfTitle.setText(oldData.title)

            //setDes
            viewBinding.inputOfContent.setText(oldData.content)

            //=================================

            //setImage


            //
            //        val urlList = ArrayList<String>()
            //        urlList.add("http://semtle.catholic.ac.kr:8080/image?name=jpgSana20230123182825.jpg")
            //        urlList.add("http://semtle.catholic.ac.kr:8080/image?name=shot20230123182825.png")
            //        for(i in urlList){
            //            val imageBinding = ImageItemBinding.inflate(layoutInflater)
            //            Glide.with(this).asFile().load(i).submit().get()
            //            imageBinding.cancelButton.setOnClickListener {
            //                viewBinding.selectedImages.removeView(imageBinding.root)
            ////                imageFileList.remove(nowFile)
            //                viewBinding.addImageNum.text = (--imageNum).toString() + "/5"
            //            }
            //            viewBinding.selectedImages.addView(imageBinding.root)
            //            imageFileList.add(nowFile)
            //            viewBinding.addImageNum.text = (++imageNum).toString() + "/5"
            //        }

            //=================================

            //setPartNumber
            ////setPmNumber
            viewBinding.pmSection.peopleNum.text = oldData.pmPeople.toString()

            ////setDesignNumber
            viewBinding.designSection.peopleNum.text = oldData.designPeople.toString()

            ////setFrontNumber
            viewBinding.frontSection.peopleNum.text = oldData.frontPeople.toString()

            ////setBackNumber
            viewBinding.backSection.peopleNum.text = oldData.backPeople.toString()

            ////setEtcNumber
            viewBinding.etcSection.peopleNum.text = oldData.etcPeople.toString()

            //=================================

            //setChipGroup
            for(i in oldData.languageIdNameMap.keys){
                val oldChip = AddListItem(false, oldData.languageIdNameMap[i]!!, i)
                addPageFunction.changeItem2True(allStackList, oldData.languageIdNameMap[i]!!)
                addOrRemoveChip(oldChip)
                viewBinding.stack2List.adapter!!.notifyDataSetChanged()
            }


            //setLocation
            viewBinding.locationHead.dropdownTitle.text = oldData.location

            //setDeadline
            dateJsonString = oldData.deadLine
            viewBinding.deadlineHead.dropdownTitle.text = oldData.deadLine.replace("-", "/")

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

                val imageMultiPartList = project2Server.createImageMultiPartList(finalImagePathList)
                Log.d("finalImageMultiPartList", imageMultiPartList.toString())

                if(isOld){

                }else{
                    var result = project2Server.postNewProject(this, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalNumOfPartList, imageMultiPartList)
                }
            }else{
                toastString = toastString.substring(0, toastString.length-2) + "을 확인하세요."
                Log.d("string", toastString)
                Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()
            }
        }
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
    private val getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if(uri != null){
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
        }
    }
    //imageSection - End

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

    //setHadImage
    private fun loadImageData(urlList: List<String>, imageItemList: ArrayList<AddImageItem>){
        for(url in urlList){
            var nowFile = Glide.with(this).asFile().load(url).submit().get()
            var nowUri = Uri.fromFile(nowFile)
            imageItemList.add(AddImageItem(nowUri, nowFile))
            viewBinding.addImageSection.adapter!!.notifyItemInserted(imageItemList.lastIndex)
        }
    }
}