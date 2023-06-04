package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentMyJoinStudyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyJoinStudyFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyJoinStudyBinding

    private lateinit var adapterSData: AdapterMyJoinStudyList

    override fun onResume() {
        super.onResume()
        loadSData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyJoinStudyBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadSData(context: Context){
        RetrofitClient.service.getJoinList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())
            ,"study").enqueue(object:Callback<ResJoinList> {
            override fun onResponse(call: Call<ResJoinList>, response: Response<ResJoinList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 참여한 스터디 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 참여한 스터디 불러오기 성공", "\n${it.toString()}")
                            setSAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResJoinList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setSAdapter(studyList: ArrayList<JoinData>, context: Context){
        if(studyList.size != 0){
            adapterSData = AdapterMyJoinStudyList(context, studyList)
            viewBinding.listviewStudy.adapter = adapterSData
        }
    }
}