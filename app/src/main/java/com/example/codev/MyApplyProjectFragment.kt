package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.ActivityMyBookmarkBinding
import com.example.codev.databinding.FragmentMyApplyProjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyApplyProjectFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyApplyProjectBinding

    private lateinit var adapterPData: AdapterMyApplyProjectList

    override fun onResume() {
        super.onResume()
        loadPData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyApplyProjectBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadPData(context: Context){
        RetrofitClient.service.getApplyList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
            ,"project").enqueue(object: Callback<ResApplyList> {
            override fun onResponse(call: Call<ResApplyList>, response: Response<ResApplyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 지원한 프로젝트 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 지원한 프로젝트 불러오기 성공", "\n${it.toString()}")
                            setPAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResApplyList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setPAdapter(projectList: ArrayList<ApplyData>, context: Context){
        if(projectList.size != 0) {
            adapterPData = AdapterMyApplyProjectList(context, projectList)
            viewBinding.listviewProject.adapter = adapterPData
        }
    }
}
