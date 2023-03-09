package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.ActivityRecruitSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitSearchActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitSearchBinding
    private val pDataList: ArrayList<PData> = ArrayList()
    private val sDataList: ArrayList<SData> = ArrayList()
    private var coKeyword: String = ""
    private var coProcessTag: String = ""
    private var coSortingTag: String = ""
    private var downPage: Int = 0
    private var now : Int = 0 // 0-프로젝트 / 1- 스터디
    private var projectLastPage: Boolean = false
    private var studyLastPage: Boolean = false
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitSearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarSearch.toolbar5.title = ""

        setSupportActionBar(viewBinding.toolbarSearch.toolbar5)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.toolbarSearch.btnCancel.isGone = true

        viewBinding.toolbarSearch.btnSearch.setOnClickListener {
            Log.d("test","검색")
            downPage = 0
            coKeyword = viewBinding.toolbarSearch.etKeyword.text.toString()
            loadProjectData(context, downPage, coKeyword)
        }

        viewBinding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1

                if((lastVisibleItemPosition == lastPosition) && (!projectLastPage || !studyLastPage)){ //처음에 false
                    downPage += 1
                    when(now){
                        0 -> loadProjectData(context, downPage, coKeyword)
                        1 -> loadStudyData(context, downPage, coKeyword)
                    }
                }
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

    private fun setAdapter(projectList: ArrayList<PData>, studyList: ArrayList<SData>){
        val adapter = AdapterRecruitSearchList(this, projectList, studyList)
        viewBinding.rvList.adapter = adapter
    }

    private fun loadProjectData(context: Context, int: Int, coKeyword:String) {
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this)),
            int, "", "", coKeyword, "", "").enqueue(object: Callback<ResGetProjectList> {
            @SuppressLint("NotifyDataSetChanged")
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
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    projectLastPage = true
                                    if(int == 0) {
                                        setAdapter(pDataList, sDataList)
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    pDataList.addAll(it.result.success)
                                    if(int == 0) {
                                        setAdapter(pDataList, sDataList)
                                    }
                                }
                                viewBinding.rvList.adapter!!.notifyDataSetChanged()
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

    private fun loadStudyData(context: Context, int: Int, coKeyword:String) {
        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this)),
            int, "", "", coKeyword, "", "").enqueue(object: Callback<ResGetStudyList> {
            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 스터디 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 스터디 데이터 :", "\n${it.result.success}")
                                Log.d("test: 매개변수: ",coKeyword)
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    studyLastPage = true
                                }
                                //페이지에 내용이 있으면
                                else {
                                    sDataList.addAll(it.result.success)
                                }
                                viewBinding.rvList.adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetStudyList>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}