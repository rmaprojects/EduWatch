package com.rmaprojects.parents.presentation.scoring

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter(
    context: FragmentActivity,
    private val fragmentList: List<Fragment>
): FragmentStateAdapter(context) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}