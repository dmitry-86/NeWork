package com.netology.nework.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.netology.nework.R
import com.netology.nework.databinding.FragmentNewEventBinding
import com.netology.nework.enumeration.EventType
import com.netology.nework.utils.*
import com.netology.nework.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*


class NewEventFragment: Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var fragmentBinding: FragmentNewEventBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
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
        binding.edit.setText(arguments?.getString("content"))


        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            binding.date.setOnClickListener{
                val now = Calendar.getInstance()
                val datePicker = DatePickerDialog(binding.getRoot().getContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val selectedDate= Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = format.format(selectedDate.time).toString()
                    binding.editDate.setText(date)
//                Toast.makeText(binding.getRoot().getContext(), "date: $date", Toast.LENGTH_SHORT).show()
                },
                    now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                datePicker.show()
            }

        binding.time.setOnClickListener{
            val now = Calendar.getInstance()
            val timePicker = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                now.set(Calendar.HOUR_OF_DAY, hour)
                now.set(Calendar.MINUTE, minute)
                val time = SimpleDateFormat("HH:mm").format(now.time)
                binding.editTime.setText(time)
            }
            val time = TimePickerDialog(context, timePicker, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)

            time.setTitle("")
            time.show()
        }


        val event = binding.spinner

        val adapter = ArrayAdapter.createFromResource(binding.getRoot().getContext(), R.array.eventType, android.R.layout.simple_spinner_dropdown_item)
        event.adapter = adapter
        val selectedItem = event.selectedItem.toString()
        val eventType: EventType
                when(selectedItem){
                    "online"-> eventType = EventType.ONLINE
                    else-> eventType = EventType.OFFLINE
                }

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

//

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString(), binding.editDate.text.toString() + " " + binding.editTime.text.toString(), binding.link.text.toString(), eventType)
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