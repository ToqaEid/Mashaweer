package com.jets.adapters.round;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jets.mashaweer.R;

/**
 * Created by toqae on 3/25/2017.
 */

public class RoundListViewHolder {

    View convertedView;
    TextView roundName;
    TextView roundDestination;
    ImageView imageView;
    LinearLayout startTrip;

    public RoundListViewHolder(View view)
    {
        convertedView = view ;
    }

    public LinearLayout getStartTrip() {
        if(startTrip == null)
            startTrip = (LinearLayout) convertedView.findViewById(R.id.round_start);

        return startTrip;
    }

    public void setStartTrip(LinearLayout startTrip) {
        this.startTrip = startTrip;
    }

    public TextView getRoundName() {

        if(roundName == null) {
            roundName = (TextView) convertedView.findViewById(R.id.round_tripName);

        }


        return roundName;
    }

    public void setRoundName(TextView roundName) {
        this.roundName = roundName;
    }

    public TextView getRoundDestination() {

        if(roundDestination == null)
            roundDestination = (TextView) convertedView.findViewById(R.id.round_destination);

        return roundDestination;
    }

    public void setRoundDestination(TextView roundDestination) {
        this.roundDestination = roundDestination;
    }

    public ImageView getImageView() {
        if(imageView == null)
            imageView = (ImageView) convertedView.findViewById(R.id.round_image);
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}


