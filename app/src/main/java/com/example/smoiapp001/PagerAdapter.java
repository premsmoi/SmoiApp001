package com.example.smoiapp001;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.smoiapp001.activities.fragments.AllTransactionsFragment;
import com.example.smoiapp001.activities.fragments.DashboardFragment;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class PagerAdapter extends FragmentStatePagerAdapter {

    private final int PAGE_NUMBER = 2;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new DashboardFragment();
        else if(position == 1)
            return new AllTransactionsFragment();
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return DashboardFragment.NAME;
            case 1:
                return AllTransactionsFragment.NAME;
             default:
                 return null;
        }

    }
}

