package com.jets.mashaweer;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by michael on 3/22/17.
 */

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
