package com.example.smoiapp001.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilModule() {

    @Provides
    @NonNull
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @NonNull
    @Singleton
    internal fun provideDecoration(context: Context): DividerItemDecoration {
        return DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    }

}