package com.netology.nework.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import com.netology.nework.databinding.FragmentNewPostBinding
import com.netology.nework.viewmodel.PostViewModel
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.databinding.FragmentNewEventBinding
import com.netology.nework.enumeration.EventType
import com.netology.nework.utils.AndroidUtils
import com.netology.nework.utils.StringArg
import com.netology.nework.viewmodel.EventViewModel


class NewEventFragment: Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var fragmentBinding: FragmentNewEventBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewEventBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        arguments?.textArg
            ?.let(binding.edit::setText)
        arguments?.textArg
            ?.let(binding.link::setText)

        val event = binding.spinner

        val adapter = ArrayAdapter.createFromResource(binding.getRoot().getContext(), R.array.eventType, android.R.layout.simple_spinner_dropdown_item)
        event.adapter = adapter
        val selectedItem = event.selectedItem.toString()
        val eventType: EventType
                when(selectedItem){
                    "online"-> eventType = EventType.ONLINE
                    else-> eventType = EventType.OFFLINE
                }


        binding.edit.requestFocus()


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

        binding.location.setOnClickListener{
            findNavController().navigate(
                R.id.action_newPostFragment_to_createMapFragment)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString(), binding.link.text.toString(), eventType)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.feedFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}