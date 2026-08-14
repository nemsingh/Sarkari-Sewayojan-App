package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        JobOpportunity::class,
        AdmitCardItem::class,
        ExamResultItem::class,
        UserApplication::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SewayojanDatabase : RoomDatabase() {
    abstract fun sewayojanDao(): SewayojanDao

    companion object {
        @Volatile
        private var INSTANCE: SewayojanDatabase? = null

        fun getDatabase(context: Context): SewayojanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SewayojanDatabase::class.java,
                    "sewayojan_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
