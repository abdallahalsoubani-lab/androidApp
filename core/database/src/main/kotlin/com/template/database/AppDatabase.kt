package com.template.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main application database.
 *
 * All entities should be added to the @Database annotation.
 * Increment version when schema changes and provide migration if needed.
 */
@Database(
    entities = [
        // Add entities here
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    // Add DAOs here as abstract properties

    companion object {
        private const val DATABASE_NAME = "app_database"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME,
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
