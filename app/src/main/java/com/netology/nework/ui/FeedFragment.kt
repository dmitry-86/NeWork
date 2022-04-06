package com.netology.nework.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.adapter.OnInteractionListener
import com.netology.nework.adapter.PostsAdapter
import com.netology.nework.databinding.FragmentFeedBinding
import com.netology.nework.dto.Post
import com.netology.nework.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )


        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                showAlertDialog()
                viewModel.edit(post)
            }

        })

        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner){ state ->
            adapter.submitList(state.posts)
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root

    }

    private fun showAlertDialog() {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit)).setMessage(getString(R.string.enter_new_content))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val content = placeFormView.findViewById<EditText>(R.id.editTextContent).text.toString()
            if (content.trim().isEmpty()) {
                Toast.makeText(activity, "сообщение пустое", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            viewModel.changeContent(content)
            viewModel.save()
            dialog.dismiss()
        }

    }

}