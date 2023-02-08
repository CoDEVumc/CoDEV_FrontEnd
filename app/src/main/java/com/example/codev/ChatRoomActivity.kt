package com.example.codev

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityChatRoomBinding

class ChatRoomActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityChatRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarChat.toolbar4.title = ""
        viewBinding.toolbarChat.toolbarText1.text = "채팅방 이름"
        viewBinding.toolbarChat.toolbarText2.text = "채팅방 인원"

        setSupportActionBar(viewBinding.toolbarChat.toolbar4)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
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