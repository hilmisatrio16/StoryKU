package com.hilmisatrio.storyku.ui.detail

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.response.DetailStory
import com.hilmisatrio.storyku.databinding.FragmentDetailBinding
import com.hilmisatrio.storyku.utils.creationDate

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel by viewModels<DetailViewModel> {
        DetailViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        val getIdStory = arguments?.getString(ID_STORY)

        if (activeNetwork != null) {
            if (activeNetwork.isConnectedOrConnecting) {
                if (getIdStory != null) {
                    detailViewModel.getToken().observe(viewLifecycleOwner) {
                        if (it.isNotEmpty()) {
                            observerDataDetail(it, getIdStory)
                        }
                    }
                }
            } else {
                showSnackBar(resources.getString(R.string.not_connect))
            }
        } else {
            showShimmer(true)
            showSnackBar(resources.getString(R.string.not_connect))
        }


        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observerDataDetail(token: String, id: String) {
        detailViewModel.getDetailStory(token, id).observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Result.Success -> {
                        showShimmer(false)
                        showDetailStory(it.data.story)
                    }

                    is Result.Loading -> {
                        showShimmer(true)
                    }

                    is Result.Error -> {
                        showSnackBar(it.toString())
                    }
                }
            }
        }
    }

    private fun showShimmer(isVisible: Boolean) {
        if (isVisible) {
            binding.shimmerDetailStory.startShimmerAnimation()
            binding.layoutDetail.visibility = View.GONE
            binding.shimmerDetailStory.visibility = View.VISIBLE
        } else {
            binding.shimmerDetailStory.stopShimmerAnimation()
            binding.layoutDetail.visibility = View.VISIBLE
            binding.shimmerDetailStory.visibility = View.GONE
        }
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(
            requireActivity().window.decorView.rootView, message, Snackbar.LENGTH_SHORT
        )

        snackbar.setBackgroundTint(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.red_light
            )
        )
        snackbar.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.white
            )
        )
        snackbar.show()
    }

    private fun showDetailStory(dataStory: DetailStory) {
        with(binding) {
            Glide.with(requireActivity()).load(dataStory.photoUrl).into(this.imageContent)
            this.tvNameUser.text = dataStory.name
            this.tvDateCreateStory.text = dataStory.createdAt.creationDate()
            this.tvContentStory.text = dataStory.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ID_STORY = "id_story"
    }
}