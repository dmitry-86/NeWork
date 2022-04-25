package com.netology.nework.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.netology.nework.BuildConfig
import com.netology.nework.R
import com.netology.nework.adapter.MyPagerAdapter
import com.netology.nework.databinding.FragmentUserAccountBinding
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.PostViewModel
import com.netology.nework.viewmodel.UserViewModel


class UserAccountFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val userViewModel: UserViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserAccountBinding.inflate(inflater, container, false)


        authViewModel.data.observe(viewLifecycleOwner) { user ->
            userViewModel.data.observe(viewLifecycleOwner) {
                if (user.id.toInt() > 0) {
                    binding.tvName.text = it.get(user.id.toInt() - 1).name
                    binding.tvLogin.text = it.get(user.id.toInt() - 1).login
                } else {
                    binding.tvName.text = "Dima"
                    binding.tvLogin.text = "dima"
                }
            }
        }

        binding.ivAvatar.setImageResource(R.drawable.avatar)


        binding?.viewpager?.adapter = MyPagerAdapter(this)
        binding?.tabs?.tabIconTint = null
        TabLayoutMediator(binding!!.tabs, binding!!.viewpager) { tabs, pos ->
            when (pos) {
                0 -> tabs.setText("Posts")
                1 -> tabs.setText("Events")
                else -> tabs.setText("Jobs")
            }
        }.attach()

        return binding?.root
    }

}

