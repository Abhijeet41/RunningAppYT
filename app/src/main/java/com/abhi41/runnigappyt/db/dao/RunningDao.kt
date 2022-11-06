package com.abhi41.runnigappyt.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhi41.runnigappyt.db.entity.RunEntity

@Dao
interface RunningDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(runEntity: RunEntity)

    @Delete
    suspend fun deleteRun(runEntity: RunEntity)

    @Query("SELECT *  FROM running_table ORDER BY timeStamp DESC") //desc because latest run should on top in list
    fun getAllRunsSortedByDate(): LiveData<List<RunEntity>>

    @Query("SELECT *  FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMilis(): LiveData<List<RunEntity>>

    @Query("SELECT *  FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<RunEntity>>

    @Query("SELECT *  FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<RunEntity>>

    @Query("SELECT *  FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByColoriesBurned(): LiveData<List<RunEntity>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>
}