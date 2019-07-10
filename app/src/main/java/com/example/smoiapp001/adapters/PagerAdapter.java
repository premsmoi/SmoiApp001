package com.example.smoiapp001.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.smoiapp001.activities.fragments.DashboardFragment;
import com.example.smoiapp001.activities.fragments.MainFragment;

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
            return new MainFragment();
        else if(position == 1)
            return new DashboardFragment();
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
                return MainFragment.NAME;
            case 1:
                return DashboardFragment.NAME;
             default:
                 return null;
        }

    }
}

