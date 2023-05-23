package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.codev.addpage.*
import com.example.codev.databinding.ActivityCommunityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList


class InfoDetailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityCommunityDetailBinding
    private var id: Int = -1

    override fun onResume() {
        super.onResume()
        id = intent.getIntExtra("id",-1)
        if (id != -1) {
            loadInfoDetail(this, id)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarCommunity.toolbar2.title = ""
        viewBinding.toolbarCommunity.toolbarText.text = "정보글"
        setSupportActionBar(viewBinding.toolbarCommunity.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        //좋아요 기능
        viewBinding.smile.setOnClickListener {
            viewBinding.smile.isSelected = !viewBinding.smile.isSelected
        }

        //댓글 기능
        viewBinding.etChat.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    enableSend(true)
                }else{
                    enableSend(false)
                }
            }
        })

        viewBinding.btnSend.setOnClickListener {
            viewBinding.etChat.text.clear()
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

    fun setViewMode(boolean: Boolean){
        if (boolean){
            //뷰어와 작성자 같을 때 : 작성자
            Log.d("test","작성자 모드")
        }else{
            //뷰어와 작성자 다를 때 : 뷰어
            Log.d("test","뷰어 모드")
        }
    }

    private fun enableSend(boolean: Boolean){
        if (viewBinding.btnSend.isEnabled != boolean){
            viewBinding.btnSend.isEnabled = boolean
            viewBinding.btnSend.isSelected = boolean
        }
    }

    private fun loadInfoDetail(context:Context, id: Int){
        RetrofitClient.service.getInfoDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),id).enqueue(object : Callback<ResGetInfoDetail>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResGetInfoDetail>, response: Response<ResGetInfoDetail>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 정보글 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 정보글 데이터 :", "\n${it.result.Complete}")

                                //header
                                viewBinding.title.text = it.result.Complete.co_title
                                viewBinding.writerNickname.text = it.result.Complete.co_nickname
                                viewBinding.writeDate.text = stringToTime(it.result.Complete.updatedAt.toString())
                                Glide.with(context)
                                    .load(it.result.Complete.profileImg).circleCrop()
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                Glide.with(context)
                                                    .load("http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png")
                                                    .circleCrop()
                                                    .into(viewBinding.writerProfileImg)
                                            }
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return false
                                        }

                                    })
                                    .into(viewBinding.writerProfileImg)

                                //footer
                                viewBinding.smileCounter.text = "${it.result.Complete.co_likeCount}명이 공감해요"
                                viewBinding.commentCounter.text = "댓글 ${it.result.Complete.co_commentCount}"

                                viewBinding.smile.isSelected = it.result.Complete.co_like
                                setViewMode(it.result.Complete.co_email == it.result.Complete.co_viewer)

                                //content
                                viewBinding.contentText.text = it.result.Complete.content
                                setPhotoAdapter(it.result.Complete.co_photos)
                                setCommentAdapter(it.result.Complete.co_comment)

                                //bottom
                                Glide.with(context)
                                    .load(it.result.Complete.viewerImg).circleCrop()
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                Glide.with(context)
                                                    .load("http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png")
                                                    .circleCrop()
                                                    .into(viewBinding.viewerProfileImg)
                                            }
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return false
                                        }

                                    })
                                    .into(viewBinding.viewerProfileImg)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetInfoDetail>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setPhotoAdapter(dataList: ArrayList<InfoDetailPhoto>){
        val adapter = AdapterCommunityInfoPhotoList(this,dataList)
        viewBinding.rvImg.adapter = adapter
    }

    private fun setCommentAdapter(dataList: ArrayList<InfoDetailComment>){
        val adapter = AdapterCommunityInfoParentCommentList(this,dataList)
        viewBinding.rvComment.adapter = adapter
    }

    private fun stringToTime(string: String) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        val convertTime = LocalDateTime.parse(string, formatter)
        return convertTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
    }
}