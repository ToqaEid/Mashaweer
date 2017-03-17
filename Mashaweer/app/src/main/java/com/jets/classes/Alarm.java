package com.jets.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.jets.mashaweer.ReminderActivity;
import com.jets.mashaweer.TripEditActivity;

/**
 * Created by toqae on 17/03/2017.
 */

public class Alarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();
        Intent editIntent = new Intent(context.getApplicationContext(), ReminderActivity.class);
        editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(editIntent);

    }
}
