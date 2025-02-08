package ru.sicampus.bootcamp2025.data.list

import ru.sicampus.bootcamp2025.domain.list.ListEntity
import ru.sicampus.bootcamp2025.domain.list.ListRepo


class ListRepoImpl(
    private val userNetworkDataSource: UserNetworkDataSource
) : ListRepo {
    override suspend fun getUsers(
        pageNum: Int,
        pageSize: Int,
    ): Result<List<ListEntity>> {
        return userNetworkDataSource.getUsers(
            pageNum = pageNum,
            pageSize = pageSize
        ).map { pagingDto ->
            pagingDto.content?.map { dto ->
                ListEntity(
                    name = dto.name,
                    description = dto.description,
                    coordinateX = dto.coordinateX,
                    coordinateY = dto.coordinateY
                )
            } ?: return Result.failure(IllegalStateException("List parse error"))
        }
    }
}