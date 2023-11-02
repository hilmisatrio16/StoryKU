package com.hilmisatrio.storyku.ui.maps

import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.response.Story
import com.hilmisatrio.storyku.databinding.FragmentMapsBinding

class MapsFragment : Fragment() {


    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val latLngboundsBuilder = LatLngBounds.Builder()
    private val mapsViewModel by viewModels<MapsViewModel> {
        MapsViewModelFactory.getInstance(requireActivity())
    }

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        setMapStyle(googleMap)
        getDataStories(googleMap)
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            val mapStyleSuccessed = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style_stories
                )
            )
        } catch (exception: Resources.NotFoundException) {
            Log.e("MapStories", "Cannot find map style", exception)
        }
    }

    private fun getDataStories(googleMap: GoogleMap) {
        mapsViewModel.getToken().observe(viewLifecycleOwner) {
            if (it != null) {
                showLocationStories(it, googleMap)
            }
        }

    }

    private fun showLocationStories(token: String, googleMap: GoogleMap) {
        mapsViewModel.getStoriesFromLocation(token).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    binding.mapsProgressBar.visibility = View.GONE
                    setLocation(it.data.listStory, googleMap)
                }

                is Result.Loading -> {
                    binding.mapsProgressBar.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.mapsProgressBar.visibility = View.GONE
                    showSnackBar("Maps cannot appear", false)
                }
            }
        }
    }

    private fun showSnackBar(message: String, isSuccess: Boolean) {
        val snackbar = Snackbar.make(
            requireActivity().window.decorView.rootView, message, Snackbar.LENGTH_SHORT
        )

        if (isSuccess) {
            snackbar.setBackgroundTint(
                ContextCompat.getColor(
                    requireContext().applicationContext,
                    R.color.green_light
                )
            )
        } else {
            snackbar.setBackgroundTint(
                ContextCompat.getColor(
                    requireContext().applicationContext,
                    R.color.red_light
                )
            )
        }

        snackbar.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.white
            )
        )
        snackbar.show()
    }

    private fun setLocation(listStories: List<Story>, googleMap: GoogleMap) {
        listStories.forEach {
            val latLng = LatLng(it.lat, it.lon)
            googleMap.addMarker(
                MarkerOptions().position(latLng).title(it.name).snippet(it.description)
            )
            latLngboundsBuilder.include(latLng)
        }

        val boundsBuilder: LatLngBounds = latLngboundsBuilder.build()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                boundsBuilder,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

