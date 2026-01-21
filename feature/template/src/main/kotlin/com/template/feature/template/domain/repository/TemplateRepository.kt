package com.template.feature.template.domain.repository

import com.template.feature.template.domain.model.TemplateItem
import com.template.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for template items CRUD operations.
 */
interface TemplateRepository {
    /**
     * Get all template items with pagination.
     */
    fun getAllItems(page: Int = 1, pageSize: Int = 20): Flow<Result<List<TemplateItem>>>

    /**
     * Get a single template item by ID.
     */
    suspend fun getItem(id: String): Result<TemplateItem>

    /**
     * Create a new template item.
     */
    suspend fun createItem(title: String, description: String): Result<TemplateItem>

    /**
     * Update an existing template item.
     */
    suspend fun updateItem(
        id: String,
        title: String,
        description: String,
    ): Result<TemplateItem>

    /**
     * Delete a template item.
     */
    suspend fun deleteItem(id: String): Result<Unit>

    /**
     * Search template items.
     */
    fun searchItems(query: String): Flow<Result<List<TemplateItem>>>
}
