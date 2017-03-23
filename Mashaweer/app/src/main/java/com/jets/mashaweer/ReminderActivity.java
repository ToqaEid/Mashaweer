package com.jets.mashaweer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jets.classes.Trip;
import com.jets.classes.TripServices;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderActivity extends Activity {

    @BindView(R.id.start_btn)
    Button startBtn;
    @BindView(R.id.later_btn)
    Button laterBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;

    private TextView msg;

    private Trip trip;
    private int tripIdInt;


    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        //check if phone is locked, then open the lock and turn screen light on
        Window window = getWindow();
        if(!window.isActive()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        setActivitySoundAndVibration();

        //get Trip object from intent
        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra("Trip");
        tripIdInt = TripServices.getTripUniqueId(trip.getTripId());


        ////UI Preparation
        msg = (TextView) findViewById(R.id.msg);
        msg.setText("Time To Start Trip : " +trip.getTripTitle());

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                new TripServices().startTrip(trip);
                Toast.makeText(ReminderActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
            }
        });
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyLater(tripIdInt);
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ReminderActivity.this.finishAndRemoveTask();
//                }else{
//                int FINISH_TASK_WITH_ROOT_ACTIVITY = 1;
//                finish(FINISH_TASK_WITH_ROOT_ACTIVITY);
//                }
//                android:autoRemoveFromRecents="true"

                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        notifyLater(tripIdInt);

    }

    private void setActivitySoundAndVibration(){
        // vibrate when the activity opens
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);
        //sound when activity opens
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
        r.play();

    }


    public void notifyLater(int notificationId){

        String START_ACTION = "START_ACTION";
        String CANCEL_ACTION = "CANCEL_ACTION";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ReminderActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent intent = new Intent(this , TripServices.class);
        intent.putExtra("trip", trip);
        intent.setAction(START_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.color.colorAccent, "START", pendingIntent);

        Intent cancelIntent = new Intent(this , TripServices.class);
        cancelIntent.setAction(CANCEL_ACTION);
        cancelIntent.putExtra("trip", trip);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, 0, cancelIntent, 0);

        builder.addAction(R.color.colorAccent, "CANCEL", cancelPendingIntent);

        builder.setOngoing(true);
        builder.setAutoCancel(true);

        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Notifications Title");
        builder.setContentText("Your notification content here.");
        builder.setSubText("Tap to view the website.");

        //set Light and sound of notification
        builder.setLights(0xffffffff, 1000, 200);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());
    }
}
