package ru.kettuproj.stocks.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StockEntity::class, SettingEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao():StockDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context):AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null) return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stock_database"
                )
                    .allowMainThreadQueries() //Small database, it's okay
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
