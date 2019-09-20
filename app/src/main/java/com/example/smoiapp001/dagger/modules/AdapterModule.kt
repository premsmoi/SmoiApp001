package com.example.smoiapp001.dagger.modules

import android.content.Context
import androidx.annotation.NonNull
import com.example.smoiapp001.adapters.CurrencyAdapter
import com.example.smoiapp001.adapters.TransactionAdapter
import com.example.smoiapp001.fragments.MainFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AdapterModule() {
    @Provides
    @NonNull
    @Singleton
    internal fun provideCurrencyAdapter(context: Context): CurrencyAdapter {
        return CurrencyAdapter(context)
    }

    @Provides
    @NonNull
    @Singleton
    internal fun provideTransactionAdapter(
            context: Context,
            mainFragment: MainFragment): TransactionAdapter {
        return TransactionAdapter(context, mainFragment as TransactionAdapter.ItemClickListener)
    }
}