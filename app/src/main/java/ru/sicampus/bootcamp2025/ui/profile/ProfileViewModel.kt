package ru.sicampus.bootcamp2025.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.domain.profile.LogoutUseCase
import ru.sicampus.bootcamp2025.domain.profile.UpdateProfileUseCase
import ru.sicampus.bootcamp2025.domain.profile.UpdateUser
import ru.sicampus.bootcamp2025.utils.toReadableMessage

class ProfileViewModel(
    application: Application,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state = _state.asStateFlow()

    private val _profileData = MutableStateFlow(ProfileData())
    val profileData = _profileData.asStateFlow()

    // Новый метод для загрузки заготовленных данных профиля
    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            // Здесь можно заменить на реальный вызов к репозиторию
            // Для примера, используем заготовленные данные
            val sampleProfile = ProfileData(
                name = "Иван Иванов",
                username = "ivan_ivanov",
                email = "ivan@example.com"
            )
            _profileData.value = sampleProfile
            _state.value = ProfileState.Loaded
        }
    }

    fun updateProfile(name: String, username: String, email: String) {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val updateUser  = UpdateUser (name, username, email)
            updateProfileUseCase(updateUser ).fold(
                onSuccess = {
                    _profileData.value = ProfileData(name, username, email)
                    _state.value = ProfileState.Loaded
                },
                onFailure = { error ->
                    _state.value = ProfileState.Error(
                        error.toReadableMessage(getApplication()).toString()
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().fold(
                onSuccess = { /* Обработка успешного выхода */ },
                onFailure = { error ->
                    _state.value = ProfileState.Error(
                        error.toReadableMessage(getApplication()).toString()
                    )
                }
            )
        }
    }

    data class ProfileData(
        val name: String = "",
        val username: String = "",
        val email: String = ""
    )

    sealed interface ProfileState {
        data object Loading : ProfileState
        data object Loaded : ProfileState
        data class Error(val message: String) : ProfileState
    }

    companion object {
        fun factory(
            updateUseCase: UpdateProfileUseCase,
            logoutUseCase: LogoutUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                return ProfileViewModel(
                    application = application,
                    updateProfileUseCase = updateUseCase,
                    logoutUseCase = logoutUseCase
                ) as T
            }
        }
    }
}