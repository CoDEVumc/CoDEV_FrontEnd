package com.example.codev.addpage

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.codev.*
import com.example.codev.databinding.ActivityAppliedDetailBinding
import com.example.codev.databinding.InputLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppliedDetailActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAppliedDetailBinding
    private var isLoaded = false
    private var partName = ""
    private var isSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAppliedDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = "지원상세 보기"
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.addLinkSection.removeAllViews()
        viewBinding.stackChipGroup.removeAllViews()

        //테스트 코드
//        val intent = Intent(this@MainAppActivity, AppliedDetailActivity::class.java)
//        intent.putExtra("coProjectId", 65)
//        intent.putExtra("coPortfolioId", 68)
//        intent.putExtra("coPart", "abc")
//        intent.putExtra("coTemporaryStorage", false)
//        startActivity(intent)
        //테스트 코드

        val coProjectId = intent.getIntExtra("coProjectId", -1) // intent.putExtra("coProjectId", 값)
        val coPortfolioId = intent.getIntExtra("coPortfolioId", -1) // intent.putExtra("coPortfolioId", 값)
        partName = intent.getStringExtra("coPart").toString() // intent.putExtra("coPart", 값)
        isSelected = intent.getBooleanExtra("coTemporaryStorage", false) // intent.putExtra("coTemporaryStorage", 값)

        if(coProjectId < 0 || coPortfolioId < 0 || partName == ""){
            Toast.makeText(this, "조회 실패: 다시 시도해주세요(초기값 오류)", Toast.LENGTH_SHORT).show()
        }else{
            loadData(coProjectId, coPortfolioId)
        }

        viewBinding.btnLeft.setOnClickListener {
            if(isLoaded){
                Toast.makeText(this, "문의하기", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "로딩중입니다. 잠시만 기다려 주세요", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.btnRight.setOnClickListener {
            if(isLoaded){

                if(isSelected){
                    Toast.makeText(this, "선택 취소하기", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "선택하기", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "로딩중입니다. 잠시만 기다려 주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData(projectId: Int, portfolioId: Int){
        RetrofitClient.service.getAppliedDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this)), projectId, portfolioId).enqueue(object:
            Callback<ResAppliedUserDetail> {
            override fun onResponse(
                call: Call<ResAppliedUserDetail>,
                response: Response<ResAppliedUserDetail>
            ) {
                if (response.isSuccessful.not()) {
                    Log.d("test: 포트폴리오 불러오기 실패", response.toString())
                    Toast.makeText(
                        this@AppliedDetailActivity,
                        "서버와 연결을 시도했으나 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 불러오기 성공", response.toString())
                            setDataOnPage(this@AppliedDetailActivity, it.result.message)
                            isLoaded = true
                        }
                    }

                }

            }

            override fun onFailure(call: Call<ResAppliedUserDetail>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDataOnPage(context: Context, pfData: AllAppliedUserDetailMessage){
        //setImage
        Glide.with(context).load(pfData.profileImg).error(R.drawable.my_profile).circleCrop().into(viewBinding.userImage)

        viewBinding.userName.setText(pfData.co_name) //name

        //gender
        var genderText = ""
        genderText = if (pfData.co_gender == "MALE") "남성"
        else "여성"
        viewBinding.userGender.text = genderText
        viewBinding.userBirth.text = pfData.co_birth.replace("-", "/")
        viewBinding.applyPartText.text = partName
        viewBinding.editApplyContent.text = pfData.co_motivation

        viewBinding.pfTitle.text = pfData.co_title
        viewBinding.editPfLevel.text = pfData.co_rank
        if(!pfData.co_languages.isNullOrBlank()){
            val getStackNameList = pfData.co_languages.split(",")
            for (i in getStackNameList) viewBinding.stackChipGroup.addView(
                AddPageFunction().returnStackChipWithPF(
                    context,
                    i
                )
            )
        }

        viewBinding.editPfIntro.text = pfData.co_headLine
        viewBinding.editPfContent.text = pfData.co_introduction
        viewBinding.textCounter.text = pfData.co_introduction.length.toString()

        //setLink
        if(!pfData.co_links.isNullOrBlank()){
            val linkList = pfData.co_links.split(",")
            Log.d("linktest", "setData: $linkList")
            for (i in linkList) {
                val linkView = InputLayoutBinding.inflate(layoutInflater)
                linkView.inputOfTitle.setText(i)
                linkView.inputOfTitle.isFocusable = false
                linkView.inputOfTitle.isClickable = false
                viewBinding.addLinkSection.addView(linkView.root)
                Log.d("nowLink", "setData: $i")
            }
        }

        if(isSelected){
            viewBinding.btnRight.text = "선택 취소하기"
            viewBinding.btnRight.background = getDrawable(R.drawable.recruit_detail_btn2_selected)
        }
    }
}