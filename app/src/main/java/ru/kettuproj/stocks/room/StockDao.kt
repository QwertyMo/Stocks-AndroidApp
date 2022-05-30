package ru.kettuproj.stocks.room

import androidx.room.*

@Dao
interface StockDao {
    @Query("SELECT * FROM StockEntity")
    fun getAll():List<StockEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stock: StockEntity)

    @Delete
    fun delete(stock: StockEntity)

    @Query("SELECT * FROM SettingEntity")
    fun getSettings():List<SettingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(setting: SettingEntity)

    @Delete
    fun delete(setting: SettingEntity)

}