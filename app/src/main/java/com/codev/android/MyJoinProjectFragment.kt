package com.codev.android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codev.android.databinding.ActivityMyBookmarkBinding
import com.codev.android.databinding.FragmentMyJoinProjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyJoinProjectFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyJoinProjectBinding

    private lateinit var adapterPData: AdapterMyJoinProjectList

    override fun onResume() {
        super.onResume()
        loadPData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyJoinProjectBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadPData(context: Context){
        RetrofitClient.service.getJoinList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())
            ,"project").enqueue(object: Callback<ResJoinList> {
            override fun onResponse(call: Call<ResJoinList>, response: Response<ResJoinList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 참여한 프로젝트 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 참여한 프로젝트 불러오기 성공", "\n${it.toString()}")
                            setPAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResJoinList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setPAdapter(projectList: ArrayList<JoinData>, context: Context){
        if(projectList.size != 0) {
            adapterPData = AdapterMyJoinProjectList(context, projectList)
            viewBinding.listviewProject.adapter = adapterPData
        }
    }
}
