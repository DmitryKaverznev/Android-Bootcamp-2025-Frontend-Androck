package ru.sicampus.bootcamp2025.domain.profile

class UpdateProfileUseCase(
    private val profileRepo: ProfileRepo
) {
    suspend operator fun invoke(newUser: UpdateUser): Result<Boolean> {
        return profileRepo.update(newUser)
    }
}