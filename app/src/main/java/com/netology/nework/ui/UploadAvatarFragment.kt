package com.netology.nework.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.databinding.FragmentNewEventBinding
import com.netology.nework.databinding.FragmentUploadAvatarBinding
import com.netology.nework.dto.Coordinates
import com.netology.nework.enumeration.AttachmentType
import com.netology.nework.enumeration.EventType
import com.netology.nework.utils.*
import com.netology.nework.viewmodel.EventViewModel
import com.netology.nework.viewmodel.PostViewModel
import com.netology.nework.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*


class UploadAvatarFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var fragmentBinding: FragmentUploadAvatarBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentUploadAvatarBinding.inflate(inflater, container, false)
        fragmentBinding = binding

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

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.mediaContainer.visibility = View.GONE
                return@observe
            }

            binding.mediaContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }


        binding.ok.setOnClickListener {
//            viewModel.changeContent(
//                binding.edit.text.toString(),
//                Coordinates(latitude, longitude),
//                binding.link.text.toString()
//            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.userCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.userAccountFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}