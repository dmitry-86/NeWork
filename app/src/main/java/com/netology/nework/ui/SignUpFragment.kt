package com.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.netology.nework.auth.AppAuth
import com.netology.nework.databinding.FragmentSignUpBinding
import com.netology.nework.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val viewModel: SignUpViewModel by viewModels()

        val name = binding.nameField.text
        val login = binding.logInField.text
        val pass = binding.passwordField.text
        val passwordRepeat = binding.passwordRepeatField.text

        binding.signInButton.setOnClickListener {
            if (name!!.isBlank() || pass!!.isBlank()) {
                Toast.makeText(context, "Fields could not be blank", Toast.LENGTH_LONG).show()
            }else if(pass.toString() != passwordRepeat.toString()){
                Toast.makeText(context, "passwords do not match", Toast.LENGTH_LONG).show()
            }else{
                viewModel.registerUser(login.toString(), pass.toString(), name.toString())
            }
        }

        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        return binding.root
    }
}