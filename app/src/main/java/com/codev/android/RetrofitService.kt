package com.codev.android

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface RetrofitService {

    @POST("user/sns/login")
    fun googleSignIn(@Body params: ReqGoogleSignIn) : Call<ResSignIn>

    @POST("user/sns/login")
    fun githubSignIn(@Body params: ReqGithubSignIn) : Call<ResSignIn>


    @POST("user/login")
    fun signIn(@Body params: ReqSignIn): Call<ResSignIn>

    @POST("user/token/refresh")
    fun refreshToken(@Body params: ReqRefreshToken): Call<ResRefreshToken>

    @POST("user/join")
    @Multipart
    fun signUp(@Part("user") user: RequestBody
               ,@Part file: MultipartBody.Part): Call<JsonObject>

    @GET("user/code/mail")
    fun getEmailCode(@Query("email") value1: String) : Call<ResGetEmailCode>

    @POST("user/duplication")
    fun checkEmail(@Query("email") value1: String) : Call<ResCheckEmail>

    @POST("project")
    @Multipart
    fun createNewProject(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("project") project: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewProject>

    @PUT("project/update/{id}")
    @Multipart
    fun updateProject(
        @Path("id") id: String
        ,@Header("CoDev_Authorization") authToken: String
        ,@Part("project") project: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewProject>

    @POST("study")
    @Multipart
    fun createNewStudy(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("study") study: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewStudy>

    @PUT("study/update/{coStudyId}")
    @Multipart
    fun updateStudy(
        @Path("coStudyId") id: String
        ,@Header("CoDev_Authorization") authToken: String
        ,@Part("study") study: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewStudy>

    @POST("my-page/portfolio")
    fun createNewPF(
        @Header("CoDev_Authorization") authToken: String
        ,@Body params: ReqCreateNewPF
    ): Call<ResCreateNewPF>

    @PATCH("my-page/portfolio/{id}")
    fun updatePF(
        @Path("id") id: String
        ,@Header("CoDev_Authorization") authToken: String
        ,@Body params: ReqUpdatePF
    ): Call<ResCreateNewPF>

    @POST("infoBoard")
    @Multipart
    fun createNewInfo(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("InfoBoard") InfoBoard: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewPost>

    @POST("qnaBoard")
    @Multipart
    fun createNewQNA(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("qnaBoard") InfoBoard: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResCreateNewPost>

    @PUT("infoBoard/update/{coInfoId}")
    @Multipart
    fun updateInfo(
        @Path("coInfoId") id: String,
        @Header("CoDev_Authorization") authToken: String
        ,@Part("infoBoard") infoBoard: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResUpdatePost>

    @PUT("qnaBoard/update/{coqnaId}")
    @Multipart
    fun updateQNA(
        @Path("coqnaId") id: String,
        @Header("CoDev_Authorization") authToken: String
        ,@Part("qnaBoard") qnaBoard: RequestBody
        ,@Part files: List<MultipartBody.Part?>
    ): Call<ResUpdatePost>

    @GET("qnaBoard/qnaBoards/{page}")
    fun requestQDataList( //커뮤니티 - 질문글 리스트 전체조회
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int,
        @Query("coMyBoard") coMyBoard: Boolean,
        @Query("sortingTag") sortingTag: String
    ): Call<ResGetCommunityList1>

    @GET("infoBoard/infoBoards/{page}")
    fun requestIDataList( //커뮤니티 - 정보글 리스트 전체조회
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int,
        @Query("coMyBoard") coMyBoard: Boolean,
        @Query("sortingTag") sortingTag: String
    ): Call<ResGetCommunityList1>

    @GET("board/search/{page}")
    fun searchCommunityDataList( //커뮤니티 - 정보글 리스트 검색
        @Path("page") page: Int,
        @Query("searchTag") searchTag: String,
        @Query("sortingTag") sortingTag: String,
        @Query("coMyBoard") coMyBoard: Boolean,
        @Query("type") type: String
    ): Call<ResGetSearchCommunity>


    @GET("infoBoard/infoBoards/{page}")
    fun requestCDataList( //커뮤니티 - 공모전글 리스트 전체조회
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int,
        @Query("coMyBoard") coMyBoard: Boolean,
        @Query("sortingTag") sortingTag: String
    ): Call<ResGetCommunityList2>

    @GET("project/projects/{page}")
    fun requestPDataList(
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int, @Query("coLocationTag") coLocationTag: String,
        @Query("coPartTag") coPartTag: String, @Query("coKeyword") coKeyword: String,
        @Query("coProcessTag") coProcessTag: String,
        @Query("coSortingTag") coSortingTag: String
    ): Call<ResGetProjectList>

    @GET("study/studies/{page}")
    fun requestSDataList(
        @Header("CoDev_Authorization") header: String,
        @Path("page") page: Int, @Query("coLocationTag") coLocationTag: String,
        @Query("coPartTag") coPartTag: String, @Query("coKeyword") coKeyword: String,
        @Query("coProcessTag") coProcessTag: String,
        @Query("coSortingTag") coSortingTag: String
    ): Call<ResGetStudyList>

    @PATCH("project/heart/{coProjectId}")
    fun requestProjectBookMark(
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int
    ): Call<JsonObject>

    @PATCH("study/heart/{coStudyId}")
    fun requestStudyBookMark(
        @Header("CoDev_Authorization") header: String,
        @Path("coStudyId") coStudyId: Int
    ): Call<JsonObject>

    @GET("my-page/portfolioList")
    fun getPortFolio(@Header("CoDev_Authorization") header: String) : Call<ResPortFolioList>

    //내정보 > 북마크
    @GET("my-page/hearts/projects")
    fun getHeartedProject(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkProjectList>
    @GET("my-page/hearts/studies")
    fun getHeartedStudy(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkStudyList>
    @GET("qnaBoard/mark/list")
    fun getHeartedQustion(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkQuestionAndInfoList>
    @GET("infoBoard/mark/list")
    fun getHeartedInfo(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkQuestionAndInfoList>
    @GET("my-page/mark/list")
    fun getHeartedQustionAndInfo(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkQuestionAndInfoList>
    @GET("my-page/hearts/studies")
    fun getHeartedContest(@Header("CoDev_Authorization") header: String) : Call<ResBookMarkContestList>

    //내정보 > 내가 작성한 글 여기
    @GET("my-page/myWrite")
    fun getWrittenList(
        @Header("CoDev_Authorization") header: String,
        @Query("type") type: String
    ) : Call<ResGetPSList>

    @GET("my-page/recruitment")
    fun getApplyList(
        @Header("CoDev_Authorization") header: String,
        @Query("type") type: String
    ) : Call<ResApplyList>

    @GET("my-page/participation")
    fun getJoinList(
        @Header("CoDev_Authorization") header: String,
        @Query("type") type: String
    ) : Call<ResJoinList>


    @GET("my-page/portfolio/{coPortfolioId}")
    fun getPortFolioDetail(@Header("CoDev_Authorization") authToken: String, @Path("coPortfolioId") id: String) : Call<ResGetPFDetail>

    @DELETE("my-page/portfolio/{coPortfolioId}")
    fun deletePortFolio(@Path("coPortfolioId") coPortfolioId:Int,@Header("CoDev_Authorization") header: String) : Call<ResDeletePortfolio>

    @GET("project/{coProjectId}")
    fun getProjectDetail(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int) : Call<ResGetRecruitDetail>

    @GET("study/{coStudyId}")
    fun getStudyDetail(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int) : Call<ResGetRecruitDetail>

    @GET("project/recruitment/{coProjectId}")
    fun getApplyerProjectList( // pm] 플젝 지원자 리스트
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int,
        @Query("coPart") coPart: String
    ) : Call<ResApplyerList>

    @GET("study/recruitment/{coStudyId}")
    fun getApplyerStudyList( //pm] 스터디 지원자 리스트
        @Header("CoDev_Authorization") header: String,
        @Path("coStudyId") coStudyId: Int,
        @Query("coPart") coPart: String
    ) : Call<ResApplyerList>

    @PATCH("project/recruitment/pick/{coProjectId}") //프로젝트 지원자 선택 & 선택취소 (임시저장)
    fun requestProjectApplicant(
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int,
        @Body params: ReqUpdateApplicant
    ): Call<JsonObject>

    @PATCH("study/recruitment/pick/{coStudyId}") //스터디 지원자 선택 & 선택취소 (임시저장)
    fun requestStudyApplicant(
        @Header("CoDev_Authorization") header: String,
        @Path("coStudyId") coStudyId: Int,
        @Body params: ReqUpdateApplicant
    ): Call<JsonObject>

    @GET("project/recruitment/portfolio/{coProjectId}/{coPortfolioId}")
    fun getProjectApplicantPorfolioDetail( //프로젝트 지원자 포트폴리오 상세조회
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int,
        @Path("coPortfolioId") coPortfolioId: Int,
        ): Call<ResGetPFDetail2>

    @GET("study/recruitment/portfolio/{coStudyId}/{coPortfolioId}")
    fun getStudyApplicantPorfolioDetail( //스터디 지원자 포트폴리오 상세조회
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int,
        @Path("coPortfolioId") coPortfolioId: Int,
    ): Call<ResGetPFDetail2>


    @DELETE("project/out/{coProjectId}")
    fun deleteProject(@Path("coProjectId") coProjectId:Int,@Header("CoDev_Authorization") header: String) : Call<JsonObject>

    @DELETE("study/{coStudyId}")
    fun deleteStudy(@Path("coStudyId") coStudyId:Int,@Header("CoDev_Authorization") header: String) : Call<JsonObject>

    @POST("project/submission/{coProjectId}")
    fun applyProject(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int, @Body params: ReqApplyProject) : Call<JsonObject>

    @POST("study/submission/{coStudyId}")
    fun applyStudy(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int, @Body params: ReqApplyStudy) : Call<JsonObject>

    @HTTP(method = "DELETE", path="project/recruitment/{coProjectId}", hasBody = true)
    fun cancelProject(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int, @Body params: ReqCancelRecruit) : Call<JsonObject>

    @HTTP(method = "DELETE", path="study/recruitment/{coStudyId}", hasBody = true)
    fun cancelStudy(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int, @Body params: ReqCancelRecruit) : Call<JsonObject>

    @PATCH("project/recruitment/extension/{coProjectId}")
    fun extendProject(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int, @Body params: ReqExtendProject) : Call<ResExtendRecruit>

    @PATCH("study/recruitment/extension/{coStudyId}")
    fun extendStudy(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int, @Body params: ReqExtendStudy) : Call<ResExtendRecruit>

    @PATCH("project/recruitment/dead-line/{coProjectId}")
    fun doneRecruitProject(
        @Header("CoDev_Authorization") header: String,
        @Path("coProjectId") coProjectId: Int,
        @Body params: ReqRecruitedApplicantList
    ): Call<JsonObject>

    @PATCH("project/recruitment/{coStudyId}")
    fun doneRecruitStudy(
        @Header("CoDev_Authorization") header: String,
        @Path("coStudyId") coStudyId: Int,
        @Body params: ReqRecruitedApplicantList
    ): Call<JsonObject>



    @PUT("user/update/profile")
    @Multipart
    fun changeUserInfo(
        @Header("CoDev_Authorization") authToken: String
        ,@Part("user") userData: RequestBody
        ,@Part file: MultipartBody.Part
    ): Call<ResUserInfoChanged>

    @PATCH("user/update/password")
    fun changePassword(@Header("CoDev_Authorization") authToken: String, @Body params: ReqChangeUserPassword): Call<ResChangeUserPassword>

    @GET("project/recruitment/portfolio/{coProjectId}/{coPortfolioId}")
    fun getProjectAppliedDetail(@Header("CoDev_Authorization") header: String, @Path("coProjectId") coProjectId: Int, @Path("coPortfolioId") coPortfolioId: Int) : Call<ResAppliedUserDetail>

    @GET("study/recruitment/portfolio/{coStudyId}/{coPortfolioId}")
    fun getStudyAppliedDetail(@Header("CoDev_Authorization") header: String, @Path("coStudyId") coStudyId: Int, @Path("coPortfolioId") coPortfolioId: Int) : Call<ResAppliedUserDetail>


    @GET("chat/rooms")
    fun getChatRoomList(@Header("CoDev_Authorization") header: String) : Call<ResGetChatRoomList>

    @GET("chat/room/{roomId}")
    fun getChatList(@Header("CoDev_Authorization") header: String, @Path("roomId") roomId: String) : Call<ResGetChatList>

    @POST("chat/create/room")
    fun createChatRoom(@Header("CoDev_Authorization") header: String, @Body params: ReqCreateChatRoom) : Call<JsonObject>

    @POST("chat/invite")
    fun inviteChat(@Header("CoDev_Authorization") header: String, @Body params: ReqInviteChat) : Call<JsonObject>

    @POST("chat/update/room_title")
    fun renameChatRoom(@Header("CoDev_Authorization") header: String, @Body params: ReqRenameChatRoom) : Call<JsonObject>

    @POST("chat/confirm/{roomId}")
    fun confirmChatRoom(@Header("CoDev_Authorization") header: String, @Path("roomId") roomId: String) : Call<JsonObject>

    @GET("chat/participants/{roomId}")
    fun getChatRoomParticipants(@Header("CoDev_Authorization") header: String, @Path("roomId") roomId: String) : Call<ResGetChatRoomParticipants>

    @GET("infoBoard/{coInfoId}")
    fun getInfoDetail(@Header("CoDev_Authorization") header: String, @Path("coInfoId") coInfoId: Int) : Call<ResGetInfoDetail>

    @GET("qnaBoard/{coQnaId}")
    fun getQnaDetail(@Header("CoDev_Authorization") header: String, @Path("coQnaId") coQnaId: Int) : Call<ResGetQnaDetail>

    @PATCH("infoBoard/like/{coInfoId}")
    fun likeInfo(@Header("CoDev_Authorization") header: String, @Path("coInfoId") coInfoId: Int, @Body params: ReqLikePost) : Call<ResLikePost>

    @PATCH("qnaBoard/like/{coQnaId}")
    fun likeQna(@Header("CoDev_Authorization") header: String, @Path("coQnaId") coQnaId: Int, @Body params: ReqLikePost) : Call<ResLikePost>

    @DELETE("infoBoard/{coInfoId}")
    fun deleteInfo(@Header("CoDev_Authorization") header: String, @Path("coInfoId") coInfoId: Int) : Call<ResDeletePost>

    @DELETE("qnaBoard/{coQnaId}")
    fun deleteQna(@Header("CoDev_Authorization") header: String, @Path("coQnaId") coQnaId: Int) : Call<ResDeletePost>

    @POST("infoBoard/comment/{coInfoId}")
    fun createInfoParentComment(@Header("CoDev_Authorization") header: String, @Path("coInfoId") coInfoId: Int, @Body params: ReqCreateComment) : Call<ResConfirm>

    @POST("qnaBoard/comment/{coQnaId}")
    fun createQnaParentComment(@Header("CoDev_Authorization") header: String, @Path("coQnaId") coQnaId: Int, @Body params: ReqCreateComment) : Call<ResConfirm>

    @POST("infoBoard/recomments/{coCoib}")
    fun createInfoChildComment(@Header("CoDev_Authorization") header: String, @Path("coCoib") coCoib: Int, @Body params: ReqCreateComment) : Call<ResConfirm>
    @POST("qnaBoard/recomments/{coCoqb}")
    fun createQnaChildComment(@Header("CoDev_Authorization") header: String, @Path("coCoqb") coCoqb: Int, @Body params: ReqCreateComment) : Call<ResConfirm>

    @DELETE("infoBoard/out/comment/{coCoIb}")
    fun deleteInfoParentComment(@Header("CoDev_Authorization") header: String, @Path("coCoIb") coCoIb: Int) : Call<ResConfirm>

    @DELETE("qnaBoard/out/comment/{coCoqb}")
    fun deleteQnaParentComment(@Header("CoDev_Authorization") header: String, @Path("coCoqb") coCoqb: Int) : Call<ResConfirm>

    @DELETE("infoBoard/out/recomment/{coRcoIb}")
    fun deleteInfoChildComment(@Header("CoDev_Authorization") header: String, @Path("coRcoIb") coRcoIb: Int) : Call<ResConfirm>

    @DELETE("qnaBoard/out/recomment/{coRcoqb}")
    fun deleteQnaChildComment(@Header("CoDev_Authorization") header: String, @Path("coRcoqb") coRcoqb: Int) : Call<ResConfirm>

    @PATCH("infoBoard/mark/{coInfoId}")
    fun markInfo(@Header("CoDev_Authorization") header: String, @Path("coInfoId") coInfoId: Int) : Call<ResLikePost>

    @PATCH("qnaBoard/mark/{coQnaId}")
    fun markQna(@Header("CoDev_Authorization") header: String, @Path("coQnaId") coQnaId: Int) : Call<ResLikePost>
}