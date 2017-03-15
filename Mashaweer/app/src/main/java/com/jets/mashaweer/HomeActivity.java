package com.jets.mashaweer;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements ActionBar.TabListener , Communicator{

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ///////// add your tabs
        for(int i=0; i<TabsAdapter.tabNames.length; i++)
        {
            ActionBar.Tab tab = actionBar.newTab().setText( TabsAdapter.tabNames[i].toString() ).setTabListener(this);
            actionBar.addTab(tab);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    //////////////////////// tabs ya basha

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void sendMsg(int tripId) {

        Toast.makeText(this, "Going To "+ tripId +" TripDetails", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(),TripDetails.class);
        intent.putExtra("tripId", tripId);
        startActivity(intent);

    }
}
