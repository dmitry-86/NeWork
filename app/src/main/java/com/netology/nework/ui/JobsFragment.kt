package com.netology.nework.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.adapter.JobOnInteractionListener
import com.netology.nework.adapter.JobsAdapter
import com.netology.nework.adapter.PostOnInteractionListener
import com.netology.nework.adapter.PostsAdapter
import com.netology.nework.databinding.FragmentFeedBinding
import com.netology.nework.databinding.FragmentJobsBinding
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.JobViewModel
import com.netology.nework.viewmodel.PostViewModel

class JobsFragment : Fragment() {

    private val viewModel: JobViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )



    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentJobsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = JobsAdapter(object : JobOnInteractionListener {

            override fun onRemove(job: Job) {
                viewModel.removeById(job.id)
            }

            override fun onEdit(job: Job) {
                showAlertDialog()
                viewModel.edit(job)
            }

            override fun onLinkClick(job: Job) {
                val intent = Intent(Intent.ACTION_VIEW)
                val url = job.link
                if (!url?.startsWith("http://")!! && !url.startsWith("https://")) {
                    intent.data = Uri.parse("http://" + url)
                }else{
                    intent.data = Uri.parse(url)
                }
                startActivity(intent)
            }

        })

        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadJobs() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner){ state ->
            adapter.submitList(state.jobs)
            Log.i("tag", state.jobs.toString())
            binding.emptyText.isVisible = state.empty
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshJobs()
        }


        binding.fab.setOnClickListener {
            if(authViewModel.authenticated) {
//                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                showAlertDialog()
            }else{
                findNavController().navigate(R.id.signInFragment)
            }
        }

        return binding.root

    }

    private fun showAlertDialog() {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_create_job, null)

//        val editText: EditText = placeFormView.findViewById(R.id.editTextContent)
//        editText.setText(content)

        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit)).setMessage(getString(R.string.enter_new_content))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val name = placeFormView.findViewById<EditText>(R.id.name).text.toString()
            val position  = placeFormView.findViewById<EditText>(R.id.position).text.toString()
            val started  = placeFormView.findViewById<EditText>(R.id.started).text.toString()
            val finished  = placeFormView.findViewById<EditText>(R.id.finished).text.toString()
            val link  = placeFormView.findViewById<EditText>(R.id.link).text.toString()
            if (name.trim().isEmpty()
                || position.trim().isEmpty()
                || started.trim().isEmpty()
                || finished.trim().isEmpty()
                || link.trim().isEmpty()){
                Toast.makeText(activity, "сообщение пустое", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            viewModel.changeContent(name, position, started, finished, link)
            viewModel.save()
            dialog.dismiss()


        }

    }


}