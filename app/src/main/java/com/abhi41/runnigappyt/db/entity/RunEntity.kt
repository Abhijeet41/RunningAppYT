package com.abhi41.runnigappyt.db.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhi41.runnigappyt.utils.Constants.TABLE_NAME_RUNNING

@Entity(tableName = TABLE_NAME_RUNNING)
class RunEntity(
    var img: Bitmap? = null,
    var timeStamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}