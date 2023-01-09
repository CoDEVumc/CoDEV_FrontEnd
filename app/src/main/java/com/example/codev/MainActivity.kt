package com.example.codev

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarLogin.toolbar2.title = ""
        viewBinding.toolbarLogin.toolbarText.text = "툴바의 가운데 정렬 글 작성 예시"

        setSupportActionBar(viewBinding.toolbarLogin.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.btnLogin.setOnClickListener {
            val intent = Intent(this,MainAppActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //모집글 상세페이지의 삭제하기 수정하기 예시
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu, this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기",Toast.LENGTH_SHORT).show()
                //finish()
            }
            R.id.menu_modify ->{
                Toast.makeText(this, "수정하기",Toast.LENGTH_SHORT).show()
            }
            R.id.menu_delete ->{
                Toast.makeText(this, "삭제하기",Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
