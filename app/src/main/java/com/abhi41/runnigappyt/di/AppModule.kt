package com.abhi41.runnigappyt.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.abhi41.runnigappyt.db.dao.RunningDao
import com.abhi41.runnigappyt.db.database.RunningDatabase
import com.abhi41.runnigappyt.utils.Constants.DATABASE_NAME_RUNNING
import com.abhi41.runnigappyt.utils.Constants.KEY_FIRST_TIME_TOGGLE
import com.abhi41.runnigappyt.utils.Constants.KEY_NAME
import com.abhi41.runnigappyt.utils.Constants.KEY_WEIGHT
import com.abhi41.runnigappyt.utils.Constants.SHARED_PREFERECNCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunningDatabase::class.java,
        DATABASE_NAME_RUNNING
    ).build()

    @Provides
    @Singleton
    fun provideRunDao(db: RunningDatabase): RunningDao {
        return db.getRunDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFERECNCES_NAME, MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideName(sharedPref: SharedPreferences): String {
        return sharedPref.getString(KEY_NAME, "") ?: ""
    }

    @Provides
    @Singleton
    fun provideWeight(sharedPref: SharedPreferences): Float {
        return sharedPref.getFloat(KEY_WEIGHT, 80f)
    }

    @Provides
    @Singleton
    fun provideFirstTimeToggle(sharedPref: SharedPreferences): Boolean {
        return sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
    }

}