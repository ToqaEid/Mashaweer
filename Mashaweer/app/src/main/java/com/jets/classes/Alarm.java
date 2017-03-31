package com.jets.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jets.mashaweer.ReminderActivity;
/**
 * Created by toqae on 17/03/2017.
 */

public class Alarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent editIntent = new Intent(context.getApplicationContext(), ReminderActivity.class);
        Trip trip = (Trip) intent.getSerializableExtra("Trip");
        editIntent.putExtra("Trip", trip);
        editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.getApplicationContext().startActivity(editIntent);
    }

    public static void deleteAlarm(){


    }
}
