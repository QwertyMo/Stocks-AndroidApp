package ru.kettuproj.stocks.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingEntity(
    @PrimaryKey val setting: String,
    @ColumnInfo(name = "value") val value: String,
)

