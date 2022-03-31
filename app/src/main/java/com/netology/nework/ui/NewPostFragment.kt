package com.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.netology.nework.databinding.FragmentNewPostBinding
import com.netology.nework.viewmodel.PostViewModel
import androidx.navigation.fragment.findNavController
import com.netology.nework.utils.AndroidUtils
import com.netology.nework.utils.StringArg


class NewPostFragment: Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        binding.edit.setText(arguments?.getString("content"))
        binding.edit.requestFocus()

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().popBackStack()
        }

        return binding.root
    }


}