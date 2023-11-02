package com.hilmisatrio.storyku.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.google.android.material.snackbar.Snackbar
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.request.RequestDataRegister
import com.hilmisatrio.storyku.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAnimation()

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo


        binding.buttonSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSignUp.setOnClickListener {
            checkStatus(activeNetwork)
        }
    }

    private fun checkStatus(activeNetwork: NetworkInfo?) {
        if (activeNetwork != null) {
            if (activeNetwork.isConnectedOrConnecting) {
                registerAccount()
            }
        } else {
            showSnackBar(resources.getString(R.string.not_connect), false)
        }
    }


    private fun registerAccount() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            val dataUser = RequestDataRegister(
                name, email, password
            )
            registerViewModel.register(dataUser).observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        is Result.Success -> {
                            showSnackBar(it.data.message, true)
                            showProgressBar(false)
                            clearTextField()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }

                        is Result.Loading -> {
                            showProgressBar(true)
                        }

                        is Result.Error -> {
                            showSnackBar(it.error, false)
                            showProgressBar(false)
                        }
                    }
                }
            }
        } else {
            showSnackBar(resources.getString(R.string.data_not_empty), false)
        }

    }

    private fun clearTextField() {
        binding.edRegisterName.setText("")
        binding.edRegisterEmail.setText("")
        binding.edRegisterPassword.setText("")
    }

    private fun showProgressBar(isVisible: Boolean) {
        binding.progressbarRegist.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.buttonSignUp.isEnabled = !isVisible
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