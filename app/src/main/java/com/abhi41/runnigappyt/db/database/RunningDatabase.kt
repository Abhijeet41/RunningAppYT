package com.abhi41.runnigappyt.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abhi41.runnigappyt.db.converters.Converters
import com.abhi41.runnigappyt.db.dao.RunningDao
import com.abhi41.runnigappyt.db.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RunningDatabase:RoomDatabase() {

    abstract fun getRunDao(): RunningDao

}