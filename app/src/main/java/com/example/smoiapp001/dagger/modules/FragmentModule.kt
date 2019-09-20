package com.example.smoiapp001.dagger.modules

import androidx.annotation.NonNull
import com.example.smoiapp001.fragments.CurrencyFragment
import com.example.smoiapp001.fragments.DashboardFragment
import com.example.smoiapp001.fragments.MainFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FragmentModule() {

    @Provides
    @NonNull
    @Singleton
    internal fun provideMainFragment(): MainFragment {
        return MainFragment()
    }

    @Provides
    @NonNull
    @Singleton
    internal fun provideDashboardFragment(): DashboardFragment {
        return DashboardFragment()
    }

    @Provides
    @NonNull
    @Singleton
    internal fun provideCurrencyFragment(): CurrencyFragment {
        return CurrencyFragment()
    }
}