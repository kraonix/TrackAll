package com.example.trackall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trackall.data.dao.ExpenseDao
import com.example.trackall.data.dao.UserDao
import com.example.trackall.data.entity.Expense
import com.example.trackall.data.entity.User

@Database(
    entities = [Expense::class, User::class],
    version = 4,
    exportSchema = false
)
abstract class TrackAllDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: TrackAllDatabase? = null

        fun getDatabase(context: Context): TrackAllDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackAllDatabase::class.java,
                    "track_all_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
