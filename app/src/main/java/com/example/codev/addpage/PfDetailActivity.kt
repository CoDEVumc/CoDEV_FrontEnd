package com.example.codev.addpage

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
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
    private var isLoaded = false
    private var loadedObject: RealDataPF? = null
    private var stackMap = LinkedHashMap<Int, String>()

    private var isLoadEd = false

    override fun onResume() {
        super.onResume()
        viewBinding.addLinkSection.removeAllViews()
        viewBinding.stackChipGroup.removeAllViews()
        pfId = intent.getStringExtra("id").toString()
        loadDataUsingPFId(pfId)
    }

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_detail, menu)
        return true
    }

    private fun loadDataUsingPFId(id: String) {
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this))
        RetrofitClient.service.getPortFolioDetail(userToken, id)
            .enqueue(object : Callback<ResGetPFDetail> {
                override fun onResponse(
                    call: Call<ResGetPFDetail>,
                    response: Response<ResGetPFDetail>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d("test: 포트폴리오 불러오기 실패", response.toString())
                        Toast.makeText(
                            this@PfDetailActivity,
                            "서버와 연결을 시도했으나 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 포트폴리오 불러오기 성공", "\n${it.toString()}")
                                loadedObject = it.result.Complete
                                setData(this@PfDetailActivity, it.result.Complete)
                                isLoaded = true
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
        when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.menu_modify -> {
                if(isLoaded){
                    val intent = Intent(this,AddPfPageActivity::class.java)
//                        val stackMap = makeLinkedHashMap()
                    intent.putExtra("pf", EditPF(loadedObject!!.co_portfolioId.toString(), loadedObject!!.co_title, loadedObject!!.co_name, loadedObject!!.co_birth, loadedObject!!.co_gender, loadedObject!!.co_rank, stackMap,  loadedObject!!.co_headLine, loadedObject!!.co_introduction, loadedObject!!.co_links))
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "잠시 후 시도해보세요", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.menu_delete -> {
                //dialog를 띄우고 삭제하기
                if(isLoaded){
                    confirmDelete(this, pfId.toInt()) { finish() }
                }else{
                    Toast.makeText(this, "잠시 후 시도해보세요", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setData(context: Context, pfData: RealDataPF) {

        //setImage
        Glide.with(context).load(pfData.profileImg).error(R.drawable.my_profile).circleCrop().into(viewBinding.userImage)

        viewBinding.userName.setText(pfData.co_name) //name

        //gender
        var genderText = ""
        genderText = if (pfData.co_gender == "MALE") "남성"
        else "여성"
        viewBinding.userGender.text = genderText

        viewBinding.userBirth.text = pfData.co_birth.replace("-", "/")

        viewBinding.pfTitle.text = pfData.co_title

        viewBinding.editPfLevel.text = pfData.co_rank

        //setStack
        if (!pfData.co_languages.isNullOrBlank()){
            val stackNameList = pfData.co_languages.split(",")
            for (i in stackNameList) viewBinding.stackChipGroup.addView(
                addPageFunction.returnStackChipWithPF(
                    context,
                    i
                )
            )
        }

        val stackNameList = ArrayList<String>()
        for( i in pfData.co_languageList){
            val nowName = i.co_language
            val nowId = i.co_languageId
            stackNameList.add(nowName)
            stackMap[nowId] = nowName
        }

        for (i in stackNameList) viewBinding.stackChipGroup.addView(
            addPageFunction.returnStackChipWithPF(
                context,
                i
            )
        )

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
    }
}

private fun confirmDelete(context: Context, pfId: Int, finishPage: () -> Unit){
    // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
    val builder = AlertDialog.Builder(context)
    builder.setTitle("포트폴리오 삭제.")
        .setMessage("포트폴리오를 정말로 삭제하시겠습니까?")
        .setPositiveButton("확인",
            DialogInterface.OnClickListener { dialog, id ->
                RetrofitClient.service.deletePortFolio(pfId,AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object: Callback<ResDeletePortfolio>{
                    override fun onResponse(call: Call<ResDeletePortfolio>, response: Response<ResDeletePortfolio>) {
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
                    override fun onFailure(call: Call<ResDeletePortfolio>, t: Throwable) {
                        Log.d("test", "[Fail]${t.toString()}")
                    }
                })
            })
        .setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id ->
                Toast.makeText(context, "취소함", Toast.LENGTH_SHORT).show()
            })
    // 다이얼로그를 띄워주기
    builder.show()
}