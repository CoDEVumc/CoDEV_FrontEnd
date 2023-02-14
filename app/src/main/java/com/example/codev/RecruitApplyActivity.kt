package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.codev.addpage.AddListItem
import com.example.codev.addpage.CallbackSingleRVAdapter
import com.example.codev.databinding.ActivityRecruitApplyBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class RecruitApplyActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitApplyBinding
    private lateinit var adapter: AdapterPortfolio3
    private var portfolioId: Int = -1
    private var part: String = ""
    private var partChooseStatus: Int = -1


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitApplyBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbarRecruit.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val recruitId = intent.getIntExtra("recruitId",-1)
        val type = intent.getStringExtra("type")
        val writer = intent.getStringExtra("writer")
        val process = intent.getStringExtra("process")
        val recruitStatus = intent.getBooleanExtra("recruitStatus", false)
        val partList = intent.getSerializableExtra("partList") as ArrayList<RecruitPartLimit>


        viewBinding.toolbarRecruit.toolbar2.title = ""
        if (type == "PROJECT"){
            viewBinding.toolbarRecruit.toolbarText.text = "프로젝트 지원하기"
            viewBinding.part.dropdownTitle.text = "분야 선택"
            viewBinding.part.dropdownTitle.setTextColor(getColor(R.color.black_300))

            var partListVisible = false
            val partSelectList = ArrayList<AddListItem>()
            for(i in partList) {
                if (i.co_limit != 0){
                    partSelectList.add(AddListItem(false, i.co_part, 0))
                }
            }

            viewBinding.partList.adapter = CallbackSingleRVAdapter(partSelectList, -1){
                Log.d("test", it.toString())
                partChooseStatus = it
                viewBinding.part.dropdownTitle.text = partSelectList[it].name
                part = partSelectList[it].name
                if (viewBinding.part.dropdownTitle.currentTextColor != getColor(R.color.green_900)){
                    viewBinding.part.dropdownTitle.setTextColor(getColor(R.color.green_900))
                }
                checkNextBtn()
            }

            viewBinding.part.root.setOnClickListener{
                if(!partListVisible){
                    viewBinding.partList.visibility = View.VISIBLE
                    partListVisible = true
                }else{
                    viewBinding.partList.visibility = View.GONE
                    partListVisible = false
                }
            }
        }else if (type == "STUDY"){
            viewBinding.toolbarRecruit.toolbarText.text = "스터디 지원하기"
            viewBinding.textPart.isGone = true
            viewBinding.part.root.isGone = true
            viewBinding.partList.isGone = true
            partChooseStatus = 0
            checkNextBtn()
        }

        loadData(this)

        viewBinding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    viewBinding.contentTextCounter.text = s.length.toString() + "/500"
                }
            }
        })

        viewBinding.submitButton.setOnClickListener {
            Log.d("test", "지원")
            if (type != null && writer != null && process != null) {
                apply(this, type, recruitId, recruitStatus, writer, process)
            }
            finish()
        }
    }

    private fun setAdapter(dataList: ArrayList<PortFolio>){
        adapter = AdapterPortfolio3(dataList, -1){
            Log.d("test", "선택한 포트폴리오 id : $it")
            portfolioId = it
            checkNextBtn()
        }
        viewBinding.portfolio.adapter = adapter
    }

    private fun loadData(context: Context){
        RetrofitClient.service.getPortFolio(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object:
            Callback<ResPortFolioList> {
            override fun onResponse(call: Call<ResPortFolioList>, response: Response<ResPortFolioList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 불러오기 성공", "\n${it.toString()}")
                            setAdapter(it.result.Portfolio)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResPortFolioList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
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

    private fun checkNextBtn() {
        if(partChooseStatus != -1 && portfolioId != -1){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
    }

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.submitButton.isSelected != boolean){
            viewBinding.submitButton.isSelected = boolean
            viewBinding.submitButton.isEnabled = boolean
            if(boolean){
                viewBinding.submitButton.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.submitButton.setTextColor(getColor(R.color.black_500))
            }
        }
    }

    private fun apply(context: Context, type: String, id:Int, recruitStatus: Boolean, writer: String, process: String){
        if (type == "PROJECT"){
            RetrofitClient.service.applyProject(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),id, ReqApplyProject(portfolioId, part, viewBinding.etContent.text.toString(), recruitStatus, writer, process)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 지원 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 지원 성공", "\n${it.toString()}")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }else if (type == "STUDY"){
            RetrofitClient.service.applyStudy(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),id, ReqApplyStudy(portfolioId, viewBinding.etContent.text.toString(), recruitStatus, writer, process)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 지원 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 지원 성공", "\n${it.toString()}")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }
    }
}