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
import com.example.codev.databinding.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CommunityInfoFragment :Fragment(){
    private lateinit var viewBinding: FragmentCommunityInfoBinding

    private lateinit var adapterIData: AdapterCommunityInfoList

    private var idataList: ArrayList<QIData> = ArrayList()

    private var coMyBoard:Boolean = false //초기값 false

    private var downpage: Int = 0
    private var lastPage: Boolean = false

    private lateinit var mainAppActivity: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()

        idataList = ArrayList() //초기화

        loadData(mainAppActivity, downpage, coMyBoard) //기본으로 0page PData 가져오기

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCommunityInfoBinding.inflate(layoutInflater)


        //자동페이징 처리 부분
        viewBinding.listviewInfo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() - 2
                val lastPosition = recyclerView.adapter!!.itemCount - 3 //원래 1

                if((lastVisibleItemPosition == lastPosition) && !lastPage){ //처음에 false
                    downpage += 1
                    loadData(mainAppActivity, downpage, coMyBoard)
                }
            }
        })

        return viewBinding.root
    }

    //전체 정보글 조회
    private fun loadData(context: Context, int: Int, coMyBoard:Boolean) {
        RetrofitClient.service.requestIDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            int, coMyBoard).enqueue(object: Callback<ResGetCommunityList1>{
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
                                Log.d("test: 매개변수: ",coMyBoard.toString())
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                    lastPage = true
                                    if(int == 0) { //0page
                                        // 1.페이지가 끝이라서 그 다음페이지 결과가 []인거 --> int != 0
                                        // 2.필터링 결과가 아무것도 없는거 --> int == 0
                                        setQAdapter(idataList) //projectAdapter
                                    }
                                }
                                //페이지에 내용이 있으면
                                else {
                                    idataList.addAll(it.result.success)
                                    if(int == 0) { //0page
                                        setQAdapter(idataList) //projectAdapter
                                    }
                                    else{
                                        viewBinding.listviewInfo.adapter!!.notifyDataSetChanged()
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

    private fun setQAdapter(infoList: ArrayList<QIData>){
        val adapter = AdapterCommunityInfoList(mainAppActivity,infoList)
        viewBinding.listviewInfo.adapter = adapter
    }

}
