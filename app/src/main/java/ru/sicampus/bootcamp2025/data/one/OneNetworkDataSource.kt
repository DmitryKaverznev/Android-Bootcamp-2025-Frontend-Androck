package ru.sicampus.bootcamp2025.data.one

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import ru.sicampus.bootcamp2025.data.Network
import ru.sicampus.bootcamp2025.data.save.AuthStorageDataSource
import java.io.IOException
import java.net.URLEncoder

class OneNetworkDataSource (
    val authStorageDataSource: AuthStorageDataSource
) {
    private val client = Network.client

    suspend fun getAllUsers(): List<UserCenter> {
        val credentials = authStorageDataSource.getCredentials()

        val response = client.get("${Network.SERVER_ADDRESS}/api/users") {
            headers {
                append(
                    HttpHeaders.Authorization,
                    Credentials.basic(credentials.first, credentials.second)
                )
            }
        }

        if (!response.status.isSuccess()) {
            throw IOException("HTTP error: ${response.status}")
        }

        Log.e("AUTH", "Status: ${response.status}")
        Log.e("AUTH", "Body: ${response.body<String>()}")

        return response.body()
    }

    suspend fun registerUserToCenter(centerName: String, dto: AuthRegisterDto): Boolean {
        val credentials = authStorageDataSource.getCredentials()

        val encodedName = withContext(Dispatchers.IO) {
            URLEncoder.encode(centerName, "UTF-8")
        }

        val response = client.put(
            "${Network.SERVER_ADDRESS}/api/users/volunteer/add/${credentials.first}/$encodedName"
        ) {
            headers {
                append(HttpHeaders.ContentType, "application/json")
                append(
                    HttpHeaders.Authorization,
                    Credentials.basic(credentials.first, credentials.second)
                )
            }
        }

        Log.e("AUTH", "Status: ${response.status}")
        Log.e("AUTH", "Body: ${response.body<String>()}")

        return response.status.isSuccess()
    }
}