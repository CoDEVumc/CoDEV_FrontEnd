package com.example.codev

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityMySettingBinding
import com.example.codev.userinfo.UserInfoActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MySettingActivity:AppCompatActivity() {
    lateinit var viewBinding: ActivityMySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "설정"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }
        val userinfo = intent.getSerializableExtra("userinfo") as Userinfo

        loginTypeBindingToLogOut(userinfo.co_loginType)

        //회원 정보 수정 연결(김유정)
        viewBinding.btnEditUser.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
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

    private fun loginTypeBindingToLogOut(loginType:String){
        if (loginType == "GOOGLE"){
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            viewBinding.btnLogout.setOnClickListener {
                mGoogleSignInClient.signOut()
                UserSharedPreferences.clearUser(this)
                val intent = Intent(this,MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }else if(loginType == "GITHUB"){
            viewBinding.btnLogout.setOnClickListener {
                UserSharedPreferences.clearUser(this)
                val intent = Intent(this,MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }else{
            viewBinding.btnLogout.setOnClickListener {
                UserSharedPreferences.clearUser(this)
                val intent = Intent(this,MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }
    }
}