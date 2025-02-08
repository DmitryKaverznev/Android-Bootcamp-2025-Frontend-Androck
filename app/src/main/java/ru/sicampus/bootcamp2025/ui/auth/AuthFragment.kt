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
import ru.sicampus.bootcamp2025.ui.one.OneCenterFragment
import ru.sicampus.bootcamp2025.utils.collectWithLifecycle

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private var _viewBinding: FragmentAuthBinding? = null
    private val viewBinding: FragmentAuthBinding get() = _viewBinding!!

    private val viewModel by viewModels<AuthViewModel> { AuthViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _viewBinding = FragmentAuthBinding.bind(view)

        viewModel.checkAutoLogin()

        viewBinding.next.setOnClickListener {
            viewModel.clickNext(
                viewBinding.loginEditText.text.toString(),
                viewBinding.passwordEditText.text.toString(),
                viewBinding.nameEditText.text.toString(),
                viewBinding.emailEditText.text.toString()
            )
        }

        viewBinding.loginEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.changeLogin()
            }
        })

        viewModel.state.collectWithLifecycle(this) { state ->
            when (state) {
                is AuthViewModel.State.Loading -> {
                    viewBinding.next.isEnabled = false
                    viewBinding.nameInputLayout.isEnabled = false
                    viewBinding.emailInputLayout.isEnabled = false
                    viewBinding.passwordInputLayout.isEnabled = false
                }
                is AuthViewModel.State.Show -> {
                    viewBinding.next.isEnabled = true
                    viewBinding.nameInputLayout.isEnabled = true
                    viewBinding.emailInputLayout.isEnabled = true
                    viewBinding.passwordInputLayout.isEnabled = true

                    viewBinding.title.text = state.titleText
                    viewBinding.next.text = state.buttonText
                    viewBinding.errorMessage.text = state.errorText
                    viewBinding.errorMessage.visibility =
                        if (state.errorText != null) View.VISIBLE else View.GONE
                    viewBinding.passwordInputLayout.visibility =
                        if (state.showPassword) View.VISIBLE else View.GONE

                    if (state.navigateToList) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main, MainScreenFragment())
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }
}