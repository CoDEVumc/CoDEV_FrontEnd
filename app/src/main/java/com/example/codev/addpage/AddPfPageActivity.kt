package com.example.codev.addpage

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.AndroidKeyStoreUtil
import com.example.codev.R
import com.example.codev.databinding.ActivityAddPfPageBinding
import com.example.codev.databinding.InputLayoutBinding


class AddPfPageActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddPfPageBinding

    //functionVar
    var addPageFunction = AddPageFunction()
    var project2Server = Project2Server()

    //stackVar
    var allStackList = HashMap<Int, ArrayList<AddListItem> >()
    var chipList = HashMap<AddListItem, View>()

    //linkList
    private var linkTimeTextHashMap = LinkedHashMap<Long, String>()

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

        //TitleSection - Start
        val titleLimit = 30
        var isTitleOk = false
        viewBinding.inputPfTitle.inputOfTitle.hint = "제목 입력 (최대 30자)"
        viewBinding.inputPfTitle.inputOfTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrBlank()) {
                    viewBinding.inputPfTitle.inputOfTitle.error = "제목을 입력하세요."
                    isTitleOk = false
                }else if(s.length > titleLimit){
                    viewBinding.inputPfTitle.inputOfTitle.error = "제목이 ${titleLimit}자를 초과할 수 없습니다."
                    isTitleOk = false
                }else{
                    viewBinding.inputPfTitle.inputOfTitle.error = null

                    isTitleOk = true
                }
            }
        })
        //TitleSection - End


        //setPFName
        val userName = "상명이"
        viewBinding.inputPfName.inputOfTitle.setText(userName)
        viewBinding.inputPfName.inputOfTitle.isFocusable = false
        viewBinding.inputPfName.inputOfTitle.setTextColor(getColor(R.color.black_500))
        viewBinding.inputPfName.inputOfTitle.isClickable = false

        //setBirthday
        val userBirth = "1234-05-06"
        viewBinding.inputPfBirthday.inputOfTitle.setText(userBirth)
        viewBinding.inputPfBirthday.inputOfTitle.isFocusable = false
        viewBinding.inputPfBirthday.inputOfTitle.setTextColor(getColor(R.color.black_500))
        viewBinding.inputPfBirthday.inputOfTitle.isClickable = false

        //setSex


        //setLevel

        // START - setLevel
        var isLevelListVisible = false

        var levelString = resources.getStringArray(R.array.pf_level);
        var levelList = ArrayList<AddListItem>()
        for(i in levelString) levelList.add(AddListItem(false, i, 0))
        var selectedLevelIndex = -1
        viewBinding.levelHead.dropdownTitle.text = "능력 정도 선택"

        viewBinding.inputPfLevel.adapter = CallbackSingleRVAdapter(levelList, selectedLevelIndex) {
            Log.d("inputPfLevel", it.toString())
            viewBinding.levelHead.dropdownTitle.text = levelList[it].name
        }
        viewBinding.inputPfLevel.layoutManager = LinearLayoutManager(this)
        viewBinding.levelHead.root.setOnClickListener{
            if(!isLevelListVisible){
                viewBinding.inputPfLevel.visibility = View.VISIBLE
                isLevelListVisible = true
            }else{
                viewBinding.inputPfLevel.visibility = View.GONE
                isLevelListVisible = false
            }
        }
        // End - setLevel


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

        //IntroSection - Start
        val introLimit = 30
        var isIntroOk = false
        viewBinding.inputPfIntro.inputOfTitle.hint = "나를 한 마디로 표현해 보세요. (최대 30자)"
        viewBinding.inputPfIntro.inputOfTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrBlank()) {
                    viewBinding.inputPfIntro.inputOfTitle.error = "대표 문구를 입력하세요."
                    isIntroOk = false
                }else if(s.length > introLimit){
                    viewBinding.inputPfIntro.inputOfTitle.error = "대표 문구가 ${introLimit}자를 초과할 수 없습니다."
                    isIntroOk = false
                }else{
                    viewBinding.inputPfIntro.inputOfTitle.error = null
                    isIntroOk = true
                }
            }
        })
        //IntroSection - End

        //contentSection - Start
        val contentLimit = 500
        viewBinding.contentTextCounter.text = "0/${contentLimit}"

        var isContentOk = false
        viewBinding.inputPfContent.hint = getString(R.string.pf_content_hint)
        viewBinding.inputPfContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrBlank()) {
                    viewBinding.contentTextCounter.text = "0/${contentLimit}"
                    viewBinding.inputPfContent.error = "자기 소개를 입력하세요."
                    isContentOk = false
                }else {
                    viewBinding.contentTextCounter.text = "${s!!.length}/${contentLimit}"
                    if(s.length > contentLimit){
                        viewBinding.inputPfContent.error = "자기 소개가 ${contentLimit}자를 초과할 수 없습니다."
                        isContentOk = false
                    }else{
                        viewBinding.inputPfContent.error = null
                        isContentOk = true
                    }
                }
            }
        })
        //contentSection - End

        //LinkSection - Start
        val linkLimit = 65536
        var maxLinkLimit = 5
        var nowLinkNumber = 0
        viewBinding.addLinkButton.setOnClickListener {
            if(nowLinkNumber >= maxLinkLimit){
                Toast.makeText(this, "외부 링크는 최대 5개까지입니다.", Toast.LENGTH_SHORT).show()
            }else{
                val nowTime = System.currentTimeMillis()
                linkTimeTextHashMap[nowTime] = ""
                val linkView = InputLayoutBinding.inflate(layoutInflater)
                linkView.inputOfTitle.hint = "링크 입력 (최대 65536자)"
                linkView.inputOfTitle.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        linkTimeTextHashMap[nowTime] = s.toString()
                        if(s.isNullOrBlank()) {
                            linkView.inputOfTitle.error = "링크를 입력하세요."
                        }else if(s.length > linkLimit){
                            linkView.inputOfTitle.error = "링크가 ${linkLimit}자를 초과할 수 없습니다."
                        }else{
                            linkView.inputOfTitle.error = null
                        }
                    }
                })
                linkView.cancelButton.visibility = View.VISIBLE
                linkView.cancelButton.setOnClickListener {
                    viewBinding.addLinkSection.removeView(linkView.root)
                    linkTimeTextHashMap.remove(nowTime)
                    nowLinkNumber -= 1
                }

                linkView.inputOfTitle.hint = "링크를 입력하세요."
                viewBinding.addLinkSection.addView(linkView.root)
                nowLinkNumber += 1
            }
            
        }
        //LinkSection - End

        //setViewData
        val extras = intent.extras
        //TODO: 객체가 존재하면 객체 내용을 페이지에 적용하고, toolbar과 제출하기의 text도 "수정하기"로 바꾸자
        //checkIsNew
        var isOld = false
        if (extras != null) {
            isOld = true
            viewBinding.toolbarTitle.toolbarText.text = getString(R.string.edit_pf_toolbar)
            viewBinding.submitButton.text = "수정하기"

            val oldData: EditPF = extras.get("pf") as EditPF

            //setTitle
            viewBinding.inputPfTitle.inputOfTitle.setText(oldData.title)
            //=================================

            //setName
            viewBinding.inputPfName.inputOfTitle.setText(oldData.realName)

            //setBirthday
            viewBinding.inputPfBirthday.inputOfTitle.setText(oldData.birthday)

            //setSex
            if(oldData.isMan){
                viewBinding.boyIcon.setImageResource(R.drawable.sex_icon_selected_round)
                viewBinding.textBoy.setTextColor(resources.getColor(R.color.green_900) )
            }else {
                viewBinding.girlIcon.setImageResource(R.drawable.sex_icon_selected_round)
                viewBinding.textGirl.setTextColor(resources.getColor(R.color.green_900))
            }

            //setPartName
            viewBinding.levelHead.dropdownTitle.text = oldData.level
            //=================================

            //setChipGroup
            for(i in oldData.languageIdNameMap.keys){
                val oldChip = AddListItem(false, oldData.languageIdNameMap[i]!!, i)
                addPageFunction.changeItem2True(allStackList, oldData.languageIdNameMap[i]!!)
                addOrRemoveChip(oldChip)
                viewBinding.stack2List.adapter!!.notifyDataSetChanged()
            }

            viewBinding.inputPfIntro.inputOfTitle.setText(oldData.intro)

            //setLocation
            viewBinding.inputPfContent.setText(oldData.content)

            //setLinkList
            //TODO: link List Add
        }


        viewBinding.submitButton.setOnClickListener {
            val finalTitle = viewBinding.inputPfTitle.inputOfTitle.text.toString()
            Log.d("finalTitle", finalTitle)

            val finalLevel = viewBinding.levelHead.dropdownTitle.text.toString()
            Log.d("finalLevel", finalLevel)

            val finalStackList = ArrayList<Int>();
            for(i in chipList.keys){
                finalStackList.add(i.numberInServer)
            }
            Log.d("finalStackList", finalStackList.toString())

            val finalIntro = viewBinding.inputPfIntro.inputOfTitle.text.toString()
            Log.d("finalIntro", finalIntro)

            val finalContent = viewBinding.inputPfContent.text.toString()
            Log.d("finalContent", finalContent)

            val finalLinkList = ArrayList<String>()
            for(i in linkTimeTextHashMap.values){
                finalLinkList.add(i)
            }
            Log.d("finalLinkList", finalLinkList.toString())

            var toastString = ""
            if(!isTitleOk){
                toastString += "제목, "
            }

            var isLevelOk = true
            if(finalLevel == "능력 정도 선택"){
                toastString += "능력, "
                isLevelOk = false
            }

            if(!isIntroOk){
                toastString += "대표 문구, "
            }

            if(!isContentOk){
                toastString += "자기 소개, "
            }

            var isLinkOk = true
            for(i in finalLinkList){
                if (i.isEmpty()){
                    toastString += "링크, "
                    isLinkOk = false
                    break
                }
            }

            if(isTitleOk and isLevelOk and isIntroOk and isContentOk and isLinkOk){
                var result = project2Server.postNewPF(this, finalTitle, finalLevel, finalIntro, finalContent, finalStackList, finalLinkList)
                if(result){

                    finish()
                }
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



    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

}