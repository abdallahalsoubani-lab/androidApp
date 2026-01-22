package com.template.database.base

import androidx.room.ColumnInfo

/**
 * Base class for database entities with common fields.
 */
open class BaseEntity(
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
