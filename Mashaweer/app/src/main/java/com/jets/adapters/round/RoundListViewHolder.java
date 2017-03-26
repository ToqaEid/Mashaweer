package com.jets.adapters.round;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
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

    public RoundListViewHolder(View view)
    {
        convertedView = view ;
    }

    public TextView getRoundName() {

        if(roundName == null)
            roundName = (TextView) convertedView.findViewById(R.id.round_tripName);

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


