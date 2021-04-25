package com.task.bbrassignment.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // by default coroutine stops when child fails, for which helps SupervisorJob
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


    // could not do this as dao/db can't be accessed on the main thread
//    @Provides
//    @ActivityScoped
//    fun provideRecordListAdapter(dao: RecordDao): RecordListAdapter {
//
//        val list: List<Record> = dao.getAllRecords()
//        return RecordListAdapter(list)
//    }

}
