package com.jets.constants;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by toqae on 14/03/2017.
 */

public class SharedPreferenceInfo {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String USER_ID = "userID";
    public static final String ALARMS_SET = "alarmSet";


    /***
     * addUserDataToSharedPreference()
     *if the user has authorized access in the first time he logged in then:
     * his username & password will be added in the shared preference for quick login next time
     ***/
    static public void addUserDataToSharedPreference(Context context, String userID) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPreferenceInfo.USER_ID, userID);
        editor.putBoolean(SharedPreferenceInfo.ALARMS_SET, true);
        editor.commit();

    }

    static public String getUserId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(SharedPreferenceInfo.USER_ID, null);
    }

    static public void signOut(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPreferenceInfo.USER_ID);
        editor.remove(ALARMS_SET);
        editor.apply();
    }


}

