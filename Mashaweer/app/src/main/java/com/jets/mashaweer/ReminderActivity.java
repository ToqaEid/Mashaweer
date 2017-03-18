package com.jets.mashaweer;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ReminderActivity extends Activity {
    private Vibrator vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);
        Window window = getWindow();
        if(!window.isActive()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
    }
}
