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


class HomeFragment:Fragment() {
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
        var tempBannerList = ArrayList<HomeBannerItem>()
        tempBannerList.add(
            HomeBannerItem("https://w.namu.la/s/dc388bfca00f6fe727bd88f7489b11db7e431f56ff50c26bd692e7967f18563f8f726a524ef29bddba60f8828d2716063d6e3d8037608093bacab39ac69f6d425c02c191f036bdee450c791ece868a81c34926e8ad81c021cd4a0453dbbfddda29e77d03c8b242e55d45911da78433d8", "nayeon"))
        tempBannerList.add(HomeBannerItem("https://w.namu.la/s/7f9203a6e7b7fb0f8df063e907fc54eaaf00b9cfeac023386da4f791c036859f07bd6eabbcb4e117a460744aae636effeacec15787a2209f2264f32d2fc92efd3a5c400d493236e123a75826ed3355de2b613f0fefc26e9437071855b49b976138efd60a63529822b85fde70a53bea7a", "Dahyun"))
        setBannerAdapter(tempBannerList)

        setProjectAdapter()
        setStudyAdapter()

        var tempPostList = ArrayList<HomeCommunityItem>()
        tempPostList.add(HomeCommunityItem("현직자가 알려주는 면접 단골 질문 \uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB", "https://w.namu.la/s/dc388bfca00f6fe727bd88f7489b11db7e431f56ff50c26bd692e7967f18563f8f726a524ef29bddba60f8828d2716063d6e3d8037608093bacab39ac69f6d425c02c191f036bdee450c791ece868a81c34926e8ad81c021cd4a0453dbbfddda29e77d03c8b242e55d45911da78433d8", "02/04 20:45", 225, 3, 56))
        tempPostList.add(HomeCommunityItem("\uD83D\uDD0E 개발자를 위한 정보 검색 팁! ", null, "02/04 21:51", 100, 3, 45))
        tempPostList.add(HomeCommunityItem("\uD83D\uDD25 UI/UX 디자이너 희망자라면 꼭 봐야 할 꿀팁!", "https://w.namu.la/s/7f9203a6e7b7fb0f8df063e907fc54eaaf00b9cfeac023386da4f791c036859f07bd6eabbcb4e117a460744aae636effeacec15787a2209f2264f32d2fc92efd3a5c400d493236e123a75826ed3355de2b613f0fefc26e9437071855b49b976138efd60a63529822b85fde70a53bea7a", "02/04 20:01", 103, 7, 56))
        tempPostList.add(HomeCommunityItem("2023 블록체인 시리즈 세미나 \uD83C\uDF10", null, "02/04 20:00", 223, 2, 40))
        setPostView(tempPostList)
        setQnaView(tempPostList)

        var tempGameList = ArrayList<HomeGameItem>()
        tempGameList.add(
            HomeGameItem(
                "https://w.namu.la/s/dc388bfca00f6fe727bd88f7489b11db7e431f56ff50c26bd692e7967f18563f8f726a524ef29bddba60f8828d2716063d6e3d8037608093bacab39ac69f6d425c02c191f036bdee450c791ece868a81c34926e8ad81c021cd4a0453dbbfddda29e77d03c8b242e55d45911da78433d8", "2023 야간관광 특화도시 숏폼 공모전", "한국관광공사")
        )
        tempGameList.add(
            HomeGameItem(
                "https://w.namu.la/s/7f9203a6e7b7fb0f8df063e907fc54eaaf00b9cfeac023386da4f791c036859f07bd6eabbcb4e117a460744aae636effeacec15787a2209f2264f32d2fc92efd3a5c400d493236e123a75826ed3355de2b613f0fefc26e9437071855b49b976138efd60a63529822b85fde70a53bea7a", "2023 미래도서관 정책 아이디어 해커톤 대회", "국가도서관위원회")
        )
        tempGameList.add(
            HomeGameItem(
                "https://w.namu.la/s/dc388bfca00f6fe727bd88f7489b11db7e431f56ff50c26bd692e7967f18563f8f726a524ef29bddba60f8828d2716063d6e3d8037608093bacab39ac69f6d425c02c191f036bdee450c791ece868a81c34926e8ad81c021cd4a0453dbbfddda29e77d03c8b242e55d45911da78433d8", "2023 제 1 회 CCC 커튼콜 마케팅 공모전", "라이브콘테스트")
        )
        tempGameList.add(
            HomeGameItem(
                "https://w.namu.la/s/7f9203a6e7b7fb0f8df063e907fc54eaaf00b9cfeac023386da4f791c036859f07bd6eabbcb4e117a460744aae636effeacec15787a2209f2264f32d2fc92efd3a5c400d493236e123a75826ed3355de2b613f0fefc26e9437071855b49b976138efd60a63529822b85fde70a53bea7a", "2023 UMC 데모데이", "MakeUs")
        )
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
            0, "", "", "", "", "populaRity").enqueue(object: Callback<ResGetProjectList> {
                override fun onResponse(call: Call<ResGetProjectList>, response: Response<ResGetProjectList>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 조회실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: 플젝 조회 성공! ", "\n${it.toString()}")
                                    Log.d("test: 플젝 데이터 : ", "\n${it.result.success}")
                                    //페이지가 비어있으면
                                    if(it.result.success.toString() == "[]") {
                                        //Log.d("test: success: ", "[] 라서 비어있어용")
                                        Toast.makeText(context,"이 글의 끝입니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    //페이지에 내용이 있으면
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
                    Log.d("test: 조회실패 - RPF > loadData_p(플젝 전체조회): ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setStudyAdapter(){
        RetrofitClient.service.requestSDataList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
            0, "", "", "", "", "populaRity").enqueue(object: Callback<ResGetStudyList> {
            override fun onResponse(call: Call<ResGetStudyList>, response: Response<ResGetStudyList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 스터디 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 스터디 데이터 :", "\n${it.result.success}")
                                //페이지가 비어있으면
                                if(it.result.success.toString() == "[]") {
                                    //Log.d("test: success: ", "[] 라서 비어있어용")
                                    Toast.makeText(context,"이 글의 끝입니다.",Toast.LENGTH_SHORT).show()
                                }
                                //페이지에 내용이 있으면
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
                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
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