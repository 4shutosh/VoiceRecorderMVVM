package com.task.bbrassignment.di

import android.app.Activity
import androidx.fragment.app.FragmentManager
import com.task.bbrassignment.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    @ActivityScoped
    fun provideFragmentManager(activity: Activity): FragmentManager {
        val act = activity as MainActivity
        return act.supportFragmentManager
    }

    @Provides
    @ActivityScoped
    fun provideFragmentTitleList(): List<String> {
        return arrayOf("Record", "List").asList()
    }

}