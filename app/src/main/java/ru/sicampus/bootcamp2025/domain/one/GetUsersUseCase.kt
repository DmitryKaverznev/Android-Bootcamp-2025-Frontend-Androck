// GetUsersUseCase.kt
package ru.sicampus.bootcamp2025.domain.one

import ru.sicampus.bootcamp2025.data.one.UserCenter

class GetUsersUseCase(private val repository: OneCenterRepository) {
    suspend operator fun invoke(): Result<List<UserCenter>> {
        return repository.getAllUsers()
    }
}