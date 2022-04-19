package com.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.netology.nework.adapter.MyPagerAdapter
import com.netology.nework.databinding.FragmentUserAccountBinding


class UserAccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserAccountBinding.inflate(inflater, container, false)

        binding?.viewpager?.adapter = MyPagerAdapter(this)
        binding?.tabs?.tabIconTint = null
        TabLayoutMediator(binding!!.tabs, binding!!.viewpager){
                tabs, pos ->
            when(pos){
                0 -> tabs.setText("Posts")
                1 -> tabs.setText("Events")
                else -> tabs.setText("Jobs")
            }
        }.attach()
//        binding!!.tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                tab?.icon?.alpha  = 70
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                tab?.icon?.alpha  = 70
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
//            }
//
//        })

        return binding?.root
    }



}

