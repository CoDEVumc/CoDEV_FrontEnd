package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.ActivityMyBookmarkBinding
import com.example.codev.databinding.FragmentMyBookmarkProjectBinding
import com.example.codev.databinding.FragmentMyWriteBinding
import com.example.codev.databinding.FragmentMyWritePostedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyWritePostedFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyWritePostedBinding

    private lateinit var adapterPData: AdapterMyWritePostedList

    private var coMyBoard:Boolean = true //초기값 false, true면 내가 쓴 글
    private var downpage: Int = 0
    private var lastPage: Boolean = false
    private var sortingTag: String = "" //정렬버튼

    //val now = arguments?.getInt("now") //MyWriteFragment로부터 받은 현재 토글

    private var type: String = "project" //플젝or스터디 - 기본 프로젝트

    private var qdataList: ArrayList<QIData> = ArrayList()
    private var idataList: ArrayList<QIData> = ArrayList()


    override fun onResume() {
        super.onResume()

        //기본은 내가 쓴 프로젝트 글 보이게
        //loadData()
        //기본으로 질문정보글 보여지게 해놨는데 이거 플젝으로 바꿔야됨
        qdataList = ArrayList()
        idataList = ArrayList() //초기화

        loadQData(requireContext(), downpage, coMyBoard, sortingTag)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyWritePostedBinding.inflate(layoutInflater)

        //자동페이징 처리 부분
        viewBinding.listviewPosted.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1

                if((lastVisibleItemPosition == lastPosition) && !lastPage){ //처음에 false
                    downpage += 1
                    loadQData(requireContext(), downpage, coMyBoard, sortingTag)
                }
            }
        })

        downpage = 0
        lastPage = false
        coMyBoard = true
        qdataList = ArrayList()
        loadQData(requireContext(), downpage, coMyBoard, sortingTag) //이거 loadPSData() 로 바꿔


        //var toggle = arguments?.getInt("toggle")

        val toggle = arguments
        if (toggle != null) {
            val receivedValue = toggle.getInt("key")
            Log.d("MyWritePostedFragment : ", "toggle: "+receivedValue.toString())

            if (receivedValue != null) {
                when(receivedValue) {
                    0 -> { //프로젝트
                        Log.d("MyWritePostedFragment : ", "toggle: "+toggle.toString())
                        loadPSData(requireContext())
                    }
                    1 -> { //스터디
                        Log.d("MyWritePostedFragment : ", "toggle: "+toggle.toString())
                        loadPSData(requireContext())
                    }
                    2 -> { //질문글
                        Log.d("MyWritePostedFragment : ", "toggle: "+toggle.toString())
                        loadQData(requireContext(), downpage, coMyBoard, sortingTag) //이거 loadPSData() 로 바꿔
                    }
                    3 -> { //정보글
                        Log.d("MyWritePostedFragment : ", "toggle: "+toggle.toString())
                        loadIData(requireContext(), downpage, coMyBoard, sortingTag) //이거 loadPSData() 로 바꿔
                    }
                }

            }
        }






        return viewBinding.root
    }

    //전체 질문글 조회
    private fun loadQData(context: Context, int: Int, coMyBoard: Boolean, sortingTag: String) {
        RetrofitClient.service.requestQDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),
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
        val adapter = AdapterCommunityQuestionList(requireContext(),questionList)
        viewBinding.listviewPosted.adapter = adapter
    }

    //전체 정보글 조회
    private fun loadIData(context: Context, int: Int, coMyBoard: Boolean, sortingTag: String) {
        RetrofitClient.service.requestIDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),
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
        val adapter = AdapterCommunityInfoList(context,infoList)
        viewBinding.listviewPosted.adapter = adapter
    }

    //프로젝트, 스터디 조회
    private fun loadPSData(context: Context){
        RetrofitClient.service.getHeartedProject(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object: Callback<ResBookMarkProjectList> {
            override fun onResponse(call: Call<ResBookMarkProjectList>, response: Response<ResBookMarkProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 북마크 프로젝트 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 찜한 프로젝트 불러오기 성공", "\n${it.toString()}")
                            setAdapter3(it.result.Complete, context)


                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResBookMarkProjectList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setAdapter3(projectList: ArrayList<BookmarkPData>, context: Context){
        if(projectList.size != 0) {
            adapterPData = AdapterMyWritePostedList(context, projectList)
            viewBinding.listviewPosted.adapter = adapterPData
        }
    }






}
