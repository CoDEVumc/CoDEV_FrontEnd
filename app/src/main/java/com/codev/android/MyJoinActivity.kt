package com.codev.android

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codev.android.databinding.ActivityMyJoinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyJoinActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyJoinBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMyJoinBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "참여 현황"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.container.id, MyJoinFragment())
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








//viewBinding.tabLayout.setOnClickListener{
//    downpage = 0
//    lastPage = false //없으면 페이지 전환시 무한스크롤 작동x
//    // (이유: 스터디 화면에서 페이지 끝까지 닿으면 lastpage = true -> addOnScrollListener의 조건에 의해 더이상 downpage가 증가하지 x)
//    // S화면 끝까지 닿았다가 P화면 전환 시 P화면 무한스크롤 x
//    when(it.id){
//        R.id.bookmark_project -> {
//            //프로젝트 불러와
//            now = 0
//            pdataList = ArrayList()
//            sdataList = ArrayList()
//            when (it.itemId) { //프->스
//                R.id.m_study -> {
//                    now = 1
//                    Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()
//                    viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)
//                    loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
//                    Log.d("test: (1나와야 돼) now : ",now.toString())
//                    true
//                }
//                else -> false
//            }
//
//        }
//        R.id.bookmark_study -> {
//            //스터디 불러와
//            now = 1
//            pdataList = ArrayList()
//            sdataList = ArrayList()
//            when (it.itemId) { //스->프
//                R.id.m_project -> {
//                    now = 0
//                    Toast.makeText(mainAppActivity, "프로젝트", Toast.LENGTH_SHORT).show()
//                    viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)
//                    loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
//                    Log.d("test: (0나와야 돼) now : ",now.toString())
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//    true
//}





//override fun onCreate(savedInstanceState: Bundle?){
//    super.onCreate(savedInstanceState)
//    viewBinding = ActivityMyBookmarkBinding.inflate(layoutInflater)
//    setContentView(viewBinding.root)
//
//    //먼저 프로젝트 화면 탭 되어있다고 지정해줘야 될듯
//    now = 0
//
//    viewBinding.tabLayout.setOnClickListener{
//        downpage = 0
//        lastPage = false //없으면 페이지 전환시 무한스크롤 작동x
//        // (이유: 스터디 화면에서 페이지 끝까지 닿으면 lastpage = true -> addOnScrollListener의 조건에 의해 더이상 downpage가 증가하지 x)
//        // S화면 끝까지 닿았다가 P화면 전환 시 P화면 무한스크롤 x
//        when(it.id){
//            R.id.bookmark_project -> {
//                //프로젝트 불러와
//                now = 0
//                pdataList = ArrayList()
//                sdataList = ArrayList()
//                when (it.itemId) { //프->스
//                    R.id.m_study -> {
//                        now = 1
//                        Toast.makeText(mainAppActivity, "스터디", Toast.LENGTH_SHORT).show()
//                        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_study)
//                        loadSData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
//                        Log.d("test: (1나와야 돼) now : ",now.toString())
//                        true
//                    }
//                    else -> false
//                }
//
//            }
//            R.id.bookmark_study -> {
//                //스터디 불러와
//                now = 1
//                pdataList = ArrayList()
//                sdataList = ArrayList()
//                when (it.itemId) { //스->프
//                    R.id.m_project -> {
//                        now = 0
//                        Toast.makeText(mainAppActivity, "프로젝트", Toast.LENGTH_SHORT).show()
//                        viewBinding.toolbarRecruit.toolbarImg.setImageResource(R.drawable.logo_project)
//                        loadPData(mainAppActivity, downpage, coLocationTag, coPartTag, coKeyword, coProcessTag, coSortingTag)
//                        Log.d("test: (0나와야 돼) now : ",now.toString())
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
//        true
//    }
//
//}