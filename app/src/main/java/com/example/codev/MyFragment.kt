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
import com.example.codev.databinding.FragmentMyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFragment:Fragment() {
    private lateinit var viewBinding: FragmentMyBinding
    private lateinit var mainAppActivity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyBinding.inflate(layoutInflater)
        viewBinding.toolbarMy.toolbar1.inflateMenu(R.menu.menu_toolbar_1)
        viewBinding.toolbarMy.toolbar1.title = ""
        viewBinding.toolbarMy.toolbarImg.setImageResource(R.drawable.logo_my)


        loadData(mainAppActivity,0)

        viewBinding.btnMore.setOnClickListener {
            val intent = Intent(mainAppActivity, MyPortfolioActivity::class.java)
            startActivity(intent)
        }

        return viewBinding.root
    }

    private fun setAdapter(dataList: ArrayList<PortFolio>){
        val adapter = PortfolioAdapter1(dataList)
        viewBinding.recyclePortfolio.adapter = adapter
    }

    private fun loadData(context: Context, int: Int){
        RetrofitClient.service.getPortFolio(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),int).enqueue(object: Callback<ResPortFolioList>{
            override fun onResponse(call: Call<ResPortFolioList>, response: Response<ResPortFolioList>) {
                if(response.isSuccessful.not()){
                    Log.d("test",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test", "\n${it.toString()}")
                            setAdapter(it.result)
                        }
                    }
                }

            }

            override fun onFailure(call: Call<ResPortFolioList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }
}