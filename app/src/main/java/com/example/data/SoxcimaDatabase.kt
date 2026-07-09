package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NotaSoxcimaEntity::class, SoxcimaConfigEntity::class, RegistroMemoriaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class SoxcimaDatabase : RoomDatabase() {
    abstract fun soxcimaDao(): SoxcimaDao

    companion object {
        @Volatile
        private var INSTANCE: SoxcimaDatabase? = null

        fun getDatabase(context: Context): SoxcimaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoxcimaDatabase::class.java,
                    "soxcima_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
