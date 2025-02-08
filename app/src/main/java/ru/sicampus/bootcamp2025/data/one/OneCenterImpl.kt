package ru.sicampus.bootcamp2025.data.one

import ru.sicampus.bootcamp2025.domain.one.OneCenterRepository

class OneCenterImpl(
    private val dataSource: OneNetworkDataSource,
    private val centerName: String
) : OneCenterRepository {

    override suspend fun registerToCenter(): Result<Boolean> = runCatching {
        val credentials = dataSource.authStorageDataSource.getCredentials()
        val dto = AuthRegisterDto(
            username = dataSource.authStorageDataSource.getName(),
            name = centerName
        )
        dataSource.registerUserToCenter(centerName, dto)
    }

    override suspend fun getAllUsers(): Result<List<UserCenter>> = runCatching {
        dataSource.getAllUsers()
    }
}