package com.abhi41.runnigappyt.repository

import com.abhi41.runnigappyt.db.dao.RunningDao
import com.abhi41.runnigappyt.db.entity.RunEntity
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runningDao: RunningDao
) {

    suspend fun insertRun(run: RunEntity) = runningDao.insertRun(run)

    suspend fun deletRun(run: RunEntity) = runningDao.deleteRun(run)

    fun getAllRunsSortedDate() = runningDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runningDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMilis() = runningDao.getAllRunsSortedByTimeInMilis()

    fun getAllRunsSortedByAvgSpeed() = runningDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByColoriesBurned() = runningDao.getAllRunsSortedByColoriesBurned()

    fun getTotalAvgSpeed() = runningDao.getTotalAvgSpeed()

    fun getTotalDistance() = runningDao.getTotalDistance()

    fun getTotalCaloriesBurned() = runningDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runningDao.getTotalTimeInMillis()



}