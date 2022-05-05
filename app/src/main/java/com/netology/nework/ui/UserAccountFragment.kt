package com.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.netology.nework.R
import com.netology.nework.adapter.MyPagerAdapter
import com.netology.nework.auth.AppAuth
import com.netology.nework.databinding.FragmentUserAccountBinding
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.UserViewModel


class UserAccountFragment : Fragment() {

    private val userViewModel: UserViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val userId = AppAuth.getInstance().authStateFlow.value.id

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserAccountBinding.inflate(inflater, container, false)

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.editUserFragment)
        }


//        val name = arguments?.getString("name")
//        val login = arguments?.getString("login")
//
//        with(binding) {
//            textViewName.text = name
//            textViewLogin.text = login
//        }


        userViewModel.getUserById(userId)

        userViewModel.user.observe(viewLifecycleOwner) {
            with(binding) {
                textViewName.text = it.name
                textViewLogin.text = it.login
            }
        }


//                    binding.textViewLogin.text = it.get(userId.toInt()+1).login
//                    val avatar = "${BuildConfig.BASE_URL}/avatars/${
//                        it.get(userId.toInt()).avatar
//                    }"
//                    with(binding) {
//                        Glide.with(imageViewAvatar)
//                            .load("$avatar")
//                            .transform(CircleCrop())
//                            .placeholder(R.drawable.avatar)
//                            .into(imageViewAvatar)
//                    }

        binding.imageViewAvatar.setImageResource(R.drawable.avatar)

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

