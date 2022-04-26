package com.netology.nework.ui

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.adapter.*
import com.netology.nework.databinding.FragmentEventsBinding
import com.netology.nework.databinding.FragmentFeedBinding
import com.netology.nework.databinding.FragmentJobsBinding
import com.netology.nework.dto.Event
import com.netology.nework.dto.Post
import com.netology.nework.enumeration.EventType
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.EventViewModel
import com.netology.nework.viewmodel.JobViewModel
import com.netology.nework.viewmodel.PostViewModel

class EventsFragment : Fragment() {

    private val viewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEventsBinding.inflate(
            inflater,
            container,
            false
        )


        val adapter = EventAdapter(object : EventOnInteractionListener {

            override fun onRemove(event: Event) {
//                viewModel.removeById(post.id)
            }

            override fun onEdit(event: Event) {
//                showAlertDialog(post.content)
                viewModel.edit(event)
            }

            override fun onLike(event: Event) {
                if(authViewModel.authenticated) {
                    viewModel.likeEvent(event)
                }else {
                    createDialog()
                }
            }

        })

        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadEvents() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner){ state ->
            adapter.submitList(state.events)
            binding.emptyText.isVisible = state.empty
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshEvents()
        }


        binding.fab.setOnClickListener {
            if(authViewModel.authenticated) {
                findNavController().navigate(R.id.newEventFragment)
            }else{
                createDialog()
            }
        }

        return binding.root

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