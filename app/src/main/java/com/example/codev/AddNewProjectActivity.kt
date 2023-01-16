package com.example.codev

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.databinding.ActivityAddNewProjectBinding
import com.example.codev.databinding.AddSubSectionBinding
import com.example.codev.databinding.DropdownListBinding
import com.example.codev.databinding.ImageItemBinding
import com.google.android.material.chip.Chip
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddNewProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAddNewProjectBinding
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var imageList = ArrayList<ImageItemBinding>()
    private var imageNum = 0
    var dateString = ""
    var timeString = ""
    var chipList = HashMap<String, View>()
    var checkedStack2 = HashMap<String, addListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddNewProjectBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)


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
        setAddSubButton(viewBinding.pmSection)
        setAddSubButton(viewBinding.designSection)
        setAddSubButton(viewBinding.frontSection)
        setAddSubButton(viewBinding.backSection)
        setAddSubButton(viewBinding.etcSection)

        //set stack list


        // START-setNewStack1
//        var newStack1String = getResources().getStringArray(R.array.stack1_list);
//        var newStack1List = ArrayList<addListItem>()
//        for(i in newStack1String) newStack1List.add(addListItem(false, i))
//        var selectedNewStack1Index = -1
//        viewBinding.stackDropdown.dropdownTitle.text = "분야를 선택하세요"
//
//        var newStack1ListView = DropdownListBinding.inflate(layoutInflater)
//        var isStack1ListOpen = false
//        var newStack1RVListAdapter = NewDropdownRVListAdapter(this, newStack1List)
//        newStack1RVListAdapter.setOnItemClickListener(object: NewDropdownRVListAdapter.OnItemClickListener{
//            override fun onItemClick(v: View?, item: addListItem, pos: Int) {
//                if(pos != selectedNewStack1Index){
//                    if(selectedNewStack1Index != -1){
//                        newStack1List.get(selectedNewStack1Index).isSelected = false
//                        newStack1RVListAdapter.notifyItemChanged(selectedNewStack1Index)
//                    }
//                    newStack1List.get(pos).isSelected = true
//                    newStack1RVListAdapter.notifyItemChanged(pos)
//                    selectedNewStack1Index = pos
////                    viewBinding.stackDropdown.dropdownTitle.text = newStack1List.get(selectedNewStack1Index).name
//                }
//            }
//        })
//        viewBinding.stackDropdown.dropdownRound.setOnClickListener {
//            if(!isStack1ListOpen){
//                var addStack1ListView = newStack1ListView.rvList
//                addStack1ListView.adapter = newStack1RVListAdapter;
//                addStack1ListView.layoutManager = LinearLayoutManager(this)
//                viewBinding.locationSection.addView(addStack1ListView.rootView)
//                isStack1ListOpen = true
//            }else{
//                viewBinding.locationSection.removeAllViews()
//                isStack1ListOpen = false
//            }
//        }
        // END-setNewStack1

        //set stack1 -> pm==0, design==1, front==2, back==3, etc==4
        var allPartList = listOf<addListItem>(addListItem(false, "계획"), addListItem(false,"디자인"), addListItem(false, "프론트엔드"), addListItem(false, "백엔드"), addListItem(false, "기타"))
        var selectedPartIndex = -1

        var allStackList = mutableMapOf<Int, ArrayList<addListItem> >()

        allStackList[-1] = ArrayList<addListItem>()

        var pmStackStringList = getResources().getStringArray(R.array.pm_stack);
        allStackList[0] = ArrayList<addListItem>()
        for(i in pmStackStringList) allStackList[0]?.add(addListItem(false, i))

        var designStackStringList = getResources().getStringArray(R.array.design_stack);
        allStackList[1] = ArrayList<addListItem>()
        for(i in designStackStringList) allStackList[1]?.add(addListItem(false, i))

        var frontStackStringList = getResources().getStringArray(R.array.front_stack);
        allStackList[2] = ArrayList<addListItem>()
        for(i in frontStackStringList) allStackList[2]?.add(addListItem(false, i))

        var backStackStringList = getResources().getStringArray(R.array.back_stack);
        allStackList[3] = ArrayList<addListItem>()
        for(i in backStackStringList) allStackList[3]?.add(addListItem(false, i))

        var etcStackStringList = getResources().getStringArray(R.array.etc_stack);
        allStackList[4] = ArrayList<addListItem>()
        for(i in etcStackStringList) allStackList[4]?.add(addListItem(false, i))


        var stack1view = DropdownListBinding.inflate(layoutInflater)
        var stack2view = DropdownListBinding.inflate(layoutInflater)
        var isStack1open: Boolean = false
        var isStack2open: Boolean = false
        var stack1RVAdapter = stack1Adapter(this, allPartList)
        var stack2RVAdapter = allStackList.get(selectedPartIndex)?.let { stack2Adapter(this, it) }


        viewBinding.stackDropdown.dropdownTitle.text = "분야를 선택하세요"
        viewBinding.stackDropdown.dropdownRound.setOnClickListener {
            if(!isStack1open){
                stack1RVAdapter.setOnItemClickListener(object: stack1Adapter.OnItemClickListener{
                    override fun onItemClick(v: View?, item: addListItem, pos: Int) {
                        if(selectedPartIndex != -1)allPartList[selectedPartIndex].isSelected = false
                        if(pos != selectedPartIndex){
                            selectedPartIndex = pos
                            viewBinding.stackDropdown.dropdownTitle.text = item.name
                            stack1RVAdapter.notifyDataSetChanged()
                            viewBinding.stack1Section.removeAllViews()
                            isStack1open = false

                            if(isStack2open){
                                viewBinding.stack2Section.removeAllViews()
                                isStack2open = false
                            }

                            stack2RVAdapter = allStackList.get(selectedPartIndex)?.let { stack2Adapter(this@AddNewProjectActivity, it) }

                            stack2RVAdapter?.setOnItemClickListener(object: stack2Adapter.OnItemClickListener{
                                override fun onItemClick(v: View?, item: addListItem, pos: Int) {
                                    if(item.isSelected){
                                        var deleteView = chipList.get(item.name)
                                        viewBinding.stackChipGroup.removeView(deleteView)
                                        chipList.remove(item.name)

                                    }else{
                                        chipList[item.name] = addChip(item.name, stack2RVAdapter!!)
                                        checkedStack2[item.name] = item
                                    }
                                    Log.d("did", "section1DID")

                                }
                            })

                            stack2view.rvList.adapter = stack2RVAdapter
                            stack2view.rvList.layoutManager = LinearLayoutManager(this@AddNewProjectActivity)
                            viewBinding.stack2Section.addView(stack2view.root)
                            isStack2open = true
                        }

                    }

                })

                stack1view.rvList.adapter = stack1RVAdapter
                stack1view.rvList.layoutManager = LinearLayoutManager(this)
                viewBinding.stack1Section.addView(stack1view.root)
                isStack1open = true
            }else{
                viewBinding.stack1Section.removeAllViews()
                isStack1open = false
            }

        }


        viewBinding.stackDropdown2.dropdownTitle.text = "세부 분야를 선택하세요"
        viewBinding.stackDropdown2.dropdownRound.setOnClickListener {
            if(!isStack2open){

                stack2RVAdapter = allStackList.get(selectedPartIndex)?.let { stack2Adapter(this@AddNewProjectActivity, it) }

                stack2RVAdapter?.setOnItemClickListener(object: stack2Adapter.OnItemClickListener{
                    override fun onItemClick(v: View?, item: addListItem, pos: Int) {
                        if(item.isSelected){
                            var deleteView = chipList.get(item.name)
                            viewBinding.stackChipGroup.removeView(deleteView)
                            chipList.remove(item.name)
                        }else{
                            chipList[item.name] = addChip(item.name, stack2RVAdapter!!)
                            checkedStack2[item.name] = item

                        }
                        Log.d("did", "section2DID")
                    }
                })

                stack2view.rvList.adapter = stack2RVAdapter
                stack2view.rvList.layoutManager = LinearLayoutManager(this)
                viewBinding.stack2Section.addView(stack2view.root)
                isStack2open = true
            }else{
                viewBinding.stack2Section.removeAllViews()
                isStack2open = false
            }
        }

        // START-setLocationNew
        var newLocationString = getResources().getStringArray(R.array.location_list);
        var newLocationList = ArrayList<addListItem>()
        for(i in newLocationString) newLocationList.add(addListItem(false, i))
        var selectedNewLocationIndex = -1
        viewBinding.locationDropdown.dropdownTitle.text = "지역을 선택하세요"

        var newLocationListView = DropdownListBinding.inflate(layoutInflater)
        var isLocationListOpen = false
        var newLocationRVAdapter = NewDropdownRVListAdapter(this, newLocationList)
        newLocationRVAdapter.setOnItemClickListener(object: NewDropdownRVListAdapter.OnItemClickListener{
            override fun onItemClick(v: View?, item: addListItem, pos: Int) {
                if(pos != selectedNewLocationIndex){
                    if(selectedNewLocationIndex != -1){
                        newLocationList.get(selectedNewLocationIndex).isSelected = false
                        newLocationRVAdapter.notifyItemChanged(selectedNewLocationIndex)
                    }
                    newLocationList.get(pos).isSelected = true
                    newLocationRVAdapter.notifyItemChanged(pos)
                    selectedNewLocationIndex = pos
                    viewBinding.locationDropdown.dropdownTitle.text = newLocationList.get(selectedNewLocationIndex).name

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
                dateString = "${year}/${month+1}/${dayOfMonth}"
                viewBinding.deadlineDropdown.dropdownTitle.text = dateString
            }
            var dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            addImageView(imageList, imageUri)
        }
        for(i in imageList){
            Log.d("image", i.selectedImage.drawable.toString())
        }
    }

    private fun setAddSubButton(section: AddSubSectionBinding){
        //버튼
        section.subButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> section.subButton.background = getDrawable(R.drawable.sub_button_press)
                    MotionEvent.ACTION_UP -> section.subButton.background = getDrawable(R.drawable.sub_button)
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        section.subButton.setOnClickListener {
            var nowInt = Integer.parseInt(section.peopleNum.text.toString())

            if(nowInt > 0){
                section.peopleNum.text = "" + (nowInt - 1)
            }
            if(nowInt-1 == 0){
                section.peopleNum.setTextColor(getColor(R.color.black_300))
            }
        }

        section.addButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> section.addButton.background = getDrawable(R.drawable.add_button_press)
                    MotionEvent.ACTION_UP -> section.addButton.background = getDrawable(R.drawable.add_button)
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        section.addButton.setOnClickListener {
            var nowInt = Integer.parseInt(section.peopleNum.text.toString())

            if(nowInt >= 0){
                section.peopleNum.text = "" + (nowInt + 1)
            }
            if(nowInt+1 == 1){
                section.peopleNum.setTextColor(getColor(R.color.black_900))
            }
        }
        //버튼 끝

    }

    private fun addImageView(list: ArrayList<ImageItemBinding>, imageUri: Uri?){
        val imageBinding = ImageItemBinding.inflate(layoutInflater)
        imageBinding.selectedImage.setImageURI(imageUri)
        imageBinding.cancelButton.setOnClickListener {
            viewBinding.selectedImages.removeView(imageBinding.root)
            list.remove(imageBinding)
            viewBinding.addImageNum.text = (--imageNum).toString() + "/5"
        }
        viewBinding.selectedImages.addView(imageBinding.root)
        list.add(imageBinding)
        viewBinding.addImageNum.text = (++imageNum).toString() + "/5"
    }

    private fun addChip(name: String, adapter: stack2Adapter): Chip {
        var chip = Chip(this)
        chip.text = name
        chip.setChipBackgroundColorResource(R.color.green_300)
        chip.chipStrokeColor = getColorStateList(R.color.green_900)
        chip.chipStrokeWidth = dpToPx(this, 1f)
        chip.chipCornerRadius = dpToPx(this, 6f)
        chip.isCloseIconVisible = true
        chip.closeIcon = getDrawable(R.drawable.close_icon)
        chip.closeIcon?.setTint(getColor(R.color.green_900))
        chip.setTextColor(getColor(R.color.black_900))
        chip.setTextAppearance(R.style.Text_Body4_SemiBold)

        chip.setOnCloseIconClickListener {
            viewBinding.stackChipGroup.removeView(chip)
            chipList.remove(name)
            checkedStack2[name]?.isSelected  = false
            adapter.notifyDataSetChanged()
        }
        viewBinding.stackChipGroup.addView(chip)
        Log.d("did", "addDID")
        return chip
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }
}