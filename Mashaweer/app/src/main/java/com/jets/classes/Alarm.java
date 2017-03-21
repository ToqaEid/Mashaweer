package com.jets.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jets.activites.ReminderActivity;
/**
 * Created by toqae on 17/03/2017.
 */

public class Alarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();
//        Intent editIntent = new Intent(context.getApplicationContext(), TEST.class);
//        editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(editIntent);
        Intent editIntent = new Intent(context.getApplicationContext(), ReminderActivity.class);
        editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.getApplicationContext().startActivity(editIntent);

    }
}
