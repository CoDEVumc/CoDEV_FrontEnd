package com.example.codev

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityMyPortfolioBinding

class MyPortfolioActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyPortfolioBinding

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

        val temp: ResPortFolio = ResPortFolio("temp","2021.10.11")
        val templist = listOf<ResPortFolio>(temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp,temp)
        val adapter = PortfolioAdapter2(this,templist)
        viewBinding.recyclePortfolio.adapter = adapter
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