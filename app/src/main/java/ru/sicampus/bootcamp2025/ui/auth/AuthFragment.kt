package ru.sicampus.bootcamp2025.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.FragmentAuthBinding
import ru.sicampus.bootcamp2025.ui.mainscreen.MainScreenFragment
import ru.sicampus.bootcamp2025.utils.collectWithLifecycle

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private var _viewBinding: FragmentAuthBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val viewModel by viewModels<AuthViewModel> { AuthViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewBinding = FragmentAuthBinding.bind(view)

        viewModel.checkAutoLogin()

        with(viewBinding) {
            next.setOnClickListener {
                viewModel.clickNext(
                    loginEditText.text.toString(),
                    passwordEditText.text.toString(),
                    nameEditText.text.toString(),
                    emailEditText.text.toString()
                )
            }

            loginEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    viewModel.changeLogin()
                }
            })
        }

        viewModel.state.collectWithLifecycle(this) { state ->
            when (state) {
                is AuthViewModel.State.Loading -> handleLoadingState()
                is AuthViewModel.State.Show -> handleShowState(state)
            }
        }
    }

    private fun handleLoadingState() {
        with(viewBinding) {
            next.isEnabled = false
            loginInputLayout.isEnabled = false
            passwordInputLayout.isEnabled = false
            nameInputLayout.isEnabled = false
            emailInputLayout.isEnabled = false
        }
    }

    private fun handleShowState(state: AuthViewModel.State.Show) {
        with(viewBinding) {
            if (passwordInputLayout.visibility == View.VISIBLE && !state.showPassword) {
                passwordEditText.text?.clear()
            }

            if (nameInputLayout.visibility == View.VISIBLE && !state.showRegistrationFields) {
                nameEditText.text?.clear()
                emailEditText.text?.clear()
            }

            next.isEnabled = true
            loginInputLayout.isEnabled = true
            passwordInputLayout.isEnabled = state.showPassword
            nameInputLayout.isEnabled = state.showRegistrationFields
            emailInputLayout.isEnabled = state.showRegistrationFields

            title.text = state.titleText
            next.text = state.buttonText
            errorMessage.text = state.errorText
            errorMessage.visibility = if (state.errorText != null) View.VISIBLE else View.GONE

            passwordInputLayout.visibility = if (state.showPassword) View.VISIBLE else View.GONE.also {
                passwordEditText.text?.clear()
            }

            nameInputLayout.visibility = if (state.showRegistrationFields) View.VISIBLE else View.GONE.also {
                nameEditText.text?.clear()
            }

            emailInputLayout.visibility = if (state.showRegistrationFields) View.VISIBLE else View.GONE.also {
                emailEditText.text?.clear()
            }

            if (state.navigateToList) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, MainScreenFragment())
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}