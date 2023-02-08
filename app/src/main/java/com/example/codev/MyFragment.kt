package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.codev.databinding.FragmentMyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFragment:Fragment() {
    private lateinit var viewBinding: FragmentMyBinding
    private lateinit var mainAppActivity: Context
    private lateinit var userinfo: Userinfo


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("test","onResume")
        loadData(mainAppActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyBinding.inflate(layoutInflater)
        viewBinding.toolbarMy.toolbar1.inflateMenu(R.menu.menu_toolbar_my)
        viewBinding.toolbarMy.toolbar1.title = ""
        viewBinding.toolbarMy.toolbarImg.setImageResource(R.drawable.logo_my)
        viewBinding.toolbarMy.toolbar1.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_setting ->{
                    Toast.makeText(mainAppActivity, "설정", Toast.LENGTH_SHORT).show()
                    val intent = Intent(mainAppActivity, MySettingActivity::class.java)
                    intent.putExtra("userinfo",userinfo)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        viewBinding.profile.setOnClickListener {
            val intent = Intent(mainAppActivity, MyProfileActivity::class.java)
            intent.putExtra("userinfo",userinfo)
            startActivity(intent)
        }

        viewBinding.btnMore.setOnClickListener {
            val intent = Intent(mainAppActivity, MyPortfolioActivity::class.java)
            intent.putExtra("userinfo",userinfo)
            startActivity(intent)
        }

        viewBinding.btnBookmark.setOnClickListener {
            val intent = Intent(mainAppActivity, MyBookMarkActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnApply.setOnClickListener {
            val intent = Intent(mainAppActivity, MyApplyActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnJoin.setOnClickListener {
            val intent = Intent(mainAppActivity, MyJoinActivity::class.java)
            startActivity(intent)
        }






        return viewBinding.root
    }

    private fun setAdapter(dataList: ArrayList<PortFolio>){
        val adapter = AdapterPortfolio1(dataList, userinfo.co_name, userinfo.co_birth, userinfo.co_gender)
        viewBinding.recyclePortfolio.adapter = adapter
    }

    private fun loadData(context: Context){
        RetrofitClient.service.getPortFolio(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object: Callback<ResPortFolioList>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResPortFolioList>, response: Response<ResPortFolioList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 불러오기 성공", "\n${it.toString()}")
                            viewBinding.name.text = it.result.Complete.co_name + " 님"
                            viewBinding.email.text = it.result.Complete.co_email
                            userinfo = Userinfo(it.result.Complete.co_nickName,it.result.Complete.profileImg,it.result.Complete.co_email,it.result.Complete.co_name,it.result.Complete.co_gender,it.result.Complete.co_birth,it.result.Complete.co_loginType)
                            Glide.with(context)
                                .load(it.result.Complete.profileImg).circleCrop()
                                .into(viewBinding.profile)
                            setAdapter(it.result.Portfolio)
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