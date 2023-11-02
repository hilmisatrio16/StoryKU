package com.hilmisatrio.storyku.ui.post

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.request.RequestNewStory
import com.hilmisatrio.storyku.databinding.FragmentPostBinding
import com.hilmisatrio.storyku.utils.getImageUri
import com.hilmisatrio.storyku.utils.reduceFileImage
import com.hilmisatrio.storyku.utils.uriToFile

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private var currentImageStoryUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val postViewModel by viewModels<PostViewModel> {
        PostViewModelFactory.getInstance(requireActivity())
    }

    private val requestPermissionGetLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLocation()
            }

            else -> {
                showSnackBar("Permission not granted", false)
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    postViewModel.setLocation(location.latitude, location.longitude)
                } else {
                    showSnackBar("Location is not found. Try Again", false)
                }
            }
        } else {
            requestPermissionGetLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showSnackBar(resources.getString(R.string.granted), true)
        } else {
            showSnackBar(resources.getString(R.string.denied), false)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_DENIED


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun checkRequestPermission() {
        if (allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkRequestPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        binding.buttonGallery.setOnClickListener {
            pickImageFromGallery()
        }

        binding.buttonTakeCamera.setOnClickListener {
            takeImageCamera()
        }

        binding.buttonUpload.setOnClickListener {
            if (activeNetwork != null) {
                if (activeNetwork.isConnectedOrConnecting) {
                    checkValidation()
                } else {
                    showSnackBar(resources.getString(R.string.not_connect), false)
                }
            } else {
                showSnackBar(resources.getString(R.string.not_connect), false)
            }

        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonChangeImage.setOnClickListener {
            showMenuSelectImage(it, R.menu.menu_select_image)
        }

        setCheckBoxLocationlistener()


    }

    private fun setCheckBoxLocationlistener() {
        binding.checkboxMylocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            }
        }
    }

    private fun checkValidation() {
        if (binding.edDescription.text.toString().isNotEmpty() && currentImageStoryUri != null) {
            postViewModel.getToken().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    if (binding.checkboxMylocation.isChecked) {
                        val lat = postViewModel.latitute.value
                        val lon = postViewModel.longitude.value
                        if (lat != null && lon != null) {
                            uploadNewStory(it, lat.toFloat(), lon.toFloat())
                        }
                    } else {
                        uploadNewStory(it, null, null)
                    }
                }
            }

        } else {
            showSnackBar(resources.getString(R.string.data_not_empty), false)
        }
    }

    private fun showMenuSelectImage(v: View, @MenuRes menuRes: Int) {

        val popUpMenu = android.widget.PopupMenu(context, v)
        popUpMenu.menuInflater.inflate(menuRes, popUpMenu.menu)

        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.camera -> {
                    takeImageCamera()
                    true
                }

                R.id.gallery -> {
                    pickImageFromGallery()
                    true
                }

                else -> false
            }
        }
        popUpMenu.show()
    }

    private val launchImageGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            currentImageStoryUri = it
            showStoryImage()
        } else {
            showSnackBar(resources.getString(R.string.no_image_selected), false)
        }

    }

    private fun pickImageFromGallery() {
        launchImageGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun takeImageCamera() {
        currentImageStoryUri = getImageUri(requireContext())
        launcherCamera.launch(currentImageStoryUri)

    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            showStoryImage()
        }
    }

    private fun showStoryImage() {
        currentImageStoryUri?.let {
            binding.imageUpload.setImageURI(it)
        }
    }

    private fun uploadNewStory(token: String, lat: Float?, lon: Float?) {
        currentImageStoryUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val description = binding.edDescription.text.toString()

            postViewModel.uploadStory(token, RequestNewStory(imageFile, description, lat, lon))
                .observe(viewLifecycleOwner) {
                    if (it != null) {
                        when (it) {
                            is Result.Loading -> {
                                showProgressBar(true)
                            }

                            is Result.Success -> {
                                showProgressBar(false)
                                showSnackBar(it.data.message, true)
                                findNavController().navigate(R.id.action_postFragment_to_homeFragment)

                            }

                            is Result.Error -> {
                                showProgressBar(false)
                                showSnackBar(it.error, false)

                            }
                        }
                    }
                }
        }
    }

    private fun showProgressBar(isVisible: Boolean) {
        binding.progressbarUpload.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.buttonUpload.isEnabled = !isVisible
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}