package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySearchBinding
    private var pdataList: ArrayList<PData> = ArrayList()
    private var sdataList: ArrayList<SData> = ArrayList()
    private var coKeyword: String = ""
    private var downpage: Int = 0
    private var lastPage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarSearch.toolbar5.title = ""

        setSupportActionBar(viewBinding.toolbarSearch.toolbar5)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.toolbarSearch.btnSearch.setOnClickListener {
            downpage = 0
            coKeyword = viewBinding.toolbarSearch.etKeyword.text.toString()
            loadData(this, downpage, coKeyword)
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

    private fun setPAdapter(projectList: ArrayList<PData>){
        val adapter = AdapterRecruitProjectList(this, projectList)
        viewBinding.rvList.adapter = adapter
    }

    private fun setSAdapter(studyList: ArrayList<SData>){
        val adapter = AdapterRecruitStudyList(this, studyList)
        viewBinding.rvList.adapter = adapter
    }

    private fun loadData(context: Context, int: Int, coKeyword:String) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this)),
            int, "", "", coKeyword, "", "").enqueue(object: Callback<ResGetProjectList> {
            override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 플젝 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 플젝 데이터 : ", "\n${it.result.success}")
                                Log.d("test: 매개변수: ",coKeyword)
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    lastPage = true
                                    if(int == 0) { //0page
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setPAdapter(pdataList) //projectAdapter
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    pdataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setPAdapter(pdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.rvList.adapter!!.notifyDataSetChanged()
                                        //viewBinding.listviewMain.adapter!!.notifyItemRangeInserted(downpage*10-1,10)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_p(플젝 전체조회): ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}