package com.example.smoiapp001.dagger.modules

import androidx.annotation.NonNull
import com.example.smoiapp001.factories.CurrencyViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FactoryModule() {
    @Provides
    @NonNull
    @Singleton
    internal fun provideCurrencyViewModelFactory(): CurrencyViewModelFactory {
        return CurrencyViewModelFactory()
    }
}