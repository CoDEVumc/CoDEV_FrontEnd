package com.example.codev.addpage

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.AndroidKeyStoreUtil
import com.example.codev.R
import com.example.codev.databinding.*
import com.google.android.material.chip.Chip
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddNewStudyActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddNewStudyBinding
    private var imageFileList = ArrayList<File>()
    private var tempFileList = java.util.ArrayList<String>()
    private var imageNum = 0
    var allStackList = HashMap<Int, ArrayList<AddListItem> >()
    var chipList = HashMap<AddListItem, View>()
    private var dateJsonString: String = ""
    var addPageFunction = AddPageFunction()
    private var project2Server = Project2Server()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddNewStudyBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //setViewData
        val extras = intent.extras

        //checkIsNew


        //setTitle
        //val data = extras!!["MainEditText"] as String
        //=================================

        //setDes

        //=================================

        //setImage

        //=================================

        //setPartNumber
        ////setPartNumber

        //=================================

        //setChipGroup

        //setLocation

        //setDeadline

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
        var isTitleOk = false
        viewBinding.addPageTitleLayout.counterMaxLength = 30
        viewBinding.addPageTitleLayout.isCounterEnabled = true

        viewBinding.addPageTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(viewBinding.addPageTitle.text.toString().isEmpty() || viewBinding.addPageTitle.text.toString().replace(" ", "").length == 0) {
                    viewBinding.addPageTitleLayout.error = "제목을 입력하세요."
                    isTitleOk = false
                }else if(count >= 30){
                    viewBinding.addPageTitleLayout.error = "제목이 30자를 초과할 수 없습니다."
                    isTitleOk = false
                }else{
                    viewBinding.addPageTitleLayout.error = null
                    isTitleOk = true
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })
        //TitleSection - End

        //DesSection - Start
        var isDesOk = false
        viewBinding.addPageDes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(viewBinding.addPageDes.text.isEmpty() || viewBinding.addPageDes.text.toString().replace(" ", "").length == 0) {
                    Log.d("desText", viewBinding.addPageDes.text.toString().replace(" ", "").length.toString())
                    viewBinding.addPageDes.error = "상세 설명을 입력하세요."
                    isDesOk = false
                }else if(count >= 300){
                    viewBinding.addPageDes.error = "상세설명이 300자를 초과할 수 없습니다."
                    isDesOk = false
                }else{
                    viewBinding.addPageDes.error = null
                    isDesOk = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        //DesSection - End

        //add_image_section
        viewBinding.addImageButton.setOnClickListener {
            if(imageNum == 5){
                Toast.makeText(this, "사진은 최대 5개를 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }else {
                getContent.launch("image/*")
            }
        }

        // START-setNewStack2
        val makeStackDropdown = MakeStackDropdown()
        allStackList = makeStackDropdown.createAllStackHashMap(resources)

        var selectedNewStack1Index = -1

        viewBinding.stackDropdown2.dropdownTitle.text = "세부 기술을 선택하세요"

        val newStack2ListView = DropdownListBinding.inflate(layoutInflater)
        var isStack2ListOpen = false
        var newStack2RVListAdapter = NewDropdownRVListAdapter(this, allStackList[selectedNewStack1Index]!!)
        newStack2RVListAdapter = setStack2ClickEvent(newStack2RVListAdapter)
        viewBinding.stackDropdown2.dropdownRound.setOnClickListener {
            if(!isStack2ListOpen){
                val addStack2ListView = newStack2ListView.rvList
                addStack2ListView.adapter = newStack2RVListAdapter;
                addStack2ListView.layoutManager = LinearLayoutManager(this)
                viewBinding.stack2Section.addView(addStack2ListView.rootView)
                isStack2ListOpen = true
            }else{
                viewBinding.stack2Section.removeAllViews()
                isStack2ListOpen = false
            }
        }
        // END-setNewStack2

        //add_sub_button_setting
        addPageFunction.setAddSubButton(this, viewBinding.peopleSection)

        // START-setNewStack1
        val newStack1String = resources.getStringArray(R.array.stack1_list);
        val newStack1List = ArrayList<AddListItem>()
        for(i in newStack1String) newStack1List.add(AddListItem(false, i, 0))
        viewBinding.stackDropdown.dropdownTitle.text = "분야를 선택하세요"

        val newStack1ListView = DropdownListBinding.inflate(layoutInflater)
        var isStack1ListOpen = false
        val newStack1RVListAdapter = NewDropdownRVListAdapter(this, newStack1List)
        newStack1RVListAdapter.setOnItemClickListener(object:
            NewDropdownRVListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, item: AddListItem, pos: Int) {
                if(pos != selectedNewStack1Index){
                    if(selectedNewStack1Index != -1){
                        newStack1List[selectedNewStack1Index].isSelected = false
                        newStack1RVListAdapter.notifyItemChanged(selectedNewStack1Index)
                    }
                    newStack1List[pos].isSelected = true
                    newStack1RVListAdapter.notifyItemChanged(pos)
                    selectedNewStack1Index = pos

                    //resetStack2Section
                    viewBinding.stack2Section.removeAllViews()
                    newStack2RVListAdapter = NewDropdownRVListAdapter(this@AddNewStudyActivity, allStackList[selectedNewStack1Index]!!)
                    newStack2RVListAdapter = setStack2ClickEvent(newStack2RVListAdapter)
                    val  addStack2ListView = newStack2ListView.rvList
                    addStack2ListView.adapter = newStack2RVListAdapter;
                    addStack2ListView.layoutManager = LinearLayoutManager(this@AddNewStudyActivity)
                    viewBinding.stack2Section.addView(addStack2ListView.rootView)
                    isStack2ListOpen = true
                    //resetStack2Section END
                }
            }
        })
        viewBinding.stackDropdown.dropdownRound.setOnClickListener {
            if(!isStack1ListOpen){
                val addStack1ListView = newStack1ListView.rvList
                addStack1ListView.adapter = newStack1RVListAdapter;
                addStack1ListView.layoutManager = LinearLayoutManager(this)
                viewBinding.stack1Section.addView(addStack1ListView.rootView)
                isStack1ListOpen = true
            }else{
                viewBinding.stack1Section.removeAllViews()
                isStack1ListOpen = false
            }
        }
        // END-setNewStack1

        // START-setLocationNew
        val newLocationString = resources.getStringArray(R.array.location_list);
        val newLocationList = ArrayList<AddListItem>()
        for(i in newLocationString) newLocationList.add(AddListItem(false, i, 0))
        var selectedNewLocationIndex = -1
        viewBinding.locationDropdown.dropdownTitle.text = "지역을 선택하세요"

        val newLocationListView = DropdownListBinding.inflate(layoutInflater)
        var isLocationListOpen = false
        val newLocationRVAdapter = NewDropdownRVListAdapter(this, newLocationList)
        newLocationRVAdapter.setOnItemClickListener(object:
            NewDropdownRVListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, item: AddListItem, pos: Int) {
                if(pos != selectedNewLocationIndex){
                    if(selectedNewLocationIndex != -1){
                        newLocationList[selectedNewLocationIndex].isSelected = false
                        newLocationRVAdapter.notifyItemChanged(selectedNewLocationIndex)
                    }
                    newLocationList[pos].isSelected = true
                    newLocationRVAdapter.notifyItemChanged(pos)
                    selectedNewLocationIndex = pos
                    viewBinding.locationDropdown.dropdownTitle.text = newLocationList[selectedNewLocationIndex].name

                }
            }
        })
        viewBinding.locationDropdown.dropdownRound.setOnClickListener {
            if(!isLocationListOpen){
                val addLocationListView = newLocationListView.rvList
                addLocationListView.adapter = newLocationRVAdapter;
                addLocationListView.layoutManager = LinearLayoutManager(this)
                viewBinding.locationSection.addView(addLocationListView.rootView)
                isLocationListOpen = true
            }else{
                viewBinding.locationSection.removeAllViews()
                isLocationListOpen = false
            }
        }
        // END-setLocationNew

        //setDeadLine
        viewBinding.deadlineDropdown.dropdownTitle.text = "마감 일자를 선택하세요"
        viewBinding.deadlineDropdown.dropdownRound.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val dateShowString = "${year}/${month+1}/${dayOfMonth}"
                dateJsonString = String.format("%d-%02d-%d", year, month+1, dayOfMonth)
                viewBinding.deadlineDropdown.dropdownTitle.text = dateShowString
            }
            val dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }

        viewBinding.submitButton.setOnClickListener {
            val finalTitle = viewBinding.addPageTitle.text.toString()
            Log.d("finalTitle", finalTitle)

            val finalDes = viewBinding.addPageDes.text.toString()
            Log.d("finalDes", finalDes)

            val finalImageList = imageFileList
            Log.d("finalImageFiles", finalImageList.toString())

            val finalPartString: String
            //TODO: 스터디의 모집 분야 얘기가 끝나면 작성하자 -> 단일 선택인지, 필수 선택인지 등

            val finalPeople = viewBinding.peopleSection.peopleNum.text.toString().toInt()
            Log.d("finalPeople", finalPeople.toString())

            val finalStackList = ArrayList<Int>();
            for(i in chipList.keys){
                finalStackList.add(i.numberInServer)
            }
            Log.d("finalStackList", finalStackList.toString())

            val finalLocation = viewBinding.locationDropdown.dropdownTitle.text.toString()
            Log.d("finalLocation", finalLocation)

            val finalDeadline = dateJsonString
            Log.d("finalDeadline", finalDeadline)

            var toastString = ""

            if(!isTitleOk){
                toastString += "제목, "
            }

            if(!isDesOk){
                toastString += "상세 설명, "
            }

            var isPartPeopleOk: Boolean
            if(finalPeople == 0){
                toastString += "총 모집 인원수, "
                isPartPeopleOk = false
            }else{
                isPartPeopleOk = true
            }

            var isLocationOk: Boolean
            if(finalLocation == "지역을 선택하세요"){
                toastString += "지역, "
                isLocationOk = false
            }else{
                isLocationOk = true
            }

            var isDeadlineOk: Boolean
            if(dateJsonString.isEmpty()){
                toastString+= "마감 시간, "
                isDeadlineOk = false
            }else{
                isDeadlineOk = true
            }

            if(isTitleOk and isDesOk and isPartPeopleOk and isLocationOk and isDeadlineOk){
//                val finalNumOfPartList = project2Server.createPartNumList(finalPeople, finalDesignPeople, finalFrontPeople, finalBackPeople, finalEtcPeople)
//                Log.d("finalPartList", finalNumOfPartList.toString())

                val imageMultiPartList = project2Server.createImageMultiPartList(imageFileList)
                Log.d("finalImageMultiPartList", imageMultiPartList.toString())

//                project2Server.postNewProject(this, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalNumOfPartList, imageMultiPartList)
            }else{
                toastString = toastString.substring(0, toastString.length-2) + "을 확인하세요."
                Log.d("string", toastString)
                Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in imageFileList){
            Log.d("delete", i.toString())
            i.delete()
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
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null){
            addImageView(imageFileList, uri)
        }
    }

    private fun addImageView(list: ArrayList<File>, imageUri: Uri){
        val imageBinding = ImageItemBinding.inflate(layoutInflater)
        imageBinding.selectedImage.setImageURI(imageUri)
        val imageInfo = addPageFunction.getInfoFromUri(this, imageUri)
        val imageName = imageInfo[0]
        val imageSize = imageInfo[1]
        val imagePath = addPageFunction.createCopyAndReturnPath(this, imageUri, imageName)
        val imageFile = File(imagePath)
        imageBinding.cancelButton.setOnClickListener {
            viewBinding.selectedImages.removeView(imageBinding.root)
            list.remove(imageFile)
            imageFile.delete()
            viewBinding.addImageNum.text = String.format("%d/5", --imageNum)
        }
        viewBinding.selectedImages.addView(imageBinding.root)
        list.add(imageFile)
        viewBinding.addImageNum.text = String.format("%d/5", ++imageNum)
    }
    //imageSection - End


    //setStack2Section - Start
    fun setStack2ClickEvent(adapter: NewDropdownRVListAdapter): NewDropdownRVListAdapter {
        adapter.setOnItemClickListener(object: NewDropdownRVListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, item: AddListItem, pos: Int) {
                if(!item.isSelected){
                    chipList[item] = makeChip(item, adapter)
                    item.isSelected = true
                }else{
                    val removeChipView = chipList[item]
                    viewBinding.stackChipGroup.removeView(removeChipView)
                    chipList.remove(item)//remove from chipview
                    item.isSelected = false
                }
                adapter.notifyItemChanged(pos)
            }
        })
        return adapter
    }

    private fun makeChip(addItem: AddListItem, adapter: NewDropdownRVListAdapter): Chip {
        val chipView = addPageFunction.setStackChip(this, addItem, adapter)

        chipView.setOnCloseIconClickListener {
            addPageFunction.changeItem2False(allStackList, addItem)
            viewBinding.stackChipGroup.removeView(chipView)
            chipList.remove(addItem)
            adapter.notifyDataSetChanged()
        }
        viewBinding.stackChipGroup.addView(chipView)
        Log.d("did", "addDID")
        return chipView
    }
    //setStack2Section - End

}