package ru.sicampus.bootcamp2025.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.data.profile.ProfileRepoImpl
import ru.sicampus.bootcamp2025.databinding.FragmentProfileBinding
import ru.sicampus.bootcamp2025.domain.profile.LogoutUseCase
import ru.sicampus.bootcamp2025.domain.profile.UpdateProfileUseCase

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _viewBinding: FragmentProfileBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.factory(
            updateUseCase = UpdateProfileUseCase(ProfileRepoImpl()),
            logoutUseCase = LogoutUseCase(ProfileRepoImpl())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        enableEditMode(false)
        viewModel.loadProfile()
    }

    private fun setupUI() {
        with(viewBinding) {
            editButton.setOnClickListener { enableEditMode(true) }
            logoutButton.setOnClickListener { viewModel.logout() }
            saveButton.setOnClickListener { saveProfileChanges() }
        }
    }

    private fun enableEditMode(enable: Boolean) {
        with(viewBinding) {
            nameEditText.apply {
                isFocusableInTouchMode = enable
                isFocusable = enable
            }
            loginEditText.apply {
                isFocusableInTouchMode = enable
                isFocusable = enable
            }
            emailEditText.apply {
                isFocusableInTouchMode = enable
                isFocusable = enable
            }

            // Переключение между иконками редактирования и сохранения
            editButton.visibility = if (enable) View.GONE else View.VISIBLE
            saveButton.visibility = if (enable) View.VISIBLE else View.GONE
        }
    }

    private fun saveProfileChanges() {
        val name = viewBinding.nameEditText.text.toString()
        val username = viewBinding.loginEditText.text.toString()
        val email = viewBinding.emailEditText.text.toString()

        viewModel.updateProfile(name, username, email)
        enableEditMode(false)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.profileData.collect { data ->
                    viewBinding.nameEditText.setText(data.name)
                    viewBinding.loginEditText.setText(data.username)
                    viewBinding.emailEditText.setText(data.email)
                }
            }

            launch {
                viewModel.state.collect { state ->
                    when (state) {
                        ProfileViewModel.ProfileState.Loading ->
                            viewBinding.progressBar.visibility = View.VISIBLE

                        ProfileViewModel.ProfileState.Loaded ->
                            viewBinding.progressBar.visibility = View.GONE

                        is ProfileViewModel.ProfileState.Error -> {
                            viewBinding.progressBar.visibility = View.GONE
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            viewModel.loadProfile()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}