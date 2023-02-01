package com.example.codev.addpage

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.codev.*
import com.example.codev.databinding.ActivityPfDetailBinding
import com.example.codev.databinding.InputLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PfDetailActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPfDetailBinding

    var addPageFunction = AddPageFunction()
    private var pfId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPfDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = getString(R.string.detail_pf_toolbar)
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        //TODO: 점점점 메뉴 만들기

        val pfId = intent.getStringExtra("id").toString()
        loadDataUsingPFId(pfId)

    }

    private fun loadDataUsingPFId(id: String){
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this))
        RetrofitClient.service.getPortFolioDetail(userToken, id).enqueue(object: Callback<ResGetPFDetail>{
            override fun onResponse(
                call: Call<ResGetPFDetail>,
                response: Response<ResGetPFDetail>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 불러오기 실패",response.toString())
                    Toast.makeText(this@PfDetailActivity, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 불러오기 성공", "\n${it.toString()}")
                            setData(this@PfDetailActivity, it.result.Complete)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetPFDetail>, t: Throwable) {
                Log.d("fail", "[loadPFData]${t.toString()}")

            }
        })
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

    fun setData(context: Context, pfData: RealDataPF){

        //setImage
        Glide.with(context).load(pfData.profileImg).error(R.drawable.my_profile).circleCrop().fitCenter().into(viewBinding.userImage)

        viewBinding.userName.setText(pfData.co_name) //name

        //gender
        var genderText = ""
        genderText = if(pfData.co_gender == "MALE") "남성"
        else "여성"
        viewBinding.userGender.text = genderText

        viewBinding.userBirth.text = pfData.co_birth.replace("-", "/")

        viewBinding.pfTitle.text = pfData.co_title

        viewBinding.editPfLevel.text = pfData.co_rank

        //setStack
        val stackNameList = pfData.co_languages.split(",")
        for (i in stackNameList) viewBinding.stackChipGroup.addView(addPageFunction.returnStackChipWithPF(context, i))

        viewBinding.editPfIntro.text = pfData.co_headLine

        viewBinding.editPfContent.text = pfData.co_introduction

        viewBinding.textCounter.text = pfData.co_introduction.length.toString()

        //setLink
        val linkList = pfData.co_links.split(",")
        for(i in linkList){
            val linkView = InputLayoutBinding.inflate(layoutInflater)
            linkView.inputOfTitle.setText(i)
            linkView.inputOfTitle.isFocusable = false
            linkView.inputOfTitle.isClickable = false
            viewBinding.addLinkSection.addView(linkView.root)
        }
    }
}