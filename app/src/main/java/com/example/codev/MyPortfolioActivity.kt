package com.example.codev

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.codev.databinding.ActivityMyPortfolioBinding
import java.util.*

class MyPortfolioActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyPortfolioBinding

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

        val temp = ResPortFolio("temp","2021.10.11")
        val templist = listOf<ResPortFolio>(temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp)
        val adapter = PortfolioAdapter2(templist){
            Log.d("test","리콜받음")
            if (it == 1){
                enableDelete(true)
            }else if (it == 0){
                enableDelete(false)
            }
        }
        viewBinding.recyclePortfolio.adapter = adapter
        viewBinding.btnDelete.isGone = true

        viewBinding.btnEdit.setOnClickListener {
            viewBinding.btnEditChk.isChecked = !viewBinding.btnEditChk.isChecked
            viewBinding.btnDelete.isGone = !viewBinding.btnEditChk.isChecked
            enableDelete(false)
            adapter.setEdit(viewBinding.btnEditChk.isChecked)
            adapter.setDeleteCount(0)
            adapter.resetIsChecked()
            adapter.notifyDataSetChanged()
        }

        viewBinding.btnEditChk.setOnClickListener {
            viewBinding.btnDelete.isGone = !viewBinding.btnEditChk.isChecked
            enableDelete(false)
            adapter.setEdit(viewBinding.btnEditChk.isChecked)
            adapter.setDeleteCount(0)
            adapter.resetIsChecked()
            adapter.notifyDataSetChanged()
        }

        //isChecked 된 항목들 삭제기능 필요
        viewBinding.btnDelete.setOnClickListener {
            Log.d("test", "삭제")
            Log.d("test",adapter.getIsChecked().toString())
        }

        //isChecked 된 항목들 삭제기능 필요
        viewBinding.btnDeleteImg.setOnClickListener {
            Log.d("test", "삭제")
            Log.d("test",adapter.getIsChecked().toString())
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}