package ru.sicampus.bootcamp2025.data.list

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import ru.sicampus.bootcamp2025.data.Network.SERVER_ADDRESS
import ru.sicampus.bootcamp2025.data.save.AuthStorageDataSource

class UserNetworkDataSource(
    private val authStorageDataSource: AuthStorageDataSource
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getUsers(
        pageNum: Int,
        pageSize: Int,
    ): Result<ListPagingDto> = withContext(Dispatchers.IO) {
        runCatching {
            val credentials = authStorageDataSource.getCredentials()

            val result = client.get("$SERVER_ADDRESS/api/volunteer/paginated?page=$pageNum&size=$pageSize") {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        Credentials.basic(credentials.first, credentials.second)
                    )
                }
            }

            Log.e("AUTH", "Status: ${result.status}")
            Log.e("AUTH", "Body: ${result.body<String>()}")

            if (result.status != HttpStatusCode.OK) {
                error("Status ${result.status}")
            }
            result.body()
        }
    }
}