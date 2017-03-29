package com.jets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jets.fragments.PastTripsFragment;
import com.jets.fragments.UpcomingTripsFragment;

/**
 * Created by mohamed on 14/03/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter {

    public static String [] tabNames = {"Upcoming Trips","Past Trips"};
    UpcomingTripsFragment upcomingTripsFragment;
    PastTripsFragment pastTripsFragment;


    public TabsAdapter(FragmentManager fm) {
        super(fm);
        upcomingTripsFragment = new UpcomingTripsFragment();
        pastTripsFragment = new PastTripsFragment();

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return upcomingTripsFragment;

            case 1:
                return pastTripsFragment;
        }

        return null;
    }


    public PastTripsFragment getPastTripsFragment(){
        return pastTripsFragment;
    }
    public UpcomingTripsFragment getUpcomingTripsFragment(){
        return upcomingTripsFragment;
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
