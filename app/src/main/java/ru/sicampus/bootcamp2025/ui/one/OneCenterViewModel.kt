// OneCenterViewModel.kt
package ru.sicampus.bootcamp2025.ui.one

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
import ru.sicampus.bootcamp2025.domain.one.OneCenter
import ru.sicampus.bootcamp2025.data.one.UserCenter
import ru.sicampus.bootcamp2025.domain.one.GetUsersUseCase
import ru.sicampus.bootcamp2025.domain.one.RegisterUserUseCase
import ru.sicampus.bootcamp2025.utils.toReadableMessage

class OneCenterViewModel(
    application: Application,
    private val oneCenter: OneCenter, // Обязательный параметр
    private val getUsersUseCase: GetUsersUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    sealed class State {
        data object Loading : State()
        data class Loaded(val center: OneCenter, val volunteers: List<UserCenter>) : State()
        data class Error(val message: String) : State()
    }

    fun registerUser() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                // Явное приведение типа для userId
                val success = registerUserUseCase().getOrThrow()
                if (success) loadUsers()
                else _state.value = State.Error(getApplication<Application>().getString(R.string.common_refresh))
            } catch (e: Exception) {
                _state.value = State.Error(e.toReadableMessage(getApplication()).toString())
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val users = getUsersUseCase().getOrThrow()
                _state.value = State.Loaded(oneCenter, users)
            } catch (e: Exception) {
                _state.value = State.Error(e.toReadableMessage(getApplication()).toString())
            }
        }
    }

    companion object {
        fun factory(
            oneCenter: OneCenter, // Обязательная передача центра
            getUsersUseCase: GetUsersUseCase,
            registerUserUseCase: RegisterUserUseCase
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                return OneCenterViewModel(
                    application = app,
                    oneCenter = oneCenter, // Явная передача параметра
                    getUsersUseCase = getUsersUseCase,
                    registerUserUseCase = registerUserUseCase
                ) as T
            }
        }
    }
}