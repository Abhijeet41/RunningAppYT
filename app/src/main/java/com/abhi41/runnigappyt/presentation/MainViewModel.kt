package com.abhi41.runnigappyt.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhi41.runnigappyt.db.entity.RunEntity
import com.abhi41.runnigappyt.repository.MainRepository
import com.abhi41.runnigappyt.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val runSortedByDate = mainRepository.getAllRunsSortedDate()
    private val runSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runSortedByColoriesBurned = mainRepository.getAllRunsSortedByColoriesBurned()
    private val runSortedByTimeInMilis = mainRepository.getAllRunsSortedByTimeInMilis()
    private val runSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()


    /* mediator lived data allows to merge several lifedata together and write our
       custom logic for that when we want to emit which king of data
     */

    val runs = MediatorLiveData<List<RunEntity>>()
    val sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate) { result->
            if (sortType == SortType.DATE){
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByDistance) { result->
            if (sortType == SortType.DISTANCE){
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByColoriesBurned) { result->
            if (sortType == SortType.CALORIES_BURNED){
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByTimeInMilis) { result->
            if (sortType == SortType.RUNNING_TIME){
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByAvgSpeed) { result->
            if (sortType == SortType.AVG_SPEED){
                result?.let {
                    runs.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) {
        return when(sortType) {
            SortType.DATE -> runSortedByDate?.value.let { runs.value = it }
            SortType.RUNNING_TIME -> runSortedByTimeInMilis?.value.let { runs.value = it }
            SortType.AVG_SPEED -> runSortedByAvgSpeed?.value.let { runs.value = it }
            SortType.DISTANCE -> runSortedByDistance?.value.let { runs.value = it }
            SortType.CALORIES_BURNED -> runSortedByColoriesBurned?.value.let { runs.value = it }
        }
    }

    fun insertRun(runEntity: RunEntity) = viewModelScope.launch {
        mainRepository.insertRun(runEntity)
    }


}