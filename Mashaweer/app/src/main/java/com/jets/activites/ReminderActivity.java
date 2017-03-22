package com.jets.activites;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

    private Trip trip;
    int tripId = 1;
    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        Log.i("Tag", "oncreate");
        // vibrate when the activity opens
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
        r.play();


        //check if phone is locked, then open the lock and turn screen light on
        Window window = getWindow();
        if(!window.isActive()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                new TripServices().startTrip(ReminderActivity.this, trip);
                Toast.makeText(ReminderActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
            }
        });
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyLater(tripId);
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

//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Dialog));
//        builder.setMessage("Are you sure you want to exit?");
//                .setCancelable(
//                false).setPositiveButton("Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                }).setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        builder.setPositiveButton("START", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void notifyLater(int notificationId){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ReminderActivity.this);

        builder.setSmallIcon(R.mipmap.ic_launcher);
       // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.journaldev.com/"));
        Intent intent = new Intent(this , TripServices.class);
        intent.putExtra("trip", trip);
        intent.putExtra("context", (Serializable) ReminderActivity.this);
        PendingIntent pendingIntent = PendingIntent.getActivity(ReminderActivity.this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Notifications Title");
        builder.setContentText("Your notification content here.");
        builder.setSubText("Tap to view the website.");
        builder.setLights(0xffffffff, 1000, 200);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        builder.setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());
    }
}
