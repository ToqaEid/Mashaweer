package com.jets.mashaweer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.jets.constants.SharedPreferenceInfo;


public class SplashScreenAvtivity extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_avtivity);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
//
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
                String authorized = sharedPreferences.getString(SharedPreferenceInfo.USER_ID, null);

                System.out.println(authorized);

                if (authorized == null) {
                    intent = new Intent(SplashScreenAvtivity.this, LoginActivity.class);

                }else{
                    intent = new Intent(SplashScreenAvtivity.this, HomeActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
//        String authorized = sharedPreferences.getString(SharedPreferenceInfo.USER_ID, null);
//
//        System.out.println(authorized);
//
//        if (authorized == null) {
//            intent = new Intent(SplashScreenAvtivity.this, LoginActivity.class);
//
//        }else{
//            intent = new Intent(SplashScreenAvtivity.this, HomeActivity.class);
//        }
//        startActivity(intent);
//        finish();
    }
}
