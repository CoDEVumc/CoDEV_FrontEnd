package com.codev.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codev.android.databinding.ActivityMyWriteBinding

class MyWriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyWriteBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMyWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val now = intent.getIntExtra("now", -1)
        val bundle = Bundle()
        bundle.putInt("now", now)
        val fragment = MyWriteFragment()
        fragment.arguments = bundle

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "내가 작성한 글"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.container.id, fragment)
            .commitAllowingStateLoss()

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