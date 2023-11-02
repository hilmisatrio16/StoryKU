package com.hilmisatrio.storyku.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.databinding.FragmentHomeBinding
import com.hilmisatrio.storyku.ui.adapter.ListStoriesAdapter
import com.hilmisatrio.storyku.ui.adapter.LoadingStateStoriesAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var listStoriesAdapter: ListStoriesAdapter

    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        setRecycleView()

        if (activeNetwork != null) {
            if (activeNetwork.isConnectedOrConnecting) {
                homeViewModel.getToken().observe(viewLifecycleOwner) {
                    if (it.isNotEmpty()) {
                        showShimmer(true)
                        observerDataHome(it)
                    }
                }

            } else {
                showSnackBar(resources.getString(R.string.not_connect))
            }
        } else {
            homeViewModel.getToken().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    showShimmer(true)
                    observerDataHome(it)
                }
            }
            showSnackBar(resources.getString(R.string.not_connect))
        }

        buttonEventListener()


    }

    private fun buttonEventListener() {
        binding.buttonShowMenu.setOnClickListener {
            showMenu(it)
        }

        binding.buttonAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postFragment)
        }

        binding.buttonMapStories.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
        }

        binding.buttonSetting.setOnClickListener {
            showMenuSetting()
        }
    }

    private fun showMenuSetting() {
        val settingBottomSheet = SettingBottomSheetDialogFragment()
        settingBottomSheet.show(parentFragmentManager, SettingBottomSheetDialogFragment.TAG)
    }

    private fun showMenu(view: View) {
        val popUpMenuHome = PopupMenu(context, view)
        popUpMenuHome.menuInflater.inflate(R.menu.menu_app, popUpMenuHome.menu)

        popUpMenuHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    showDialogConfirmation()
                    true
                }

                else -> false
            }
        }
        popUpMenuHome.show()
    }

    private fun showDialogConfirmation() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.confirm))
            .setMessage(resources.getString(R.string.question_logout))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->

                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                homeViewModel.clearDataSession()
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun observerDataHome(token: String) {
        homeViewModel.getStories(token).observe(viewLifecycleOwner) {
            if (it != null) {
                listStoriesAdapter.submitData(lifecycle, it)
                showShimmer(false)
            }else{
                showSnackBar("Story has empty")
            }
        }

        homeViewModel.getName().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvName.text = it
            }
        }

        homeViewModel.isSessionActive().observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    private fun showShimmer(isVisible: Boolean) {
        if (isVisible) {
            binding.shimmerListStory.startShimmerAnimation()
            binding.rvStory.visibility = View.GONE
            binding.shimmerListStory.visibility = View.VISIBLE
        } else {
            binding.shimmerListStory.stopShimmerAnimation()
            binding.rvStory.visibility = View.VISIBLE
            binding.shimmerListStory.visibility = View.GONE
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

    private fun setRecycleView() {
        listStoriesAdapter = ListStoriesAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listStoriesAdapter.withLoadStateFooter(
                footer = LoadingStateStoriesAdapter {
                    listStoriesAdapter.retry()
                }
            )
            setHasFixedSize(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
