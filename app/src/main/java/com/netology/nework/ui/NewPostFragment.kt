package com.netology.nework.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.netology.nework.dto.Coordinates
import com.netology.nework.ui.NewEventFragment.Companion.textArg
import com.netology.nework.utils.AndroidUtils
import com.netology.nework.utils.StringArg


class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        latitude = arguments?.getDouble("lat")
        longitude = arguments?.getDouble("lng")

        arguments?.textArg
            ?.let(binding.edit::setText)
        arguments?.textArg
            ?.let(binding.link::setText)
        binding.edit.setText(arguments?.getString("content"))
        arguments?.textArg
            ?.let(binding.link::setText)
        binding.link.setText(arguments?.getString("link"))

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


        binding.location.setOnClickListener {
            val bundle = Bundle().apply {
                putString("content", binding.edit.text.toString())
                putString("link", binding.link.text.toString())
                latitude?.let { it1 -> putDouble("lat", it1) }
                longitude?.let { it1 -> putDouble("lng", it1) }
                putString("fragment", "newPost")
            }
            findNavController().navigate(
                R.id.createMapFragment, bundle
            )
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
            viewModel.changeContent(
                binding.edit.text.toString(),
                Coordinates(latitude!!, longitude!!),
                binding.link.text.toString()
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.feedFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}