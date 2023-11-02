package com.hilmisatrio.storyku.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private val splashScreenViewModel by viewModels<SplashScreenViewModel> {
        SplashScreenViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAnimation()
        setTheme()
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launchWhenResumed {
                checkSession()
            }
        }, 2000)
    }

    private fun setTheme() {
        splashScreenViewModel.getTheme().observe(viewLifecycleOwner) {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun checkSession() {
        splashScreenViewModel.isSessionActive().observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }
        }
    }

    private fun textAnimation() {
        val image = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(400)
        val title = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 1f).setDuration(300)
        AnimatorSet().apply {
            playSequentially(image, title)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}