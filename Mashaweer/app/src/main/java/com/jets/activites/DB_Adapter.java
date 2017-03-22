package com.jets.activites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jets.classes.Trip;

import static com.jets.activites.DB_Adapter.DB_Helper.COL_1_10;

/**
 * Created by mohamed on 16/03/2017.
 */

public class DB_Adapter {


    DB_Helper helper;
    Context context;
    SQLiteDatabase sqLiteDatabase;


    public DB_Adapter (Context context)
    {
        this.context = context;
        this.helper = new DB_Helper(context);
    }

    public boolean insertTripInfo (Trip trip){

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DB_Helper.COL_1_2, trip.getTripTitle() );
        contentValues.put(DB_Helper.COL_1_3, trip.getTripNotes());
        contentValues.put(DB_Helper.COL_1_4, trip.getTripStartLongLat());
        contentValues.put(DB_Helper.COL_1_5, trip.getTripStartLocation());
        contentValues.put(DB_Helper.COL_1_6, trip.getTripEndLongLat());
        contentValues.put(DB_Helper.COL_1_7, trip.getTripEndLocation());
        contentValues.put(DB_Helper.COL_1_8, trip.getTripDateTime());
        contentValues.put(DB_Helper.COL_1_9, trip.getTripType());
        contentValues.put(COL_1_10, trip.getTripStatus());

        long result = db.insert(DB_Helper.TABLE_NAME_1,null,contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }



    public Cursor getAllTrips (){

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_Helper.TABLE_NAME_1 , null);

        return cursor;

    }


    public Cursor getComingTrips (){

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_Helper.TABLE_NAME_1 + " WHERE "+ COL_1_10 + "= 0" ,null);

        return cursor;

    }

    public Cursor getPastTrips (){

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+DB_Helper.TABLE_NAME_1 + " WHERE "+ COL_1_10 + "= 1"   ,null);

        return cursor;

    }

    public Cursor getSpecificTrip (int tripId){

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_Helper.TABLE_NAME_1 + " WHERE ID=" + tripId,null);

        return cursor;

    }




    static class DB_Helper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "TripzSchema";

        public static final String TABLE_NAME_1 = "Trip";
        public static final String COL_1_1 = "ID";
        public static final String COL_1_2 = "TITLE";
        public static final String COL_1_3 = "NOTES";
        public static final String COL_1_4 = "START_LONG";
        public static final String COL_1_5 = "START_LAT";
        public static final String COL_1_6 = "END_LONG";
        public static final String COL_1_7 = "END_LAT";
        public static final String COL_1_8 = "TIME_DATE";
        public static final String COL_1_9 = "tripType"; //// round_Trip =1 or one_way=0
        public static final String COL_1_10 = "tripStatus";  //////// Upcoming = 1 or Done = -1

        public static final String TABLE_NAME_2 = "TripUpdates";
        public static final String COL_2_1 = "TRIP_ID";
        public static final String COL_2_2 = "TRIP_ACTION";


        DB_Helper (Context context)
        {
            super(context, DATABASE_NAME,null, 1);
            Log.i("MyTag","DBHelper is Constructed");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("MyTag","DB To Be Created");


            //db.execSQL("DROP TABLE "+TABLE_NAME_1);

            db.execSQL("CREATE TABLE " + TABLE_NAME_1 + " (" + COL_1_1 + " INTEGER PRIMARY KEY AUTOINCREMENT , " + COL_1_2 + " TEXT , "  + COL_1_3 + " TEXT , "  + COL_1_4 + " TEXT , "  + COL_1_5 + " TEXT , " + COL_1_6 + " TEXT , " + COL_1_7 + " TEXT , " + COL_1_8 + " TEXT , " + COL_1_9 + " INTEGER , " + COL_1_10 + " INTEGER);");
            db.execSQL("CREATE TABLE " + TABLE_NAME_2 + " (" + COL_2_1 + " INTEGER PRIMARY KEY AUTOINCREMENT , " + COL_2_2 + " TEXT);");


            Log.i("MyTag","DB_Tables are Created Successfully");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
