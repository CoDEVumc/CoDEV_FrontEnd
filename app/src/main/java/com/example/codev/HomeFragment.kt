package com.example.codev

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.example.codev.addpage.AddPageFunction
import com.example.codev.databinding.FragmentHomeBinding
import com.example.codev.databinding.HomeHotCommunityBinding
import com.example.codev.homepage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment():Fragment() {
    private lateinit var viewBinding: FragmentHomeBinding
    private lateinit var mainAppActivity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        loadData(mainAppActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentHomeBinding.inflate(layoutInflater)
        viewBinding.toolbarHome.toolbar1.inflateMenu(R.menu.menu_toolbar_1)
        viewBinding.toolbarHome.toolbar1.title = ""
        viewBinding.toolbarHome.toolbarImg.setImageResource(R.drawable.logo_home)

        return viewBinding.root
    }


    private fun loadData(context: Context){
        val tempBannerList = ArrayList<HomeBannerItem>()
        tempBannerList.add(HomeBannerItem("http://semtle.catholic.ac.kr:8080/image?name=banner120230207154253.png", "banner1"))
        tempBannerList.add(HomeBannerItem("http://semtle.catholic.ac.kr:8080/image?name=banner220230207154253.png", "banner2"))
        tempBannerList.add(HomeBannerItem("http://semtle.catholic.ac.kr:8080/image?name=banner320230207154253.png", "banner3"))
        tempBannerList.add(HomeBannerItem("http://semtle.catholic.ac.kr:8080/image?name=banner420230207154253.png", "banner4"))
        tempBannerList.add(HomeBannerItem("http://semtle.catholic.ac.kr:8080/image?name=banner520230207154253.png", "banner5"))
        setBannerAdapter(tempBannerList)

        setProjectAdapter()
        setStudyAdapter()

        val tempPostList = ArrayList<HomeCommunityItem>()
        tempPostList.add(HomeCommunityItem("???????????? ???????????? ?????? ?????? ?????? \uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB", "http://semtle.catholic.ac.kr:8080/image?name=post120230207155427.jpg", "02/04 20:45", 225, 3, 56))
        tempPostList.add(HomeCommunityItem("\uD83D\uDD0E ???????????? ?????? ?????? ?????? ???! ", null, "02/04 21:51", 100, 3, 45))
        tempPostList.add(HomeCommunityItem("\uD83D\uDD25 UI/UX ???????????? ??????????????? ??? ?????? ??? ??????!", "http://semtle.catholic.ac.kr:8080/image?name=post220230207155427.jpg", "02/04 20:01", 103, 7, 56))
        tempPostList.add(HomeCommunityItem("2023 ???????????? ????????? ????????? \uD83C\uDF10", null, "02/04 20:00", 223, 2, 40))
        setPostView(tempPostList)

        val tempQnaList = ArrayList<HomeCommunityItem>()
        tempQnaList.add(HomeCommunityItem("?????? ?????? ?????? ?????? ????????? ?????????????", null, "02/04 21:51", 2, 11, 5))
        tempQnaList.add(HomeCommunityItem("??? ?????? ????????? ????????????????", "http://semtle.catholic.ac.kr:8080/image?name=post320230207155427.jpg", "02/04 20:45", 1, 10, 5))
        tempQnaList.add(HomeCommunityItem("????????? ?????? ??????????????? ????????? ??????????", "http://semtle.catholic.ac.kr:8080/image?name=post420230207155427.jpg", "02/04 20:01", 5, 16, 7))
        tempQnaList.add(HomeCommunityItem("?????? ??? ?????? ?????? ????????????????", null, "02/04 20:00", 7, 7, 16))
        setQnaView(tempQnaList)

        val tempGameList = ArrayList<HomeGameItem>()
        tempGameList.add( HomeGameItem("http://semtle.catholic.ac.kr:8080/image?name=120230207155427.jpg", "2023 ???????????? ???????????? ?????? ?????????", "??????????????????"))
        tempGameList.add( HomeGameItem("http://semtle.catholic.ac.kr:8080/image?name=220230207155427.png", "2023 ??????????????? ?????? ???????????? ????????? ??????", "????????????????????????"))
        tempGameList.add( HomeGameItem("http://semtle.catholic.ac.kr:8080/image?name=320230207155427.jpg", "2023 ??? 1 ??? CCC ????????? ????????? ?????????", "?????????????????????"))
        tempGameList.add( HomeGameItem("http://semtle.catholic.ac.kr:8080/image?name=420230207155427.jpg", "2023 UMC ????????????", "MakeUs"))
        setGameAdapter(tempGameList)
    }
    private fun setBannerAdapter(imgList: ArrayList<HomeBannerItem>){
        if(!imgList.isNullOrEmpty()){
            viewBinding.vpImage.adapter = AdapterHomeBanner(mainAppActivity, imgList)
            viewBinding.indicator.attachTo(viewBinding.vpImage)
        }
    }

    private fun setPostView(itemList: ArrayList<HomeCommunityItem>){
        viewBinding.hotPostList.removeAllViews()
        for(post in itemList){
            var postBinding = HomeHotCommunityBinding.inflate(layoutInflater)
            postBinding.title.text = post.title
            if(!post.imgUrl.isNullOrBlank()){
                postBinding.photo.visibility = View.VISIBLE
                postBinding.photo.clipToOutline = true
                Glide.with(mainAppActivity).load(post.imgUrl).into(postBinding.photo)
            }
            postBinding.time.text = post.time
            postBinding.likeNumber.text = post.likeNumber.toString()
            postBinding.commentNumber.text = post.commentNumber.toString()
            postBinding.bookNumber.text = post.bookedNumber.toString()
            viewBinding.hotPostList.addView(postBinding.root)
        }
    }

    private fun setQnaView(itemList: ArrayList<HomeCommunityItem>){
        viewBinding.hotQnaList.removeAllViews()
        for(post in itemList){
            var postBinding = HomeHotCommunityBinding.inflate(layoutInflater)
            postBinding.qText.visibility = View.VISIBLE
            postBinding.title.text = post.title
            if(!post.imgUrl.isNullOrBlank()){
                postBinding.photo.visibility = View.VISIBLE
                postBinding.photo.clipToOutline = true
                Glide.with(mainAppActivity).load(post.imgUrl).into(postBinding.photo)
            }
            postBinding.time.text = post.time
            postBinding.likeNumber.text = post.likeNumber.toString()
            postBinding.commentNumber.text = post.commentNumber.toString()
            postBinding.bookNumber.text = post.bookedNumber.toString()
            viewBinding.hotQnaList.addView(postBinding.root)
        }
    }

    private fun setProjectAdapter(){
        RetrofitClient.service.requestPDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            0, "", "", "", "ING", "populaRity").enqueue(object: Callback<ResGetProjectList> {
                override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: ????????????",response.toString())
                        Toast.makeText(context, "????????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: ?????? ?????? ??????! ", "\n${it.toString()}")
                                    Log.d("test: ?????? ????????? : ", "\n${it.result.success}")
                                    //???????????? ???????????????
                                    if(it.result.success.toString() == "[]") {
                                        //Log.d("test: success: ", "[] ?????? ???????????????")
                                        Toast.makeText(context,"??? ?????? ????????????.", Toast.LENGTH_SHORT).show()
                                    }
                                    //???????????? ????????? ?????????
                                    else {
                                        viewBinding.hotProjectList.adapter = AdapterHomeProject( mainAppActivity,it.result.success.take(5) )
                                        val itemListDecoration = HomeListDecoration(AddPageFunction().dpToPx(mainAppActivity, 16f).toInt())
                                        if(viewBinding.hotProjectList.itemDecorationCount != 0){
                                            viewBinding.hotProjectList.removeItemDecorationAt(0)
                                        }
                                        viewBinding.hotProjectList.addItemDecoration(itemListDecoration)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResGetProjectList>, t: Throwable) {
                    Log.d("test: ???????????? - RPF > loadData_p(?????? ????????????): ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "????????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setStudyAdapter(){
        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            0, "", "", "", "ING", "populaRity").enqueue(object: Callback<ResGetStudyList> {
            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: ????????????",response.toString())
                    Toast.makeText(context, "????????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: ????????? ?????? ??????! ", "\n${it.toString()}")
                                Log.d("test: ????????? ????????? :", "\n${it.result.success}")
                                //???????????? ???????????????
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] ?????? ???????????????")
                                    Toast.makeText(context,"??? ?????? ????????????.",Toast.LENGTH_SHORT).show()
                                }
                                //???????????? ????????? ?????????
                                else {
                                    viewBinding.hotStudyList.adapter = AdapterHomeStudy(mainAppActivity, it.result.success.take(5))
                                    val itemListDecoration = HomeListDecoration(AddPageFunction().dpToPx(mainAppActivity, 16f).toInt())
                                    if(viewBinding.hotStudyList.itemDecorationCount != 0){
                                        viewBinding.hotStudyList.removeItemDecorationAt(0)
                                    }
                                    viewBinding.hotStudyList.addItemDecoration(itemListDecoration)
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetStudyList>, t: Throwable) {
                Log.d("test: ???????????? - RPF > loadData_s(????????? ????????????)", "[Fail]${t.toString()}")
                Toast.makeText(context, "????????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setGameAdapter(tempList: ArrayList<HomeGameItem>){
        viewBinding.hotGameList.adapter = AdapterHomeGame(mainAppActivity, tempList)
        val itemListDecoration = HomeListDecoration(AddPageFunction().dpToPx(mainAppActivity, 16f).toInt())
        if(viewBinding.hotGameList.itemDecorationCount != 0){
            viewBinding.hotGameList.removeItemDecorationAt(0)
        }
        viewBinding.hotGameList.addItemDecoration(itemListDecoration)
    }


}