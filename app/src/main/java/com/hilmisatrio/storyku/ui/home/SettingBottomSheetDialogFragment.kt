package com.hilmisatrio.storyku.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.databinding.FragmentSettingBottomSheetDialogBinding

class SettingBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSettingBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBottomSheetDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setThemeApplication()

        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            homeViewModel.selectTheme(checked)
        }

        binding.buttonLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.buttonLogout.setOnClickListener {
            showDialogConfirmation()
        }

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
                dismiss()
            }
            .show()
    }


    private fun setThemeApplication() {
        homeViewModel.getTheme().observe(viewLifecycleOwner) {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "SettingBottomSheet"
    }


}