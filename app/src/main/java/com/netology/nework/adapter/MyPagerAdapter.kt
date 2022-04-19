package com.netology.nework.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.netology.nework.ui.EventsFragment
import com.netology.nework.ui.JobsFragment
import com.netology.nework.ui.PostsFragment
import com.netology.nework.ui.UserAccountFragment

class MyPagerAdapter(activity: UserAccountFragment): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PostsFragment()
            1 -> EventsFragment()
            else -> JobsFragment()
        }
    }



}