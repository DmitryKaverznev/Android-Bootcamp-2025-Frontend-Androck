package ru.sicampus.bootcamp2025.domain.profile

interface ProfileRepo {
    suspend fun update(newUser: UpdateUser): Result<Boolean>
    suspend fun logout(): Result<Boolean>
}