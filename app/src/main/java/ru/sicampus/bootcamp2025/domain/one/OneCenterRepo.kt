package ru.sicampus.bootcamp2025.domain.one

import ru.sicampus.bootcamp2025.data.one.UserCenter

interface OneCenterRepository {
    suspend fun registerToCenter(): Result<Boolean>
    suspend fun getAllUsers(): Result<List<UserCenter>>
}