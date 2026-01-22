package com.template.database.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base DAO interface with common database operations.
 */
interface BaseDao<T> {
    /**
     * Insert an entity into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    /**
     * Insert multiple entities into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Update an entity in the database.
     */
    @Update
    suspend fun update(entity: T)

    /**
     * Update multiple entities in the database.
     */
    @Update
    suspend fun updateAll(entities: List<T>)

    /**
     * Delete an entity from the database.
     */
    @Delete
    suspend fun delete(entity: T)

    /**
     * Delete multiple entities from the database.
     */
    @Delete
    suspend fun deleteAll(entities: List<T>)
}
