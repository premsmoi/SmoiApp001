package com.example.smoiapp001

import android.app.Application
import com.example.smoiapp001.dagger.AppComponent
import com.example.smoiapp001.dagger.DaggerAppComponent
import com.example.smoiapp001.dagger.modules.*
import timber.log.Timber

class MyApplication : Application() {

    companion object {
        lateinit var mAppComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            println("DEBUG!")
            Timber.plant(Timber.DebugTree())
        }

        //Instantiating the component
        //DaggerUtilComponent will show up after build
        mAppComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .fragmentModule(FragmentModule())
                .utilModule(UtilModule())
                .databaseModule(DatabaseModule())
                .adapterModule(AdapterModule())
                .factoryModule(FactoryModule())
                .build()

    }

}
