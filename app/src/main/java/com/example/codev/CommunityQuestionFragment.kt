package com.example.codev

import android.content.Context
import android.content.Intent
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
import com.example.codev.databinding.FragmentCommunityQuestionBinding
import com.example.codev.databinding.FragmentMyBookmarkProjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CommunityQuestionFragment :Fragment(){
    private lateinit var viewBinding: FragmentCommunityQuestionBinding

    private lateinit var adapterQData: AdapterCommunityQuestionList

    private var qdataList: ArrayList<QIData> = ArrayList()

    private var coMyBoard:Boolean = false //초기값 false, true면 내가 쓴 글

    private var downpage: Int = 0
    private var lastPage: Boolean = false
    private var sortingTag: String = "" //정렬버튼

    private lateinit var mainAppActivity: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()

        qdataList = ArrayList() //초기화

        loadData(mainAppActivity, downpage, coMyBoard, sortingTag) //기본으로 0page PData 가져오기

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCommunityQuestionBinding.inflate(layoutInflater)

        //자동페이징 처리 부분
        viewBinding.listviewQuestion.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1

                if((lastVisibleItemPosition == lastPosition) && !lastPage){ //처음에 false
                    downpage += 1
                    loadData(mainAppActivity, downpage, coMyBoard, sortingTag)
                }
            }
        })


        //내가 쓴 글만 보기 버튼
        viewBinding.recruitingQuestionBtn.setOnClickListener {
            //필터링 다른거 적용이 이중으로 안돼
            downpage = 0
            lastPage = false
            coMyBoard = viewBinding.recruitingQuestionBtn.isChecked
            //아래 코드 압축한게 coMyBoard = viewBinding.recruitingQuestionBtn.isChecked
//            if(viewBinding.recruitingQuestionBtn.isChecked){
//                coMyBoard = true
//            }
//            else{
//                coMyBoard = false
//            }

            qdataList = ArrayList()
            loadData(mainAppActivity, downpage, coMyBoard, sortingTag)
        }

        //정렬 누르고 최신순or추천순 선택하면 <- 이거 api랑 일단은 아다리 안맞음
        val bottomSheetSort = BottomSheetSort(){
            downpage = 0
            lastPage = false
            sortingTag = it // ""이거나 populaRity
            if(sortingTag != "") { //populaRity : 추천순
                viewBinding.sort.text = "추천순"
                sortingTag = "POPULARITY"
            }
            else{ //아무것도 없 : 최신순
                viewBinding.sort.text = "최신순"
            }

            qdataList = ArrayList()
            loadData(mainAppActivity, downpage, coMyBoard, sortingTag)

            Log.d("coSortingTag: ",sortingTag)
        }

        //정렬 버튼
        viewBinding.sort.setOnClickListener {
            bottomSheetSort.show(childFragmentManager, bottomSheetSort.tag)
        }
        viewBinding.filterSort.setOnClickListener {
            bottomSheetSort.show(childFragmentManager, bottomSheetSort.tag)
        }



        return viewBinding.root
    }



    //전체 질문글 조회
    private fun loadData(context: Context, int: Int, coMyBoard: Boolean, sortingTag: String) {
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
                                        setQAdapter(qdataList) //projectAdapter
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    qdataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setQAdapter(qdataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewQuestion.adapter!!.notifyDataSetChanged()
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

    private fun setQAdapter(questionList: ArrayList<QIData>){
        val adapter = AdapterCommunityQuestionList(mainAppActivity,questionList)
        viewBinding.listviewQuestion.adapter = adapter
    }

}
