package com.codev.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.codev.android.databinding.ActivityMyPortfolioBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyPortfolioActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyPortfolioBinding
    private lateinit var adapter: AdapterPortfolio2
    private lateinit var userinfo: Userinfo

    override fun onResume() {
        super.onResume()
        loadData(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMyPortfolioBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "포트폴리오 관리"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        userinfo = intent.getSerializableExtra("userinfo") as Userinfo

        viewBinding.btnDelete.isGone = true

        viewBinding.btnEdit.setOnClickListener {
            viewBinding.btnEditChk.isChecked = !viewBinding.btnEditChk.isChecked
            viewBinding.btnDelete.isGone = !viewBinding.btnEditChk.isChecked
            adapter.setEdit(viewBinding.btnEditChk.isChecked)
            adapter.setDeleteCount(0)
            adapter.resetIsChecked()
            adapter.resetDeleteList()
            adapter.notifyDataSetChanged()
            enableDelete(false)
        }

        viewBinding.btnEditChk.setOnClickListener {
            viewBinding.btnDelete.isGone = !viewBinding.btnEditChk.isChecked
            adapter.setEdit(viewBinding.btnEditChk.isChecked)
            adapter.setDeleteCount(0)
            adapter.resetIsChecked()
            adapter.resetDeleteList()
            adapter.notifyDataSetChanged()
            enableDelete(false)
        }

        //isChecked 된 항목들 삭제기능 필요
        viewBinding.btnDelete.setOnClickListener {
            Log.d("test", "삭제")
            Log.d("test",adapter.getIsChecked().toString())
            Log.d("test",adapter.getDeleteList().size.toString())
            for (i:Int in 0 until adapter.getDeleteList().size){
                Log.d("test: 삭제 목록",adapter.getDeleteList()[i].toString())
                deletePortfolio(this,adapter.getDeleteList()[i])
            }
            viewBinding.btnEditChk.isChecked = !viewBinding.btnEditChk.isChecked
            viewBinding.btnDelete.isGone = !viewBinding.btnEditChk.isChecked
        }

        //isChecked 된 항목들 삭제기능 필요
        viewBinding.btnDeleteImg.setOnClickListener {
            Log.d("test", "삭제")
            Log.d("test",adapter.getIsChecked().toString())
            Log.d("test",adapter.getDeleteList().size.toString())
            for (i:Int in 0 until adapter.getDeleteList().size){
                Log.d("test: 삭제 목록",adapter.getDeleteList()[i].toString())
                deletePortfolio(this,adapter.getDeleteList()[i])
            }
            viewBinding.btnEditChk.isChecked = !viewBinding.btnEditChk.isChecked
            viewBinding.btnDelete.isGone = !viewBinding.btnEditChk.isChecked
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableDelete(boolean: Boolean){
        if (boolean != viewBinding.btnDelete.isEnabled){
            viewBinding.btnDelete.isEnabled = boolean
            viewBinding.btnDeleteImg.isEnabled = boolean
            viewBinding.btnDeleteImg.isSelected = boolean
            if(boolean){
                viewBinding.btnDeleteText.setTextColor(getColor(R.color.black_700))
            }else{
                viewBinding.btnDeleteText.setTextColor(getColor(R.color.black_300))
            }
        }
    }

    private fun setAdapter(dataList: ArrayList<PortFolio>){
        adapter = AdapterPortfolio2(dataList, userinfo.co_name, userinfo.co_birth, userinfo.co_gender){
            Log.d("test","리콜받음")
            if (it == 1){
                enableDelete(true)
            }else if (it == 0){
                enableDelete(false)
            }
        }
        viewBinding.recyclePortfolio.adapter = adapter
    }

    private fun loadData(context: Context){
        RetrofitClient.service.getPortFolio(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())).enqueue(object: Callback<ResPortFolioList> {
            override fun onResponse(call: Call<ResPortFolioList>, response: Response<ResPortFolioList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 불러오기 성공", "\n${it.toString()}")
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

    private fun deletePortfolio(context: Context,coPortfolioId: Int){
        RetrofitClient.service.deletePortFolio(coPortfolioId,AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())).enqueue(object: Callback<ResDeletePortfolio>{
            override fun onResponse(call: Call<ResDeletePortfolio>, response: Response<ResDeletePortfolio>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                            loadData(context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResDeletePortfolio>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }
}