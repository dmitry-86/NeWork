package com.netology.nework.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.adapter.PostOnInteractionListener
import com.netology.nework.adapter.PostsAdapter
import com.netology.nework.databinding.FragmentPostsBinding
import com.netology.nework.dto.Post
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.PostViewModel

class PostsFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : PostOnInteractionListener {

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                showAlertDialog(post.content)
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

        viewModel.userData.observe(viewLifecycleOwner){ state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }


        binding.fab.setOnClickListener {
            if(authViewModel.authenticated) {
                findNavController().navigate(R.id.newPostFragment)
            }else{
                createDialog()
            }
        }

        return binding.root

    }

    private fun showAlertDialog(content: String) {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_change_post, null)

        val editText: EditText = placeFormView.findViewById(R.id.editTextContent)
        editText.setText(content)

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