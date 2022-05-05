package com.netology.nework.ui

import android.content.DialogInterface
import android.os.Bundle
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
import com.netology.nework.dto.Event
import com.netology.nework.enumeration.EventType
import com.netology.nework.viewmodel.AuthViewModel
import com.netology.nework.viewmodel.EventViewModel

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
                viewModel.removeById(event.id)
            }

            override fun onEdit(event: Event) {
                showAlertDialog(event.content, event.datetime, event.link!!, event.type)
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

    private fun showAlertDialog(content: String, datetime: String, link: String, eventType: EventType) {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_change_event, null)

        val editText: EditText = placeFormView.findViewById(R.id.editTextView)
        editText.setText(content)
        val linkEditText: EditText = placeFormView.findViewById(R.id.linkTextView)
        linkEditText.setText(link)

        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit)).setMessage(getString(R.string.enter_new_content))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val content = placeFormView.findViewById<EditText>(R.id.editTextView).text.toString()
            val datetime = datetime
            val link = placeFormView.findViewById<EditText>(R.id.linkTextView).text.toString()
            val eventType = eventType
            if (content.trim().isEmpty()) {
                Toast.makeText(activity, "сообщение пустое", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            viewModel.changeContent(content, datetime, link, eventType)
            viewModel.save()
            dialog.dismiss()
        }
    }


    private fun createDialog(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Would you like to sign in?")
        builder.setNeutralButton("Yes"){dialogInterface, i ->
            findNavController().navigate(R.id.signInFragment)
        }
        builder.setNegativeButton("No"){dialog, i ->
            findNavController().navigate(R.id.feedFragment)
        }
        builder.show()
    }


}