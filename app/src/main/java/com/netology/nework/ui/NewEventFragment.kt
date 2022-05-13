package com.netology.nework.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.netology.nework.dto.Coordinates
import com.netology.nework.enumeration.EventType
import com.netology.nework.utils.*
import com.netology.nework.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*


class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var fragmentBinding: FragmentNewEventBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewEventBinding.inflate(inflater, container, false)
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


        arguments?.textArg
            ?.let(binding.date::setText)
        binding.editDate.setText(arguments?.getString("date"))

        arguments?.textArg
            ?.let(binding.time::setText)
        binding.editTime.setText(arguments?.getString("time"))


        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        binding.date.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                binding.getRoot().getContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = format.format(selectedDate.time).toString()
                    binding.editDate.setText(date)
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.time.setOnClickListener {
            val now = Calendar.getInstance()
            val timePicker = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                now.set(Calendar.HOUR_OF_DAY, hour)
                now.set(Calendar.MINUTE, minute)
                val time = SimpleDateFormat("HH:mm").format(now.time)
                binding.editTime.setText(time)
            }
            val time = TimePickerDialog(
                context,
                timePicker,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            )

            time.setTitle("")
            time.show()
        }

        var types = arrayOf("online", "offline")
        val spinner = binding.spinner
        val adapter = ArrayAdapter(
            binding.getRoot().getContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )
        spinner.adapter = adapter

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

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }


        binding.location.setOnClickListener {
            val bundle = Bundle().apply {
                putString("content", binding.edit.text.toString())
                putString("link", binding.link.text.toString())
                putString("date", binding.editDate.text.toString())
                putString("time", binding.editTime.text.toString())
                latitude?.let { it1 -> putDouble("lat", it1) }
                longitude?.let { it1 -> putDouble("lng", it1) }
                putString("fragment", "newEvent")
            }
            findNavController().navigate(
                R.id.createMapFragment, bundle
            )
        }

        binding.ok.setOnClickListener {
            viewModel.changeContent(
                binding.edit.text.toString(),
                binding.editDate.text.toString() + "T" + binding.editTime.text.toString() + ":00.000000Z",
                Coordinates(latitude!!, longitude!!),
                binding.link.text.toString(),
                spinner.selectedItem.toString()
            )
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