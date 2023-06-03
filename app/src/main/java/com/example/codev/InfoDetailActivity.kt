package com.example.codev

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.codev.addpage.*
import com.example.codev.databinding.ActivityCommunityDetailBinding
import com.google.gson.JsonObject
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
    private var communityType: String = "None"
    private var postImgUrlList = ArrayList<String>()
    private var parentCommentId: Int = -1

    override fun onResume() {
        super.onResume()
        viewBinding.toolbarCommunity.toolbar2.menu.clear()
        postImgUrlList = ArrayList<String>()
        id = intent.getIntExtra("id",-1) // 게시물 id
        communityType = intent.getStringExtra("type")!!  // 게시물 종류-> [ "info", "qna"] 두 종류

        if (id != -1) {
            if (communityType == "info") {
                viewBinding.toolbarCommunity.toolbarText.text = "정보글"
                loadInfoDetail(this, id)
            }
            else if (communityType == "qna"){
                viewBinding.toolbarCommunity.toolbarText.text = "질문글"
                loadQnaDetail(this, id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarCommunity.toolbar2.title = ""
        setSupportActionBar(viewBinding.toolbarCommunity.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        //좋아요 기능
        viewBinding.smile.setOnClickListener {
            if(communityType == "info")
                likeInfoPost(this)
            else if (communityType == "qna")
                likeQnaPost(this)
        }

        //북마크 기능
        viewBinding.bookIcon.setOnClickListener {
            if(communityType == "info")
                bookInfoPost(this)
            else if (communityType == "qna")
                bookQnaPost(this)
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
            sendParentComment(this)
        }

        viewBinding.etChat.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                parentCommentId = -1
                viewBinding.etChat.hint = ""
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.menu_modify -> {
                Toast.makeText(this, "modify", Toast.LENGTH_SHORT).show()
                if (communityType == "info"){
                    val intent = Intent(this,AddPostActivity::class.java)
                    intent.putExtra("postType", "info")
                    intent.putExtra("isOld", true)
                    intent.putExtra("postId", id)
                    intent.putExtra("postTitle", viewBinding.title.text)
                    intent.putExtra("postContent", viewBinding.contentText.text)
                    intent.putExtra("postImageUrlsString", postImgUrlList.joinToString(separator = ","))
                    startActivity(intent)
                }else if(communityType == "qna"){
                    val intent = Intent(this,AddPostActivity::class.java)
                    intent.putExtra("postType", "qna")
                    intent.putExtra("isOld", true)
                    intent.putExtra("postId", id)
                    intent.putExtra("postTitle", viewBinding.title.text)
                    intent.putExtra("postContent", viewBinding.contentText.text)
                    intent.putExtra("postImageUrlsString", postImgUrlList.joinToString(separator = ","))
                    startActivity(intent)
                }
            }
            R.id.menu_delete -> {
                confirmDelete(this, id) { finish() }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun setViewMode(boolean: Boolean){
        if (boolean){
            //뷰어와 작성자 같을 때 : 작성자
            Log.d("test","작성자 모드")
            setWriterMode()
        }else{
            //뷰어와 작성자 다를 때 : 뷰어
            Log.d("test","뷰어 모드")
        }
    }

    private fun setWriterMode(){
        menuInflater.inflate(R.menu.menu_toolbar_detail, viewBinding.toolbarCommunity.toolbar2.menu)
    }

    private fun enableSend(boolean: Boolean){
        if (viewBinding.btnSend.isEnabled != boolean){
            viewBinding.btnSend.isEnabled = boolean
            viewBinding.btnSend.isSelected = boolean
        }
    }

    private fun loadInfoDetail(context:Context, id: Int){
        RetrofitClient.service.getInfoDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),id).enqueue(object : Callback<ResGetInfoDetail>{
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
                                viewBinding.bookCount.text = it.result.Complete.co_markCount.toString()


                                viewBinding.smile.isSelected = it.result.Complete.co_like
                                viewBinding.bookIcon.isSelected = it.result.Complete.co_mark

                                setViewMode(it.result.Complete.co_email == it.result.Complete.co_viewer)

                                Log.d("bookinfo", "onResponse: ${it.result.Complete.co_markCount.toString()}")
                                Log.d("bookinfo", "onResponse: ${it.result.Complete.co_mark.toString()}")

                                //content
                                viewBinding.contentText.text = it.result.Complete.content
                                setInfoPhotoAdapter(it.result.Complete.co_photos)
                                setInfoCommentAdapter(it.result.Complete.co_comment, it.result.Complete.co_viewer)
                                setInfoPhotoUrlList(it.result.Complete.co_photos)
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
    private fun loadQnaDetail(context: Context, id: Int){
        RetrofitClient.service.getQnaDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),id).enqueue(object : Callback<ResGetQnaDetail>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResGetQnaDetail>, response: Response<ResGetQnaDetail>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 질문글 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 질문글 데이터 :", "\n${it.result.Complete}")

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
                                viewBinding.bookCount.text = it.result.Complete.co_markCount.toString()
                                Log.d("bookqna", "onResponse: ${it.result.Complete.co_markCount.toString()}")

                                viewBinding.smile.isSelected = it.result.Complete.co_like
                                setViewMode(it.result.Complete.co_email == it.result.Complete.co_viewer)
                                viewBinding.bookIcon.isSelected = it.result.Complete.co_mark
                                Log.d("bookqna", "onResponse: ${it.result.Complete.co_mark.toString()}")

                                //content
                                viewBinding.contentText.text = it.result.Complete.content
                                setQnaPhotoAdapter(it.result.Complete.co_photos)
                                setQnaCommentAdapter(it.result.Complete.co_comment)
                                setQnaPhotoUrlList(it.result.Complete.co_photos)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetQnaDetail>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setInfoPhotoAdapter(dataList: ArrayList<InfoDetailPhoto>){
        val adapter = AdapterCommunityInfoPhotoList(this,dataList)
        viewBinding.rvImg.adapter = adapter
    }
    private fun setInfoCommentAdapter(dataList: ArrayList<InfoDetailComment>, viewerEmail: String){
        val adapter = AdapterCommunityInfoParentCommentList(this, viewerEmail, dataList) { id, name ->
        }
        viewBinding.rvComment.adapter = adapter
    }
    private fun setInfoPhotoUrlList(coPhotos: ArrayList<InfoDetailPhoto>) {
        for (i in coPhotos){
            postImgUrlList.add(i.co_fileUrl)
        }
    }

    private fun setQnaPhotoAdapter(dataList: ArrayList<QnaDetailPhoto>){
        val adapter = AdapterCommunityQnaPhotoList(this,dataList)
        viewBinding.rvImg.adapter = adapter
    }
    private fun setQnaCommentAdapter(dataList: ArrayList<QnaDetailComment>){
        val adapter = AdapterCommunityQnaParentCommentList(this,dataList)
        viewBinding.rvComment.adapter = adapter
    }

    private fun setQnaPhotoUrlList(coPhotos: ArrayList<QnaDetailPhoto>) {
        for (i in coPhotos){
            postImgUrlList.add(i.co_fileUrl)
        }
    }

    private fun stringToTime(string: String) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        val convertTime = LocalDateTime.parse(string, formatter)
        return convertTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
    }

    private fun likeInfoPost(context: Context){
        viewBinding.smile.isClickable = false
        RetrofitClient.service.likeInfo(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqLikePost(co_like = viewBinding.smile.isSelected)).enqueue(object: Callback<ResLikePost>{
            override fun onResponse(call: Call<ResLikePost>, response: Response<ResLikePost>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    when (response.code()) {
                        200 -> {
                            viewBinding.smile.isSelected = !viewBinding.smile.isSelected
                            viewBinding.smile.isClickable = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResLikePost>, t: Throwable) {
                viewBinding.smile.isClickable = true
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun likeQnaPost(context: Context){
        viewBinding.smile.isClickable = false
        RetrofitClient.service.likeQna(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqLikePost(co_like = viewBinding.smile.isSelected)).enqueue(object: Callback<ResLikePost>{
            override fun onResponse(call: Call<ResLikePost>, response: Response<ResLikePost>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    when (response.code()) {
                        200 -> {
                            viewBinding.smile.isSelected = !viewBinding.smile.isSelected
                            viewBinding.smile.isClickable = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResLikePost>, t: Throwable) {
                viewBinding.smile.isClickable = true
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bookInfoPost(context: Context){
        viewBinding.bookIcon.isClickable = false
        RetrofitClient.service.markInfo(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<ResLikePost>{
            override fun onResponse(call: Call<ResLikePost>, response: Response<ResLikePost>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    when (response.code()) {
                        200 -> {
                            viewBinding.bookIcon.isSelected = !viewBinding.bookIcon.isSelected
                            viewBinding.bookIcon.isClickable = true
                            if(viewBinding.bookIcon.isSelected){ //요청 후 선택되었다 -> 숫자+1
                                viewBinding.bookCount.text = (viewBinding.bookCount.text.toString().toInt() + 1).toString()
                            }else{
                                viewBinding.bookCount.text = (viewBinding.bookCount.text.toString().toInt() - 1).toString()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResLikePost>, t: Throwable) {
                viewBinding.bookIcon.isClickable = true
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bookQnaPost(context: Context){
        viewBinding.bookIcon.isClickable = false
        RetrofitClient.service.markQna(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<ResLikePost>{
            override fun onResponse(call: Call<ResLikePost>, response: Response<ResLikePost>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    when (response.code()) {
                        200 -> {
                            viewBinding.bookIcon.isSelected = !viewBinding.bookIcon.isSelected
                            viewBinding.bookIcon.isClickable = true
                            if(viewBinding.bookIcon.isSelected){ //요청 후 선택되었다 -> 숫자+1
                                viewBinding.bookCount.text = (viewBinding.bookCount.text.toString().toInt() + 1).toString()
                            }else{
                                viewBinding.bookCount.text = (viewBinding.bookCount.text.toString().toInt() - 1).toString()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResLikePost>, t: Throwable) {
                viewBinding.bookIcon.isClickable = true
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDelete(context: Context, id: Int, finishPage: () -> Unit){
        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
        val builder = AlertDialog.Builder(context)
        val TYPE_KOREAN = if(communityType == "info") "정보글" else "질문글"
        builder.setTitle("$TYPE_KOREAN 삭제")
            .setMessage("해당 글을 정말로 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, _ ->
                    if (communityType == "info"){
                        RetrofitClient.service.deleteInfo(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<ResDeletePost>{
                            override fun onResponse(call: Call<ResDeletePost>, response: Response<ResDeletePost>) {
                                if(response.isSuccessful.not()){
                                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                when(response.code()){
                                    200 -> {
                                        response.body()?.let {
                                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                                            finishPage()
                                        }
                                    }
                                }
                            }
                            override fun onFailure(call: Call<ResDeletePost>, t: Throwable) {
                                Log.d("test", "[Fail]${t.toString()}")
                            }
                        })
                    }else if(communityType == "qna"){
                        RetrofitClient.service.deleteQna(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<ResDeletePost>{
                            override fun onResponse(call: Call<ResDeletePost>, response: Response<ResDeletePost>) {
                                if(response.isSuccessful.not()){
                                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                when(response.code()){
                                    200 -> {
                                        response.body()?.let {
                                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                                            finishPage()
                                        }
                                    }
                                }
                            }
                            override fun onFailure(call: Call<ResDeletePost>, t: Throwable) {
                                Log.d("test", "[Fail]${t.toString()}")
                            }
                        })
                    }
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, _ ->
                    Toast.makeText(context, "취소함", Toast.LENGTH_SHORT).show()
                })
        // 다이얼로그를 띄워주기
        builder.show()
    }

    private fun sendParentComment(context: Context){
        viewBinding.btnSend.isClickable = false
        if(communityType == "info"){
            RetrofitClient.service.createInfoParentComment(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqCreateComment(viewBinding.etChat.text.toString())).enqueue(object: Callback<ResConfirm>{
                override fun onResponse(call: Call<ResConfirm>, response: Response<ResConfirm>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 조회실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else {
                        when (response.code()) {
                            200 -> {
                                viewBinding.etChat.text.clear()
                                viewBinding.btnSend.isClickable = true
                                hideKeyboard(viewBinding.etChat)
                                onResume()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResConfirm>, t: Throwable) {
                    viewBinding.btnSend.isClickable = true
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else if(communityType == "qna"){
            RetrofitClient.service.createQnaParentComment(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqCreateComment(viewBinding.etChat.text.toString())).enqueue(object: Callback<ResConfirm>{
                override fun onResponse(call: Call<ResConfirm>, response: Response<ResConfirm>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 조회실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else {
                        when (response.code()) {
                            200 -> {
                                viewBinding.etChat.text.clear()
                                viewBinding.btnSend.isClickable = true
                                onResume()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResConfirm>, t: Throwable) {
                    viewBinding.btnSend.isClickable = true
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun Context.showKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }


}