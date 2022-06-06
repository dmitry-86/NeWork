package com.netology.nework.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.auth.AppAuth
import com.netology.nework.databinding.FragmentNewPostBinding
import com.netology.nework.databinding.FragmentSignUpBinding
import com.netology.nework.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()

    private var fragmentBinding: FragmentSignUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val name = binding.nameField.text
        val login = binding.logInField.text
        val pass = binding.passwordField.text
        val passwordRepeat = binding.passwordRepeatField.text

        //set avatar

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.pickAvatar.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.signInButton.setOnClickListener {
            if (name!!.isBlank() || pass!!.isBlank()) {
                Toast.makeText(context, "Fields could not be blank", Toast.LENGTH_LONG).show()
            }else if(pass.toString() != passwordRepeat.toString()){
                Toast.makeText(context, "passwords do not match", Toast.LENGTH_LONG).show()
            }else{
                viewModel.registerUser(login.toString(), pass.toString(), name.toString())
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                return@observe
            }
            binding.avatarImage.setImageURI(it.uri)
        }

        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigate(R.id.userAccountFragment)
        })

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}