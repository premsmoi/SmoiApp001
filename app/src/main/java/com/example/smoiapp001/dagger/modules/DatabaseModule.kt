package com.example.smoiapp001.dagger.modules

import android.content.Context
import androidx.annotation.NonNull
import com.example.smoiapp001.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule() {
    @Provides
    @NonNull
    @Singleton
    internal fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}