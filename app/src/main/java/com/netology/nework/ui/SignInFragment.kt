package com.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.netology.nework.R
import com.netology.nework.auth.AppAuth
import com.netology.nework.databinding.FragmentSignInBinding
import com.netology.nework.viewmodel.SignInViewModel

class SignInFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)

        val viewModel: SignInViewModel by viewModels()

        binding.signInButton.setOnClickListener {
            viewModel.authUser(
                binding.logInField.text.toString(),
                binding.passwordField.text.toString()
            )
        }

        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.errorLogin) {
                binding.passwordField.error = getString(R.string.error_auth)
            }
        }

        return binding.root
    }
}