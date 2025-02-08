package ru.sicampus.bootcamp2025.domain.profile

interface AuthRepository {
    suspend fun isUserExist(login: String): Result<Boolean>
    suspend fun login(login: String, password: String): Result<Unit>
    suspend fun registerUser(login: String, password: String, name: String, email: String): Result<Unit>
    suspend fun autoLogin(): Result<Unit>
}