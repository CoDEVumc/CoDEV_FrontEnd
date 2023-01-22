//package com.example.codev
//
//import android.content.Context
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.PopupMenu
//import android.widget.Toast
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.FragmentTransaction
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.codev.databinding.FragmentRecruitBinding
//import com.example.codev.databinding.FragmentRecruitProjectBinding
//import com.example.codev.databinding.FragmentRecruitStudyBinding
//
//
//class Recruit_StudyFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
//    private lateinit var viewBinding: FragmentRecruitStudyBinding
//
//    var dataList: ArrayList<ProjectData> = arrayListOf()
//    val dataRVAdapter = DataRVAdapter(dataList)
//
//    var mainAppActivity: MainAppActivity? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if(context is MainAppActivity){
//            mainAppActivity = context
//        }
//    }
//
//
//    //RecyclerView의 position 관리변수
//    private var position = 0
//    val startForResult = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ){ result: ActivityResult ->
//        if(result.resultCode == AppCompatActivity.RESULT_OK){
//            val ptitle = result.data?.getStringExtra("co_title")!!
//            val pdday = result.data?.getStringExtra("co_deadLine")!!
//            val pheartnum = result.data?.getIntExtra("co_heartCount",0)!!
//            val pishearted = result.data?.getBooleanExtra("co_heart",false)!!
//            val pnum = result.data?.getIntExtra("co_totalnum",0)!!
//            val ppart = result.data?.getStringExtra("co_parts")!!
//            val plangs = result.data?.getStringExtra("co_languages")!!
//
//            dataList.apply {
//                add(ProjectData(ptitle,pdday,pheartnum,pishearted,pnum,ppart,plangs))
//            }
//
//            dataRVAdapter.notifyItemInserted(position)
//            position++
//
//        }
//
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewBinding = FragmentRecruitProjectBinding.inflate(layoutInflater)
//
//        viewBinding.listviewMain.adapter = dataRVAdapter
//
//        //역순으로 출력
//        val manager = LinearLayoutManager(this)
//        manager.reverseLayout = true
//        manager.stackFromEnd = true
//
//        viewBinding.listviewMain.layoutManager = manager
//
//
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        viewBinding = FragmentRecruitStudyBinding.inflate(layoutInflater)
//        var temp = viewBinding.toolbarRecruit.toolbarS
//        viewBinding.toolbarRecruit.toolbarS.inflateMenu(R.menu.menu_recruit_project)
//        viewBinding.toolbarRecruit.toolbarS.title = ""
//        viewBinding.toolbarRecruit.toolbarS.toolbar_text.setOnClickListener {
//            var popupMenu = PopupMenu(mainAppActivity, temp)
//            popupMenu.inflate(R.menu.menu_recruit_study)
//            popupMenu.show()
//        }
//        viewBinding.toolbarRecruit.downBig2.setOnClickListener {
//            var popupMenu = PopupMenu(mainAppActivity, temp)
//            popupMenu.inflate(R.menu.menu_recruit_study)
//            popupMenu.show()
//        }
//
//        viewBinding.toolbarRecruit.toolbarS.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.menu_search ->{
//                    Toast.makeText(mainAppActivity, "검색", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.menu_alarm ->{
//                    Toast.makeText(mainAppActivity, "알람", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> false
//            }
//        }
//        //추가
//        super.onCreateView(inflater, container, savedInstanceState)
//
//        // Inflate the layout for this fragment
//        //return inflater.inflate(R.layout.fragment_recruit_project, container, false)
//        return viewBinding.root
//        //return inflater.inflate(R.layout.fragment_recruit_study, container, false)
//    }
//
//    //추가
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        view?.findViewById<Button>(R.id.filter_loc)?.setOnClickListener{
//            //dismiss()
//        }
//    }
//
//    // 팝업 메뉴 아이템 클릭 시 실행되는 메소드
//    override fun onMenuItemClick(item: MenuItem?): Boolean {
//        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
//            R.id.m_project -> r_project()
//            R.id.m_study -> r_study()
//        }
//
//        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
//    }
//
//    //프로젝트,스터디 Fragment 전환 함수 r_project, r_study
//    fun r_project(){
//        childFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, projectFragment)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
//    }
//
//    fun r_study(){
//        childFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, studyFragment)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
//    }
//}