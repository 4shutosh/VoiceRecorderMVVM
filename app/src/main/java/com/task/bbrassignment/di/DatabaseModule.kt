package com.task.bbrassignment.di

import android.content.Context
import androidx.room.Room
import com.task.bbrassignment.data.AppDatabase
import com.task.bbrassignment.data.AppDatabase.Callback
import com.task.bbrassignment.data.RecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): RecordDao {
        return appDatabase.recordDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: Callback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "records"
        ).fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    }
}