package com.example.smoiapp001.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.smoiapp001.activities.fragments.CurrencyFragment

import com.example.smoiapp001.activities.fragments.DashboardFragment
import com.example.smoiapp001.activities.fragments.MainFragment

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val pageNumber = 3

    override fun getItem(position: Int): Fragment {
        if (position == 0)
            return MainFragment()
        else if (position == 1)
            return DashboardFragment()
        else if (position ==2)
            return CurrencyFragment()
        return MainFragment()
    }

    override fun getCount(): Int {
        return pageNumber
    }

    override fun getPageTitle(position: Int): CharSequence?
        = when (position) {
            0 -> MainFragment.NAME
            1 -> DashboardFragment.NAME
            2 -> CurrencyFragment.NAME
            else -> null
        }

}

