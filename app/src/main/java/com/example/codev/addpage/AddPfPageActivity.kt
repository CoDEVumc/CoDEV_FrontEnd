package com.example.codev.addpage

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.AndroidKeyStoreUtil
import com.example.codev.R
import com.example.codev.databinding.ActivityAddPfPageBinding
import com.example.codev.databinding.DropdownListBinding
import com.google.android.material.chip.Chip
import java.util.ArrayList
import java.util.HashMap

class AddPfPageActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddPfPageBinding

    var allStackList = HashMap<Int, ArrayList<AddListItem> >()
    var chipList = HashMap<AddListItem, View>()
    var addPageFunction = AddPageFunction()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddPfPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = getString(R.string.add_pf_toolbar)
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        //setPFTitle
        var isTitleOk = false
        viewBinding.inputPfTitle.inputTitle.hint = "제목 입력 (최대 25자)"
        viewBinding.inputPfTitle.inputTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(viewBinding.inputPfTitle.inputTitle.text.toString().isEmpty() || viewBinding.inputPfTitle.inputTitle.text.toString().replace(" ", "").length == 0) {
                    viewBinding.inputPfTitle.inputTitle.error = "제목을 입력하세요."
                    isTitleOk = false
                }else if(count >= 30){
                    viewBinding.inputPfTitle.inputTitle.error = "제목이 30자를 초과할 수 없습니다."
                    isTitleOk = false
                }else{
                    viewBinding.inputPfTitle.inputTitle.error = null
                    isTitleOk = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        //setPFName
        val userName = "상명이"
        viewBinding.inputPfName.inputTitle.setText(userName)
        viewBinding.inputPfName.inputTitle.isFocusable = false
        viewBinding.inputPfName.inputTitle.setTextColor(getColor(R.color.black_500))
        viewBinding.inputPfName.inputTitle.isClickable = false

        //setBirthday
        val userBirth = "1234-05-06"
        viewBinding.inputPfBirthday.inputTitle.setText(userBirth)
        viewBinding.inputPfBirthday.inputTitle.isFocusable = false
        viewBinding.inputPfBirthday.inputTitle.setTextColor(getColor(R.color.black_500))
        viewBinding.inputPfBirthday.inputTitle.isClickable = false

        //setLevel
        viewBinding.inputPfLevel.dropdownTitle.hint = "능력 정도 선택"

        // START-setLevel
        var newLevelString = resources.getStringArray(R.array.pf_level);
        var newLevelList = ArrayList<AddListItem>()
        for(i in newLevelString) newLevelList.add(AddListItem(false, i, 0))
        var selectedNewLevelIndex = -1

        var newLevelListView = DropdownListBinding.inflate(layoutInflater)
        var isLevelListOpen = false
        var newLevelRVAdapter = NewDropdownRVListAdapter(this, newLevelList)
        newLevelRVAdapter.setOnItemClickListener(object:
            NewDropdownRVListAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, item: AddListItem, pos: Int) {
                if(pos != selectedNewLevelIndex){ //직전 선택한 항목이 아니고
                    if(selectedNewLevelIndex != -1){ // 직전에 선택한 항목이 있었을 때
                        newLevelList[selectedNewLevelIndex].isSelected = false
                        newLevelRVAdapter.notifyItemChanged(selectedNewLevelIndex)
                    }
                    newLevelList[pos].isSelected = true
                    newLevelRVAdapter.notifyItemChanged(pos)
                    selectedNewLevelIndex = pos
                    viewBinding.inputPfLevel.dropdownTitle.text = newLevelList[selectedNewLevelIndex].name
                }
            }
        })
        viewBinding.inputPfLevel.dropdownRound.setOnClickListener {
            if(!isLevelListOpen){
                var addLocationListView = newLevelListView.rvList
                addLocationListView.adapter = newLevelRVAdapter;
                addLocationListView.layoutManager = LinearLayoutManager(this)
                viewBinding.levelSection.addView(addLocationListView.rootView)
                isLevelListOpen = true
            }else{
                viewBinding.levelSection.removeAllViews()
                isLevelListOpen = false
            }
        }
        // END-setLevelNew

        // START-setNewStack2
        var makeStackDropdown = MakeStackDropdown()
        allStackList = makeStackDropdown.createAllStackHashMap(resources)

        var selectedNewStack1Index = -1

        viewBinding.stackDropdown2.dropdownTitle.text = "세부 기술 선택"

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
                    newStack2RVListAdapter = NewDropdownRVListAdapter(this@AddPfPageActivity, allStackList[selectedNewStack1Index]!!)
                    newStack2RVListAdapter = setStack2ClickEvent(newStack2RVListAdapter)
                    var addStack2ListView = newStack2ListView.rvList
                    addStack2ListView.adapter = newStack2RVListAdapter;
                    addStack2ListView.layoutManager = LinearLayoutManager(this@AddPfPageActivity)
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

        //setPFIntro
        var isIntroOk = false
        viewBinding.inputPfIntro.inputTitle.hint = "나를 한 마디로 표현해 보세요. (최대 25자)"
        viewBinding.inputPfIntro.inputTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(viewBinding.inputPfIntro.inputTitle.text.toString().isEmpty() || viewBinding.inputPfIntro.inputTitle.text.toString().replace(" ", "").length == 0) {
                    viewBinding.inputPfIntro.inputTitle.error = "제목을 입력하세요."
                    isIntroOk = false
                }else if(count >= 25){
                    viewBinding.inputPfIntro.inputTitle.error = "제목이 25자를 초과할 수 없습니다."
                    isIntroOk = false
                }else{
                    viewBinding.inputPfIntro.inputTitle.error = null
                    isIntroOk = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })












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