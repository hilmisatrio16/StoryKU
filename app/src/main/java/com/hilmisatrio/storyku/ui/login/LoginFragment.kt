package com.hilmisatrio.storyku.ui.login

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.animation.AnimatorSet
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAnimation()

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        binding.buttonLogin.setOnClickListener {

            checkStatus(activeNetwork)

        }

        loginViewModel.getToken().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                if (findNavController().currentDestination?.id != null) {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }
    }

    private fun checkStatus(activeNetwork: NetworkInfo?) {
        if (activeNetwork != null) {
            if (activeNetwork.isConnectedOrConnecting) {
                loginAccount()
            }
        } else {
            showSnackBar(resources.getString(R.string.not_connect), false)
        }
    }

    private fun loginAccount() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginViewModel.login(
                email,
                password
            ).observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        is Result.Success -> {
                            showProgressBar(false)
                            showSnackBar(it.data.message, true)
                            loginViewModel.sessionActive(
                                it.data.loginResult.token,
                                it.data.loginResult.name
                            )
                        }

                        is Result.Loading -> {
                            showProgressBar(true)
                        }

                        is Result.Error -> {
                            showProgressBar(false)
                            showSnackBar(it.error, false)
                        }
                    }
                }
            }
        } else {
            showSnackBar(resources.getString(R.string.data_not_empty), false)
        }

    }

    private fun showProgressBar(isVisible: Boolean) {
        binding.progressbarLogin.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isVisible
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

    private fun setAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvGreeting, View.ALPHA, 1f).setDuration(200)
        val welcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(200)
        val instruction =
            ObjectAnimator.ofFloat(binding.tvInstruction, View.ALPHA, 1f).setDuration(200)
        AnimatorSet().apply {
            playSequentially(greeting, welcome, instruction)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}