package com.netology.nework.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import com.netology.nework.R
import com.netology.nework.databinding.FragmentCreateMapBinding
import com.netology.nework.viewmodel.EventViewModel
import com.netology.nework.viewmodel.PostViewModel

class CreateMapFragment : Fragment() {
    private lateinit var googleMap: GoogleMap
    private var markers: MutableList<Marker> = mutableListOf<Marker>()

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCreateMapBinding.inflate(inflater, container, false)

        binding.ok.setOnClickListener {
            findNavController().navigate(R.id.newPostFragment)
        }

        return binding.root
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment

        lifecycle.coroutineScope.launchWhenCreated {
            googleMap = mapFragment.awaitMap().apply {
                isTrafficEnabled = true
                isBuildingsEnabled = true

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    setAllGesturesEnabled(true)
                }
            }

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    googleMap.apply {
                        isMyLocationEnabled = true
                        uiSettings.isMyLocationButtonEnabled = true
                    }

                    val fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(requireActivity())

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        println(it)
                    }
                }

                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    // TODO: show rationale dialog
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            val boundsBuilder = LatLngBounds.Builder()

            try {
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        1000,
                        1000,
                        0
                    )
                )
            } catch (e: IllegalStateException) {
                val target = LatLng(56.0153, 92.8932)
                googleMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        cameraPosition {
                            target(target)
                        }
                    )
                )
            }

            googleMap.setOnInfoWindowClickListener { markerToDelete ->
                markers.remove(markerToDelete)
                markerToDelete.remove()
            }

            googleMap.setOnMapClickListener { latLng ->

                val marker = googleMap.addMarker(MarkerOptions().position(latLng))

                val bundle = Bundle().apply {
                    putDouble("lat", marker!!.position.latitude)
                    putDouble("lng", marker!!.position.latitude)
                    putString("content", arguments?.getString("content"))
                    putString("link", arguments?.getString("link"))
                    putString("date", arguments?.getString("date"))
                    putString("time", arguments?.getString("time"))
                }


                when (arguments?.getString("fragment")) {
                    "newPost" -> findNavController().navigate(R.id.newPostFragment, bundle)
                    "newEvent" -> findNavController().navigate(R.id.newEventFragment, bundle)
                }

            }

        }


    }
}