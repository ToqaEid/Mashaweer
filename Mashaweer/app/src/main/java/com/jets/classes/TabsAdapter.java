package com.jets.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jets.mashaweer.PastTripsFragment;
import com.jets.mashaweer.UpcomingTripsFragment;

/**
 * Created by mohamed on 14/03/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter {

    public static String [] tabNames = {"Upcoming Trips","Past Trips"};


    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new UpcomingTripsFragment();

            case 1:
                return new PastTripsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    public static String[] getTabNames() {
        return tabNames;
    }

    public static void setTabNames(String[] tabNames) {
        TabsAdapter.tabNames = tabNames;
    }
}
