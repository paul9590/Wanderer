package com.wanderer.client

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class GameViewAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
){
    private val fragList = ArrayList<Fragment>()

    init {
        fragList.add(ChatFragment())
        fragList.add(LogFragment())
    }

    override fun getCount(): Int {
        return fragList.size
    }

    override fun getItem(p0: Int): Fragment = fragList[p0]
}