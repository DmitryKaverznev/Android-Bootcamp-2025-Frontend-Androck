package ru.sicampus.bootcamp2025.utils

import android.content.Context
import android.util.Log
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.network.sockets.SocketTimeoutException
import java.io.IOException
import java.net.UnknownHostException
import ru.sicampus.bootcamp2025.R

private const val TAG = "AppError" // Общий тег для всех ошибок

fun Throwable?.toReadableMessage(context: Context): String? {
    if (this == null) return null

    val errorMessage = when (this) {
        is ClientRequestException -> {
            val message = when (response.status.value) {
                401 -> context.getString(R.string.error_unauthorized)
                403 -> context.getString(R.string.error_forbidden)
                404 -> context.getString(R.string.error_not_found)
                in 400..499 -> context.getString(R.string.error_client)
                else -> context.getString(R.string.error_network)
            }
            Log.e(TAG, "Client error: ${response.status.value}", this)
            message
        }
        is ServerResponseException -> {
            Log.e(TAG, "Server error: ${response.status.value}", this)
            context.getString(R.string.error_server)
        }
        is IOException -> when (this) {
            is SocketTimeoutException -> {
                Log.e(TAG, "Timeout error", this)
                context.getString(R.string.error_timeout)
            }
            is UnknownHostException -> {
                Log.e(TAG, "No internet connection", this)
                context.getString(R.string.error_no_internet)
            }
            else -> {
                Log.e(TAG, "Network error", this)
                context.getString(R.string.error_network_general)
            }
        }
        else -> {
            Log.e(TAG, "Unknown error: ${this.javaClass.simpleName}", this)
            context.getString(R.string.error_unknown)
        }
    }

    // Дополнительное логирование полной информации
    Log.d(TAG, "Handled error: ${this.javaClass.name}", this)

    return errorMessage
}