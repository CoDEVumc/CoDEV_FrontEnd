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
import com.codev.android.databinding.FragmentMyBookmarkProjectBinding
import com.codev.android.databinding.FragmentMyWriteCommentedBinding
import com.codev.android.databinding.FragmentMyWritePostedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyWriteCommentedFragment :Fragment(){
    private lateinit var viewBinding: FragmentMyWriteCommentedBinding

    private lateinit var adapterPData: AdapterMyWriteCommentedList

    override fun onResume() {
        super.onResume()
        /*
        loadPData(requireContext())
        */
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyWriteCommentedBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    private fun loadPData(context: Context){
        RetrofitClient.service.getHeartedProject(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())).enqueue(object: Callback<ResBookMarkProjectList> {
            override fun onResponse(call: Call<ResBookMarkProjectList>, response: Response<ResBookMarkProjectList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 북마크 프로젝트 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 찜한 프로젝트 불러오기 성공", "\n${it.toString()}")
                            setPAdapter(it.result.Complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResBookMarkProjectList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setPAdapter(projectList: ArrayList<BookmarkPData>, context: Context){
        if(projectList.size != 0) {
            adapterPData = AdapterMyWriteCommentedList(context, projectList)
            viewBinding.listviewCommented.adapter = adapterPData
        }
    }
}
