package com.example.smoiapp001.dagger

import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.smoiapp001.MyApplication
import com.example.smoiapp001.activities.MainActivity
import com.example.smoiapp001.dagger.modules.*
import com.example.smoiapp001.fragments.CurrencyFragment
import com.example.smoiapp001.fragments.MainFragment
import com.example.smoiapp001.viewmodels.MainViewModel
import dagger.Component
import javax.inject.Singleton

// AppComponent.kt
@Singleton
@Component(
        modules = [
            FragmentModule::class,
            UtilModule::class,
            AppModule::class,
            DatabaseModule::class,
            AdapterModule::class,
            FactoryModule::class
        ])
interface AppComponent {

    fun inject(myApplication: MyApplication)

    fun inject(mainActivity: MainActivity)

    fun inject(mainFragment: MainFragment)

    fun inject(currencyFragment: CurrencyFragment)

    fun inject(mainViewModel: MainViewModel)

    fun inject(pagerAdapter: FragmentStatePagerAdapter)



}