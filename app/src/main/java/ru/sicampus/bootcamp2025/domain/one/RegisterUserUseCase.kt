package ru.sicampus.bootcamp2025.domain.one

class RegisterUserUseCase(private val repository: OneCenterRepository) {
    suspend operator fun invoke(): Result<Boolean> {
        return repository.registerToCenter()
    }
}