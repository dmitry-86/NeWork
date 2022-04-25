package com.netology.nework.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import com.google.maps.android.ktx.utils.collection.addMarker
import com.netology.nework.R
import com.netology.nework.viewmodel.PostViewModel

class DisplayMapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

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
        return inflater.inflate(R.layout.fragment_display_maps, container, false)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

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

            val position = arguments?.getLong("id")

            Log.i("tag", position.toString())

            viewModel.data.observe(viewLifecycleOwner) { state ->

                if (position != null) {
                    if (state.posts.get(position.toInt()).coords == null) {
                        try {
                            googleMap?.apply {
                                val sydney = LatLng(-33.852, 151.211)
                                addMarker(
                                    MarkerOptions()
                                        .position(sydney)
                                        .title("Marker in Sydney")
                                )
                                // [START_EXCLUDE silent]
                                moveCamera(
                                    CameraUpdateFactory.newLatLng(
                                        sydney
                                    )
                                )
                            }
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
                    } else {
                        val currentTarget = LatLng(
                            state.posts.get(position!!.toInt()).coords!!.lat,
                            state.posts.get(position.toInt()).coords!!.long
                        )
                        googleMap.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                cameraPosition {
                                    target(currentTarget)
                                    zoom(5F)
                                }
                            ))
                    }
                }

            }


        }
    }
}

