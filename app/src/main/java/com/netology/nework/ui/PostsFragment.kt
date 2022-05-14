package com.netology.nework.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.netology.nework.dto.Coordinates
import com.netology.nework.dto.Event
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
                showAlertDialog(post.content, post.coords!!, post.link!!)
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    viewModel.likePost(post)
                } else {
                    createDialog()
                }
            }

            val bundle = Bundle()

            override fun onImageClick(post: Post) {
                bundle.putString("url", post.attachment?.url.toString())
                findNavController().navigate(
                    R.id.imageFragment, bundle)
            }

            override fun onPlayAudio(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "audio/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPlayVideo(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "video/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_play, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onLocationClick(post: Post) {
                val bundle = Bundle().apply {
                    post.coords?.lat?.let { putDouble("lat", it) }
                    post.coords?.long?.let { putDouble("lng", it) }
                }
                findNavController().navigate(
                    R.id.displayMapsFragment, bundle
                )
            }

            override fun onLinkClick(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW)
                val url = post.link
                if (!url?.startsWith("http://")!! && !url.startsWith("https://")) {
                    intent.data = Uri.parse("http://" + url)
                } else {
                    intent.data = Uri.parse(url)
                }
                startActivity(intent)
            }

        })

        binding.list.adapter = adapter

        viewModel.userData.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        })

        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }


        binding.fab.setOnClickListener {
            if (authViewModel.authenticated) {
                findNavController().navigate(R.id.newPostFragment)
            } else {
                createDialog()
            }
        }

        return binding.root

    }

    private fun showAlertDialog(content: String, coords: Coordinates, link: String) {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_change_post, null)

        val editText: EditText = placeFormView.findViewById(R.id.editEditText)
        editText.setText(content)
        val linkEditText: EditText = placeFormView.findViewById(R.id.linkEditText)
        linkEditText.setText(link)

        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit))
            .setMessage(getString(R.string.enter_new_content))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val content =
                placeFormView.findViewById<EditText>(R.id.editEditText).text.toString()
            val coords = coords
            val link = placeFormView.findViewById<EditText>(R.id.linkEditText).text.toString()
            if (content.trim().isEmpty()) {
                Toast.makeText(activity, "сообщение пустое", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            viewModel.changeContent(content, coords, link)
            viewModel.save()
            dialog.dismiss()
        }
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Would you like to sign in?")
        builder.setNeutralButton("Yes") { dialogInterface, i ->
            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
        }
        builder.setNegativeButton("No") { dialog, i ->
            findNavController().navigate(R.id.feedFragment)
        }
        builder.show()
    }

}