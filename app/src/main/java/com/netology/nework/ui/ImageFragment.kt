package com.netology.nework.ui

import com.netology.nework.BuildConfig.BASE_URL
import com.netology.nework.databinding.FragmentImageBinding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class ImageFragment : Fragment() {
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(inflater, container,false)

        val url = "${arguments?.getString("url")}"

        Glide.with(this)
            .load(url)
            .into(binding.imageView)

        return binding.root
    }
}