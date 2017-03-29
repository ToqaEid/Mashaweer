package com.jets.mashaweer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
    @BindView(R.id.reminder_tool)
    Toolbar toolbar;

    private TextView msg;

    private Trip trip;
    private int tripIdInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.setActionBar(toolbar);
        }

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

                new TripServices().startTrip(trip);
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

//    @Override
//    protected void onPause() {
//        super.onPause();
//        notifyLater(tripIdInt);
//        finish();
//    }

    /*======================== HELPFUL FUNCTIONS ======================================*/
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
        String DETAIL_ACTION = "DETAIL_ACTION";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ReminderActivity.this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        builder.setSmallIcon(R.drawable.logo);

        Intent intent = new Intent(this , TripServices.class);
        intent.setAction(START_ACTION);
        intent.putExtra("trip", trip);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.start2, "START", pendingIntent);

        Intent cancelIntent = new Intent(this , TripServices.class);
        cancelIntent.setAction(CANCEL_ACTION);
        cancelIntent.putExtra("trip", trip);
        cancelIntent.putExtra("notificationId", notificationId);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, notificationId, cancelIntent, 0);
        builder.addAction(R.drawable.cancel1, "CANCEL", cancelPendingIntent);

        builder.setOngoing(true);
        builder.setAutoCancel(true);

        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Mashaweer");
        builder.setContentText(trip.getTripTitle() + " is postponed");
        builder.setSubText("Click to Start Trip Now");

        //set Light and sound of notification
        builder.setLights(0xffffffff, 1000, 200);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());

    }

    /*=============================== MENU =====================================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        new MenuInflater(getApplication()).inflate(R.menu.menu_reminder, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_notes:
                Intent intent = new Intent(ReminderActivity.this, ReminderNotesActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

            default:
                break;
        }

        return true;
    }
}
