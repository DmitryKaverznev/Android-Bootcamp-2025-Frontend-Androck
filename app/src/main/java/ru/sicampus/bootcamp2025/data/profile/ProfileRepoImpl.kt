package ru.sicampus.bootcamp2025.data.profile

import ru.sicampus.bootcamp2025.domain.profile.ProfileRepo
import ru.sicampus.bootcamp2025.domain.profile.UpdateUser

class ProfileRepoImpl : ProfileRepo {
    override suspend fun update(newUser: UpdateUser): Result<Boolean> {
        return Result.success(true) //TODO
    }

    override suspend fun logout(): Result<Boolean> {
        return Result.success(true) //TODO
    }
}