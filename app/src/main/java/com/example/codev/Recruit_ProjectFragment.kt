package com.example.codev

import com.example.codev.databinding.ActivityMainBinding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.databinding.FragmentRecruitProjectBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Recruit_ProjectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Recruit_ProjectFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewBinding: FragmentRecruitProjectBinding
    var dataList: ArrayList<ProjectData> = arrayListOf()
    val dataRVAdapter = DataRVAdapter(dataList)

    //RecyclerView의 position 관리변수
    private var position = 0
    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result: ActivityResult ->
        if(result.resultCode == AppCompatActivity.RESULT_OK){
            val ptitle = result.data?.getStringExtra("ptitle")!!
            val pdday = result.data?.getStringExtra("pdday")!!
            val pheart = result.data?.getBooleanExtra("pheart")!!
            val pnum = result.data?.getIntExtra("pnum")!!
            val pcontent = result.data?.getStringExtra("pcontent")!!

            dataList.apply {
                add(ProjectData(ptitle,pdday,pheart,pnum,pcontent))
            }

            dataRVAdapter.notifyItemInserted(position)
            position++

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentRecruitProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.listviewMain.adapter = dataRVAdapter

        //역순으로 출력
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        viewBinding.listviewMain.layoutManager = manager




        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //추가
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recruit_project, container, false)
    }

    //추가
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.filter_loc)?.setOnClickListener{
            //dismiss()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Recruit_ProjectFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Recruit_ProjectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }





    //버튼 클릭 시 함수를 호출하는 함수
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        BUTTON.setOnClickListener { //<--프로젝트 버튼
//            (parentFragment as RecruitFragment).r_project()
//        }
//
//        BUTTON.setOnClickListener { //<--스터디 버튼
//            (parentFragment as RecruitFragment).r_study()
//        }
//    }


}