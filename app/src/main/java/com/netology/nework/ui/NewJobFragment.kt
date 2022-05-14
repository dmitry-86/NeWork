package com.netology.nework.ui


import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.netology.nework.R
import com.netology.nework.databinding.FragmentNewJobBinding
import com.netology.nework.utils.*
import com.netology.nework.viewmodel.JobViewModel

class NewJobFragment : Fragment() {

    private val viewModel: JobViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var fragmentBinding: FragmentNewJobBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewJobBinding.inflate(inflater, container, false)
        fragmentBinding = binding

        binding.ok.setOnClickListener {
            viewModel.changeContent(
                binding.name.text.toString(),
                binding.position.text.toString(),
                binding.start.text.toString(),
                binding.finish.text.toString(),
                binding.link.text.toString()
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.feedFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}