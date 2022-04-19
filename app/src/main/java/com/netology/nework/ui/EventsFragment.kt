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

//            override fun onRemove(post: Post) {
//                viewModel.removeById(post.id)
//            }
//
//            override fun onEdit(post: Post) {
//                showAlertDialog(post.content)
//                viewModel.edit(post)
//            }

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
            LayoutInflater.from(activity).inflate(R.layout.dialog_create_event, null)

//        val editText: EditText = placeFormView.findViewById(R.id.editTextContent)
//        editText.setText(content)

        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit)).setMessage(getString(R.string.enter_new_content))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val content = placeFormView.findViewById<EditText>(R.id.content).text.toString()
//            val date  = placeFormView.findViewById<EditText>(R.id.date).text.toString()
            val event: Spinner = placeFormView.findViewById<Spinner>(R.id.spinner)

            val adapter = ArrayAdapter.createFromResource(placeFormView.getContext(), R.array.eventType, android.R.layout.simple_spinner_dropdown_item)
            event.adapter = adapter
            val selectedItem = event.selectedItem.toString()

            if (content.trim().isEmpty()
//                || date.trim().isEmpty()
                || selectedItem.trim().isEmpty()){
                Toast.makeText(activity, "сообщение пустое", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            val eventType: EventType
            when(selectedItem){
                "online"-> eventType = EventType.ONLINE
                 else-> eventType = EventType.OFFLINE
            }

            viewModel.changeContent(content, eventType)
            viewModel.save()
            dialog.dismiss()


        }

    }


}