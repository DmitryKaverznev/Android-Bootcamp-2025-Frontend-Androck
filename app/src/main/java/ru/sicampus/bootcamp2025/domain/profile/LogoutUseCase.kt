package ru.sicampus.bootcamp2025.domain.profile

class LogoutUseCase(
    private val profileRepo: ProfileRepo
) {
    suspend operator fun invoke(): Result<Boolean> {
        return profileRepo.logout()
    }
}