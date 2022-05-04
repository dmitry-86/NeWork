package com.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.adapter.UserAdapter
import com.netology.nework.adapter.UserOnInteractionListener
import com.netology.nework.databinding.FragmentUsersBinding
import com.netology.nework.dto.User
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.UserViewModel

class UsersFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUsersBinding.inflate(
            inflater,
            container,
            false
        )

        val bundle = Bundle()

        val adapter = UserAdapter(object : UserOnInteractionListener {

            override fun onItemClick(user: User) {
                bundle.putString("name", user.name.toString())
                bundle.putString("login", user.login.toString())
                findNavController().navigate(R.id.userAccountFragment, bundle)
            }


        })

        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadUsers() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner){ state ->
            adapter.submitList(state.users)
//            binding.emptyText.isVisible = state.empty
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshUsers()
        }


        binding.fab.setOnClickListener {
            if(authViewModel.authenticated) {
//                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }else{
                createDialog()
            }
        }

        return binding.root

    }

    private fun createDialog(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Would you like to sign in?")
        builder.setNeutralButton("Yes"){dialogInterface, i ->
            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
        }
        builder.setNegativeButton("No"){dialog, i ->
            findNavController().navigate(R.id.feedFragment)
        }
        builder.show()
    }


}