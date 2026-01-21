package com.template.feature.template.data.repository

import com.template.common.Result
import com.template.feature.template.domain.model.TemplateItem
import com.template.feature.template.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Implementation of [TemplateRepository].
 * Currently using in-memory storage; replace with actual API/database calls.
 */
class TemplateRepositoryImpl @Inject constructor() : TemplateRepository {
    private val items = mutableListOf<TemplateItem>()

    init {
        // Add mock data
        repeat(10) { index ->
            items.add(
                TemplateItem(
                    id = "item_$index",
                    title = "Template Item $index",
                    description = "Description for item $index",
                )
            )
        }
    }

    override fun getAllItems(page: Int, pageSize: Int): Flow<Result<List<TemplateItem>>> {
        return flowOf(Result.Success(items.toList()))
    }

    override suspend fun getItem(id: String): Result<TemplateItem> {
        return try {
            val item = items.find { it.id == id }
            if (item != null) {
                Result.Success(item)
            } else {
                Result.Error(Exception("Item not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createItem(title: String, description: String): Result<TemplateItem> {
        return try {
            val newItem = TemplateItem(
                id = "item_${System.currentTimeMillis()}",
                title = title,
                description = description,
            )
            items.add(newItem)
            Result.Success(newItem)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateItem(
        id: String,
        title: String,
        description: String,
    ): Result<TemplateItem> {
        return try {
            val index = items.indexOfFirst { it.id == id }
            if (index != -1) {
                val updatedItem = items[index].copy(
                    title = title,
                    description = description,
                    updatedAt = System.currentTimeMillis(),
                )
                items[index] = updatedItem
                Result.Success(updatedItem)
            } else {
                Result.Error(Exception("Item not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteItem(id: String): Result<Unit> {
        return try {
            items.removeAll { it.id == id }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun searchItems(query: String): Flow<Result<List<TemplateItem>>> {
        return flowOf(
            Result.Success(
                items.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                }
            )
        )
    }
}
