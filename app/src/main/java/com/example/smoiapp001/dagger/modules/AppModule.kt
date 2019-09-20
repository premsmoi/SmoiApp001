package com.example.smoiapp001.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(internal var context: Context) {
    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return context
    }

}