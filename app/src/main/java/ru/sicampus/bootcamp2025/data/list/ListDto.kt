package ru.sicampus.bootcamp2025.data.list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListDto (
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("coordinate_x")
    val coordinateX: String,
    @SerialName("coordinate_y")
    val coordinateY: String
)