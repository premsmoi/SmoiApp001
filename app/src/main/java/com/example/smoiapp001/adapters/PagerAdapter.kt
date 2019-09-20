package com.example.smoiapp001.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.smoiapp001.fragments.PagerFragment

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
class PagerAdapter (fm: FragmentManager, fragments: Array<Fragment>) : FragmentStatePagerAdapter(fm) {

    private val fragmentList: Array<Fragment> = fragments

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence?
            = (fragmentList[position] as PagerFragment).getName()

}

