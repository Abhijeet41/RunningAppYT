package com.abhi41.runnigappyt.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.abhi41.runnigappyt.R
import com.abhi41.runnigappyt.utils.Constants
import com.abhi41.runnigappyt.utils.Constants.ACTION_PAUSE_SERVICE
import com.abhi41.runnigappyt.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.abhi41.runnigappyt.utils.Constants.ACTION_STOP_SERVICE
import com.abhi41.runnigappyt.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.abhi41.runnigappyt.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.abhi41.runnigappyt.utils.Constants.NOTIFICATION_ID
import com.abhi41.runnigappyt.utils.Constants.TIMER_UPDATE_INTERVAL
import com.abhi41.runnigappyt.utils.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    var isFirstRun = true
    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var updateNotificationBuilder: NotificationCompat.Builder

    private val timeRunInSeconds = MutableLiveData<Long>()


    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        //  val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>() to avoid this long name use typealis
    }

    private fun postIntialValues() { //define empty polylines at the beginning
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postIntialValues()
        updateNotificationBuilder = baseNotificationBuilder
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this) { isTracking ->
            updateLocationTracking(isTracking)
            updateNotificationTrackingState(isTracking)
        }
    }

    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postIntialValues()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service ... ")
                        startTimer()
                    }
                    Timber.d("Started or resume service")
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L  //total time run
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L


    private fun startTimer() {
        addEmptyPolyLines()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        var flag: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            flag = PendingIntent.FLAG_IMMUTABLE
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        }
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, flag)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, flag)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //remove all actions from notification
       /* updateNotificationBuilder.javaClass.getDeclaredField("mAction").apply {
            isAccessible = true
            set(updateNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }*/
        if (!serviceKilled){
            updateNotificationBuilder = baseNotificationBuilder.addAction(
                R.drawable.ic_pause_black_24dp,
                notificationActionText,
                pendingIntent
            )
            notificationManager.notify(NOTIFICATION_ID, updateNotificationBuilder.build())
        }
    }

    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermission(this)
            //  && TrackingUtility.hasBackgroundPermission(this)
            ) {
                val request = LocationRequest().apply {
                    interval = Constants.LOCATION_UPDATE_INTERVAL
                    fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("New Location: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos) //add latlong at last index
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyLines() {
        pathPoints.value?.apply {
            add(mutableListOf())
            pathPoints.postValue(this) //because we inside apply (notify fragment the change)
        }
            ?: pathPoints.postValue(mutableListOf(mutableListOf())) //if its null then add first empty polilines
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)
        //addEmptyPolyLines()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        /* we don't need this any more because we implemented this in service module
        val notificationBuild = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running app")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())*/

        timeRunInSeconds.observe(this) {
            if (!serviceKilled){
                var notification = updateNotificationBuilder
                    .setContentText(
                        TrackingUtility.getFormattedStopWatchTime(it * 1000L, includeMillis = false)
                    )
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }
        }
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }
/*
     we don't need this cause we implemented in Service Module
    private fun getMainActivityPendingIntent(): PendingIntent? {
        var flag: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            flag = FLAG_IMMUTABLE
        } else {
            flag = FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).also {
                it.action = ACTION_SHOW_TRACKING_FRAGMENT
            },
            flag
        )
    }
*/

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

}