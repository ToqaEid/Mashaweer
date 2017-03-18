package com.jets.mashaweer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jets.classes.Trip;

public class HomeActivity extends AppCompatActivity implements ActionBar.TabListener , Communicator{

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        viewPager = (ViewPager) findViewById(R.id.pager2);
        Log.i("MyTag", String.valueOf(viewPager == null));
        actionBar = getSupportActionBar();
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabsAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



        //////////////// handling add trip buttons' click listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton_addTrip);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, TripAddActivity.class);
                startActivity(intent);

                Toast.makeText(HomeActivity.this, "--- Going to tripAdd ---", Toast.LENGTH_SHORT).show();
            }
        });





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
    public void sendMsg(Trip trip) {

        Toast.makeText(this, "Going To TripDetails", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(),TripDetails.class);
        intent.putExtra("selectedTrip", trip);
        startActivity(intent);

    }
}
