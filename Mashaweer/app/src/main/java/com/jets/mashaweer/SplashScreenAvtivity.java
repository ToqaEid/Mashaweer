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

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferenceInfo.addUserDataToSharedPreference(getApplicationContext(), "9zSBz74aX4NmnTQsoJvuslfpsIu2");
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
}
