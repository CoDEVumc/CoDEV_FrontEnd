package com.codev.android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.codev.android.databinding.FragmentMyWriteBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MyWriteFragment: Fragment() {
    private lateinit var viewBinding: FragmentMyWriteBinding
    //private lateinit var adapterPSData: AdapterMyWritePostedList

    private var coMyBoard:Boolean = true //초기값 false, true면 내가 쓴 글
    private var downpage: Int = 0
    private var lastPage: Boolean = false
    private var sortingTag: String = "" //정렬버튼

    private var type: String = "project" //플젝or스터디 - 기본 프로젝트

    private var qdataList: ArrayList<QIData> = ArrayList()
    private var idataList: ArrayList<QIData> = ArrayList()


    override fun onResume() {
        super.onResume()

        //기본은 내가 쓴 프로젝트 글 보이게
        loadPSData(requireContext(), "project")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyWriteBinding.inflate(layoutInflater)

        //기본
        loadPSData(requireContext(), "project")

        viewBinding.radioGroup.setOnCheckedChangeListener { radioGroup, checkID ->
            when(checkID) {
                R.id.btn_project -> {
                    Log.d("MyWritePostedFragment ", "프로젝트 버튼 클릭")
                    loadPSData(requireContext(), "project")

                    val psList = ArrayList<PSData>()
                    val adapterPSData = AdapterMyWritePostedList(requireContext(), psList)
                    viewBinding.listviewPosted.adapter = adapterPSData
                    viewBinding.listviewMain.scrollToPosition(0) // 스크롤 초기화
                }
                R.id.btn_study -> {
                    Log.d("MyWritePostedFragment ", "스터디 버튼 클릭")
                    loadPSData(requireContext(), "study")

                    val psList = ArrayList<PSData>()
                    val adapterPSData = AdapterMyWritePostedList(requireContext(), psList)
                    viewBinding.listviewPosted.adapter = adapterPSData
                    viewBinding.listviewMain.scrollToPosition(0) // 스크롤 초기화
                }
                R.id.btn_question -> {
                    Log.d("MyWritePostedFragment ", "질문 버튼 클릭")
                    loadQData(requireContext(), downpage, true, sortingTag)

                    val psList = ArrayList<PSData>()
                    val adapterPSData = AdapterMyWritePostedList(requireContext(), psList)
                    viewBinding.listviewPosted.adapter = adapterPSData
                    viewBinding.listviewMain.scrollToPosition(0) // 스크롤 초기화
                }
                R.id.btn_info -> {
                    Log.d("MyWritePostedFragment ", "정보 버튼 클릭")
                    loadIData(requireContext(), downpage, true, sortingTag)

                    val psList = ArrayList<PSData>()
                    val adapterPSData = AdapterMyWritePostedList(requireContext(), psList)
                    viewBinding.listviewPosted.adapter = adapterPSData
                    viewBinding.listviewMain.scrollToPosition(0) // 스크롤 초기화
                }
                R.id.btn_contest -> {
                    Log.d("MyWritePostedFragment ", "공모전 버튼 클릭")
                }
            }
        }


        return viewBinding.root
    }

    //전체 질문글 조회
    private fun loadQData(context: Context, int: Int, coMyBoard: Boolean, sortingTag: String) {
        RetrofitClient.service.requestQDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),
            int, coMyBoard, sortingTag).enqueue(object: Callback<ResGetCommunityList1>{
            override fun onResponse(call: Call<ResGetCommunityList1>, response: Response<ResGetCommunityList1>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 질문글 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 질문글 데이터 : ", "\n${it.result.success}")
                                Log.d("test: 매개변수: ",coMyBoard.toString()+sortingTag)

                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    lastPage = true
                                    if(int == 0) { //0page
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setAdapter1(context,qdataList) //projectAdapter
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    qdataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setAdapter1(context,qdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewPosted.adapter!!.notifyDataSetChanged()
                                        //viewBinding.listviewMain.adapter!!.notifyItemRangeInserted(downpage*10-1,10)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetCommunityList1>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_q(질문글 전체조회): ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter1(context: Context, questionList: ArrayList<QIData>){
        val adapter = AdapterCommunityQuestionList(context,questionList)
        viewBinding.listviewPosted.adapter = adapter

    }

    //전체 정보글 조회
    private fun loadIData(context: Context, int: Int, coMyBoard: Boolean, sortingTag: String) {
        RetrofitClient.service.requestIDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),
            int, coMyBoard, sortingTag).enqueue(object: Callback<ResGetCommunityList1>{
            override fun onResponse(call: Call<ResGetCommunityList1>, response: Response<ResGetCommunityList1>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 정보글 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 정보글 데이터 : ", "\n${it.result.success}")
                                Log.d("test: 매개변수: ",coMyBoard.toString()+sortingTag)

                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    lastPage = true
                                    if(int == 0) { //0page
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setAdapter2(context, idataList) //projectAdapter
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    idataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setAdapter2(context, idataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewPosted.adapter!!.notifyDataSetChanged()
                                        //viewBinding.listviewMain.adapter!!.notifyItemRangeInserted(downpage*10-1,10)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetCommunityList1>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_q(질문글 전체조회): ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter2(context: Context, infoList: ArrayList<QIData>){
        val adapter = AdapterCommunityInfoList(context, infoList)
        viewBinding.listviewPosted.adapter = adapter
    }

    //프로젝트, 스터디 조회
    private fun loadPSData(context: Context, type: String){
        RetrofitClient.service.getWrittenList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), type).enqueue(object: Callback<ResGetPSList> {
            override fun onResponse(call: Call<ResGetPSList>, response: Response<ResGetPSList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 내가 작성한 플젝, 스터디 글 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 내가 작성한 플젝, 스터디 글 불러오기 성공", "\n${it.toString()}")
                            setAdapter3(it.result.Complete, context)


                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetPSList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setAdapter3(psList: ArrayList<PSData>, context: Context){
        val adapterPSData = AdapterMyWritePostedList(context, psList)
        viewBinding.listviewPosted.adapter = adapterPSData
    }




}