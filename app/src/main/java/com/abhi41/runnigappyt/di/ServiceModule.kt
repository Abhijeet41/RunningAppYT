package com.abhi41.runnigappyt.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.abhi41.runnigappyt.R
import com.abhi41.runnigappyt.presentation.MainActivity
import com.abhi41.runnigappyt.utils.Constants
import com.abhi41.runnigappyt.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module // we need this injuction as long as service lived not as long as application lives
@InstallIn(ServiceComponent::class) //so instead of SingletonComponent use ServiceComponent
object ServiceModule {

/* we can also write like this
  @ServiceScoped
  @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)*/

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ): FusedLocationProviderClient {
        return FusedLocationProviderClient(app)
    }

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent( //this pending inttent provide to notification builder
        @ApplicationContext app: Context
    ): PendingIntent {
        var flag: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            flag = PendingIntent.FLAG_IMMUTABLE
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            app,
            0,
            Intent(app, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
            flag
        )
    }

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Running app")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

}