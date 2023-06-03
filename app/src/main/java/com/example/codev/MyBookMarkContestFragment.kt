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
import com.example.codev.databinding.FragmentMyBookmarkContestBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyBookMarkContestFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyBookmarkContestBinding

    private lateinit var adapterData: AdapterMyBookmarkContestList

    override fun onResume() {
        super.onResume()
        loadData(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyBookmarkContestBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadData(context: Context){
        RetrofitClient.service.getHeartedContest(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())).enqueue(object: Callback<ResBookMarkContestList> {
            override fun onResponse(call: Call<ResBookMarkContestList>, response: Response<ResBookMarkContestList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 북마크 프로젝트 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 찜한 프로젝트 불러오기 성공", "\n${it.toString()}")
                            setAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResBookMarkContestList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setAdapter(dataList: ArrayList<BookmarkCData>, context: Context){
        if(dataList.size != 0) {
            adapterData = AdapterMyBookmarkContestList(context, dataList)
            viewBinding.listviewContest.adapter = adapterData
        }
    }
}
