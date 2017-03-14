package com.jets.mashaweer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jets.constants.SharedPreferenceInfo;


public class SplashScreenAvtivity extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_avtivity);
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
        String authorized = sharedPreferences.getString(SharedPreferenceInfo.LOGIN_KEY, null);
        if (authorized == null) {
            intent = new Intent(SplashScreenAvtivity.this, LoginActivity.class);
        }else{
            intent = new Intent(SplashScreenAvtivity.this, HomeActivity.class);
        }
        startActivity(intent);
    }
}
