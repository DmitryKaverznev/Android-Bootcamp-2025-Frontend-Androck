package ru.sicampus.bootcamp2025.data.one

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRegisterDto(
    @SerialName("username")
    val username: String,
    @SerialName("name")
    val name: String,
)
