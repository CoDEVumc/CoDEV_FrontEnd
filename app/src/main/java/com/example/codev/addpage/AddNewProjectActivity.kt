package com.example.codev.addpage

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.*
import com.example.codev.databinding.ActivityAddNewProjectBinding
import com.example.codev.databinding.DropdownListBinding
import com.example.codev.databinding.ImageItemBinding
import com.google.android.material.chip.Chip
import java.util.*


class AddNewProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddNewProjectBinding
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var imageList = ArrayList<String>()
    private var imageNum = 0
    var allStackList = HashMap<Int, ArrayList<AddListItem> >()
    var chipList = HashMap<AddListItem, View>()
    private var dateJsonString: String = ""
    var addPageFunction = AddPageFunction()
    var project2Server = Project2Server()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddNewProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //setViewData
        val extras = intent.extras
        //TODO: 객체가 존재하면 객체 내용을 페이지에 적용하고, toolbar과 제출하기의 text도 "수정하기"로 바꾸자
        //checkIsNew


        //setTitle
        //val data = extras!!["MainEditText"] as String
        //=================================

        //setDes

        //=================================

        //setImage

        //=================================

        //setPartNumber
        ////setPmNumber

        ////setDesignNumber

        ////setFrontNumber

        ////setBackNumber

        ////setetcNumber
        //=================================

        //setChipGroup

        //setLocation

        //setDeadline



        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = getString(R.string.add_new_project)
        viewBinding.toolbarTitle.toolbarText.setTypeface(Typeface.DEFAULT_BOLD) //Text bold
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
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            }
        }


        //add_sub_button_setting
        addPageFunction.setAddSubButton(this, viewBinding.pmSection)
        addPageFunction.setAddSubButton(this, viewBinding.designSection)
        addPageFunction.setAddSubButton(this, viewBinding.frontSection)
        addPageFunction.setAddSubButton(this, viewBinding.backSection)
        addPageFunction.setAddSubButton(this, viewBinding.etcSection)

        //set stack list

        // START-setNewStack2
        var makeStackDropdown = MakeStackDropdown()
        allStackList = makeStackDropdown.createAllStackHashMap(resources)

        var selectedNewStack1Index = -1

        viewBinding.stackDropdown2.dropdownTitle.text = "세부 기술을 선택하세요"

        var newStack2ListView = DropdownListBinding.inflate(layoutInflater)
        var isStack2ListOpen = false
        var newStack2RVListAdapter = NewDropdownRVListAdapter(this, allStackList[selectedNewStack1Index]!!)
        newStack2RVListAdapter = setStack2ClickEvent(newStack2RVListAdapter)
        viewBinding.stackDropdown2.dropdownRound.setOnClickListener {
            if(!isStack2ListOpen){
                var addStack2ListView = newStack2ListView.rvList
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


        // START-setNewStack1
        var newStack1String = resources.getStringArray(R.array.stack1_list);
        var newStack1List = ArrayList<AddListItem>()
        for(i in newStack1String) newStack1List.add(AddListItem(false, i, 0))
        viewBinding.stackDropdown.dropdownTitle.text = "분야를 선택하세요"

        var newStack1ListView = DropdownListBinding.inflate(layoutInflater)
        var isStack1ListOpen = false
        var newStack1RVListAdapter = NewDropdownRVListAdapter(this, newStack1List)
        newStack1RVListAdapter.setOnItemClickListener(object:
            NewDropdownRVListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, item: AddListItem, pos: Int) {
                if(pos != selectedNewStack1Index){ //클릭한 항목이 선택한 항목이 아니면
                    if(selectedNewStack1Index != -1){ //직전에 선택한 항목이 있었을 때
                        newStack1List[selectedNewStack1Index].isSelected = false
                        newStack1RVListAdapter.notifyItemChanged(selectedNewStack1Index)
                    }
                    newStack1List[pos].isSelected = true
                    newStack1RVListAdapter.notifyItemChanged(pos)
                    selectedNewStack1Index = pos

                    //resetStack2Section
                    viewBinding.stack2Section.removeAllViews()
                    newStack2RVListAdapter = NewDropdownRVListAdapter(this@AddNewProjectActivity, allStackList[selectedNewStack1Index]!!)
                    newStack2RVListAdapter = setStack2ClickEvent(newStack2RVListAdapter)
                    var addStack2ListView = newStack2ListView.rvList
                    addStack2ListView.adapter = newStack2RVListAdapter;
                    addStack2ListView.layoutManager = LinearLayoutManager(this@AddNewProjectActivity)
                    viewBinding.stack2Section.addView(addStack2ListView.rootView)
                    isStack2ListOpen = true
                    //resetStack2Section END
                }
            }
        })
        viewBinding.stackDropdown.dropdownRound.setOnClickListener {
            if(!isStack1ListOpen){
                var addStack1ListView = newStack1ListView.rvList
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
        var newLocationString = resources.getStringArray(R.array.location_list);
        var newLocationList = ArrayList<AddListItem>()
        for(i in newLocationString) newLocationList.add(AddListItem(false, i, 0))
        var selectedNewLocationIndex = -1
        viewBinding.locationDropdown.dropdownTitle.text = "지역을 선택하세요"

        var newLocationListView = DropdownListBinding.inflate(layoutInflater)
        var isLocationListOpen = false
        var newLocationRVAdapter = NewDropdownRVListAdapter(this, newLocationList)
        newLocationRVAdapter.setOnItemClickListener(object:
            NewDropdownRVListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, item: AddListItem, pos: Int) {
                if(pos != selectedNewLocationIndex){ //직전 선택한 항목이 아니고
                    if(selectedNewLocationIndex != -1){ // 직전에 선택한 항목이 있었을 때
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
                var addLocationListView = newLocationListView.rvList
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
                var dateShowString = "${year}/${month+1}/${dayOfMonth}"
                dateJsonString = String.format("%d-%02d-%d", year, month+1, dayOfMonth)
                viewBinding.deadlineDropdown.dropdownTitle.text = dateShowString
            }
            var dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }

        //setSubmitButton
        viewBinding.submitButton.setOnClickListener {
            val finalTitle = viewBinding.addPageTitle.text.toString()
            Log.d("finalTitle", finalTitle)

            val finalDes = viewBinding.addPageDes.text.toString()
            Log.d("finalDes", finalDes)

            val finalImageList = imageList
            Log.d("finalImagePaths", finalImageList.toString())

            val finalPmPeople = viewBinding.pmSection.peopleNum.text.toString().toInt()
            Log.d("finalPmPeople", finalPmPeople.toString())

            val finalDesignPeople = viewBinding.designSection.peopleNum.text.toString().toInt()
            Log.d("finalDesignPeople", finalDesignPeople.toString())

            val finalFrontPeople = viewBinding.frontSection.peopleNum.text.toString().toInt()
            Log.d("finalFrontPeople", finalFrontPeople.toString())

            val finalBackPeople = viewBinding.backSection.peopleNum.text.toString().toInt()
            Log.d("finalBackPeople", finalBackPeople.toString())

            val finalEtcPeople = viewBinding.etcSection.peopleNum.text.toString().toInt()
            Log.d("finalEtcPeople", finalEtcPeople.toString())

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
            if(finalPmPeople + finalDesignPeople + finalFrontPeople + finalBackPeople + finalEtcPeople == 0){
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
                val finalNumOfPartList = project2Server.createPartNumList(finalPmPeople, finalDesignPeople, finalFrontPeople, finalBackPeople, finalEtcPeople)
                Log.d("finalPartList", finalNumOfPartList.toString())

                val imageMultiPartList = project2Server.createImageMultiPartList(imageList)
                Log.d("finalImageMultiPartList", imageMultiPartList.toString())

                project2Server.postNewProject(this, finalTitle, finalDes, finalLocation, finalStackList.toList(), finalDeadline, finalNumOfPartList, imageMultiPartList)
            }else{
                toastString = toastString.substring(0, toastString.length-2) + "을 확인하세요."
                Log.d("string", toastString)
                Toast.makeText(this, toastString, Toast.LENGTH_LONG).show()
            }



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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            addImageView(imageList, imageUri)
        }
    }

    private fun addImageView(list: ArrayList<String>, imageUri: Uri?){
        val imageBinding = ImageItemBinding.inflate(layoutInflater)
        imageBinding.selectedImage.setImageURI(imageUri)
        val imagePath = addPageFunction.getRealPathFromURI(this, imageUri)
        imageBinding.cancelButton.setOnClickListener {
            viewBinding.selectedImages.removeView(imageBinding.root)
            list.remove(imagePath)
            viewBinding.addImageNum.text = (--imageNum).toString() + "/5"
        }
        viewBinding.selectedImages.addView(imageBinding.root)
        list.add(imagePath)
        viewBinding.addImageNum.text = (++imageNum).toString() + "/5"
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
                    var removeChipView = chipList[item]

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
        var chipView = addPageFunction.setStackChip(this, addItem, adapter)

        chipView.setOnCloseIconClickListener {
//            addPageFunction.changeItem2False(allStackList, name)'
            addItem.isSelected = false
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