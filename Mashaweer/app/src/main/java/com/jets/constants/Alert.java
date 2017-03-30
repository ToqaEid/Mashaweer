package com.jets.constants;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.mashaweer.HomeActivity;
import com.jets.mashaweer.TripDetailsActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by toqae on 14/03/2017.
 */

public class Alert {

    public static void showErrorMsg(String title, String msg, Context context) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void showConfimDeleteDialog(final Context context, final Trip trip) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Delete Trip");
        alertDialog.setMessage("Are you sure you want to delete "+trip.getTripTitle()+" ?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Deleting alarm
                TripServices.deleteAlarm(context, trip);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + SharedPreferenceInfo.getUserId(getApplicationContext()) + "/trips");
                db.child(trip.getTripId()).removeValue();
                if(context instanceof Activity) {
                    if (context instanceof TripDetailsActivity)
                        ((Activity) context).finish();
                }


            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }




}
