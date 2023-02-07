package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentMyApplyStudyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyApplyStudyFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyApplyStudyBinding

    private lateinit var adapterSData: AdapterMyApplyStudyList

    override fun onResume() {
        super.onResume()
        loadSData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyApplyStudyBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadSData(context: Context){
        RetrofitClient.service.getApplyList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
            ,"study").enqueue(object:Callback<ResApplyList> {
            override fun onResponse(call: Call<ResApplyList>, response: Response<ResApplyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 지원한 스터디 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 지원한 스터디 불러오기 성공", "\n${it.toString()}")
                            setSAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResApplyList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setSAdapter(studyList: ArrayList<ApplyData>, context: Context){
        if(studyList.size != 0){
            adapterSData = AdapterMyApplyStudyList(context, studyList)
            viewBinding.listviewStudy.adapter = adapterSData
        }
    }
}