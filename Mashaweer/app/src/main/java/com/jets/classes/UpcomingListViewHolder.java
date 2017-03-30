package com.jets.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jets.mashaweer.R;

/**
 * Created by mohamed on 17/03/2017.
 */

public class UpcomingListViewHolder {

    View convertedView;
    TextView tripName_tv, tripDate_tv, tripTime_tv;
    LinearLayout tripType_tv;

    ImageView tripThumbnail;

    public UpcomingListViewHolder(View view)
    {
        convertedView = view ;
    }

    public TextView getTripName_tv() {

        if(tripName_tv == null)
            tripName_tv = (TextView) convertedView.findViewById(R.id.upcoming_tripName);

        return tripName_tv;
    }

    public void setTripName_tv(TextView tripName_tv) {
        this.tripName_tv = tripName_tv;
    }

    public TextView getTripDate_tv() {

        if(tripDate_tv == null)
            tripDate_tv = (TextView) convertedView.findViewById(R.id.upcoming_tripDate);

        return tripDate_tv;
    }

    public void setTripDate_tv(TextView tripDate_tv) {
        this.tripDate_tv = tripDate_tv;
    }

    public TextView getTripTime_tv() {

        if(tripTime_tv == null)
            tripTime_tv= (TextView) convertedView.findViewById(R.id.upcoming_tripTime);

        return tripTime_tv;
    }

    public void setTripTime_tv(TextView tripTime_tv) {
        this.tripTime_tv = tripTime_tv;
    }

    public LinearLayout getTripType_tv() {

        if(tripType_tv == null)
            tripType_tv = (LinearLayout) convertedView.findViewById(R.id.upcoming_tripType);

        return tripType_tv;
    }

    public void setTripType_tv(LinearLayout tripType_tv) {
        this.tripType_tv = tripType_tv;
    }


    public ImageView getTripThumbnail() {

        if (tripThumbnail == null)
            tripThumbnail = (ImageView) convertedView.findViewById(R.id.thumbnail);

        return tripThumbnail;
    }

    public void setTripThumbnail(ImageView tripThumbnail) {
        this.tripThumbnail = tripThumbnail;
    }
}
