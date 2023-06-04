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
import com.codev.android.databinding.FragmentMyBookmarkQuestionAndInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyBookMarkQuestionAndInfoFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyBookmarkQuestionAndInfoBinding

    private lateinit var adapterData: AdapterMyBookmarkQuestionAndInfoList

    override fun onResume() {
        super.onResume()
        loadData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyBookmarkQuestionAndInfoBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadData(context: Context){
        RetrofitClient.service.getHeartedQustionAndInfo(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())).enqueue(object: Callback<ResBookMarkQuestionAndInfoList> {
            override fun onResponse(call: Call<ResBookMarkQuestionAndInfoList>, response: Response<ResBookMarkQuestionAndInfoList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 북마크 글 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 북마크 글 불러오기 성공", "\n${it.toString()}")
                            setAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResBookMarkQuestionAndInfoList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setAdapter(dataList: ArrayList<BookmarkQIData>, context: Context){
        if(dataList.size != 0) {
            adapterData = AdapterMyBookmarkQuestionAndInfoList(context, dataList)
            viewBinding.listviewQuestionAndInfo.adapter = adapterData
        }
    }
}
