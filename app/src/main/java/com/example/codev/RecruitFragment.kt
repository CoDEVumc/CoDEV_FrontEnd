package com.example.codev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class RecruitFragment : Fragment() { //RecruitFragment 안에 RecruitProjectFragment와 RecruitStudyFragment 띄움
//                                  RecruitProjectFragment에서 버튼 눌러 RecruitStudyFragment로 전환

    var projectFragment = RecruitProjectFragment()
    var studyFragment = RecruitStudyFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recruit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    //프로젝트,스터디 Fragment 전환 함수 r_project, r_study
    fun r_project(){ //project로 전환
        childFragmentManager.beginTransaction()
            .replace(R.id.recruitframelayout, projectFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
    fun r_study(){ //study로 전환
        childFragmentManager.beginTransaction()
            .replace(R.id.recruitframelayout, studyFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }


}