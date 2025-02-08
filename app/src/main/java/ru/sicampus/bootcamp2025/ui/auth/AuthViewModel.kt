package ru.sicampus.bootcamp2025.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.data.auth.AuthNetworkDataSource
import ru.sicampus.bootcamp2025.data.auth.AuthRepoImpl
import ru.sicampus.bootcamp2025.data.save.AuthStorageDataSource
import ru.sicampus.bootcamp2025.domain.auth.AutoLoginUseCase
import ru.sicampus.bootcamp2025.domain.auth.IsUserExistUseCase
import ru.sicampus.bootcamp2025.domain.auth.LoginUseCase
import ru.sicampus.bootcamp2025.domain.auth.RegisterUserUseCase
import ru.sicampus.bootcamp2025.utils.toReadableMessage

class AuthViewModel(
    application: Application,
    private val isUserExistUseCase: IsUserExistUseCase,
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val authStorageDataSource: AuthStorageDataSource
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(getStateShow())
    val state = _state.asStateFlow()

    private var isNewUser: Boolean? = null

    fun changeLogin() {
        viewModelScope.launch {
            isNewUser = null
            updateState()
        }
    }

    fun clickNext(login: String, password: String, name: String, email: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            when (isNewUser) {
                true -> handleRegistration(login, password, name, email)
                false -> handleLogin(login, password)
                null -> checkUserExistence(login)
            }
        }
    }

    private suspend fun handleRegistration(login: String, password: String, name: String, email: String) {
        registerUserUseCase(login, password, name, email).fold(
            onSuccess = { openList() },
            onFailure = { updateState(it) }
        )
    }

    private suspend fun handleLogin(login: String, password: String) {
        loginUseCase(login, password).fold(
            onSuccess = { openList() },
            onFailure = { updateState(it) }
        )
    }

    private suspend fun checkUserExistence(login: String) {
        isUserExistUseCase(login).fold(
            onSuccess = { isExist ->
                isNewUser = !isExist
                updateState()
            },
            onFailure = { updateState(it) }
        )
    }

    private fun openList() {
        viewModelScope.launch {
            _state.value = getStateShow().copy(navigateToList = true)
        }
    }

    private fun updateState(error: Throwable? = null) {
        _state.value = getStateShow(error)
    }

    private fun getStateShow(error: Throwable? = null): State.Show {
        return State.Show(
            titleText = when (isNewUser) {
                true -> getApplication<Application>().getString(R.string.button_register)
                false -> getApplication<Application>().getString(R.string.auth_login_button)
                null -> getApplication<Application>().getString(R.string.common_welcome)
            },
            showPassword = isNewUser != null,
            showRegistrationFields = isNewUser == true,
            buttonText = when (isNewUser) {
                true -> getApplication<Application>().getString(R.string.button_register)
                false -> getApplication<Application>().getString(R.string.auth_login_button)
                null -> getApplication<Application>().getString(R.string.auth_next_button)
            },
            errorText = error?.toReadableMessage(getApplication()),
            navigateToList = false
        )
    }

    fun checkAutoLogin() {
        viewModelScope.launch {
            if (authStorageDataSource.isLoggedIn()) {
                autoLoginUseCase().fold(
                    onSuccess = { openList() },
                    onFailure = {
                        authStorageDataSource.clear()
                        updateState(it)
                    }
                )
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data class Show(
            val titleText: String,
            val showPassword: Boolean,
            val showRegistrationFields: Boolean,
            val buttonText: String,
            val errorText: String?,
            val navigateToList: Boolean
        ) : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!

                val authRepo = AuthRepoImpl(
                    AuthNetworkDataSource,
                    AuthStorageDataSource()
                )

                return AuthViewModel(
                    application,
                    IsUserExistUseCase(authRepo),
                    LoginUseCase(authRepo),
                    RegisterUserUseCase(authRepo),
                    AutoLoginUseCase(authRepo),
                    AuthStorageDataSource()
                ) as T
            }
        }
    }
}