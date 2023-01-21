package com.example.codev.addpage

import android.content.res.Resources
import com.example.codev.R
import java.util.HashMap

class MakeStackDropdown {
    public fun createAllStackHashMap(resources: Resources): HashMap<Int, ArrayList<AddListItem>>{
        var allStackList = HashMap<Int, ArrayList<AddListItem> >()
        allStackList[-1] = ArrayList<AddListItem>()

        var pmStackStringList = resources.getStringArray(R.array.pm_stack);
        allStackList[0] = ArrayList<AddListItem>()
        for(i in pmStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[0]?.add(AddListItem(false, stackName, stackInt))
        }

        var designStackStringList = resources.getStringArray(R.array.design_stack);
        allStackList[1] = ArrayList<AddListItem>()
        for(i in designStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[1]?.add(AddListItem(false, stackName, stackInt))
        }

        var frontStackStringList = resources.getStringArray(R.array.front_stack);
        allStackList[2] = ArrayList<AddListItem>()
        for(i in frontStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[2]?.add(AddListItem(false, stackName, stackInt))
        }

        var backStackStringList = resources.getStringArray(R.array.back_stack);
        allStackList[3] = ArrayList<AddListItem>()
        for(i in backStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[3]?.add(AddListItem(false, stackName, stackInt))
        }

        var etcStackStringList = resources.getStringArray(R.array.etc_stack);
        allStackList[4] = ArrayList<AddListItem>()
        for(i in etcStackStringList){
            val splitList = i.split(":-:")
            val stackName = splitList[0]
            val stackInt = splitList[1].toInt()
            allStackList[4]?.add(AddListItem(false, stackName, stackInt))
        }
        return allStackList
    }
}