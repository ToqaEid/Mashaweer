package com.jets.classes;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jets.adapters.UpcomingCustomAdapter;

/**
 * Created by toqae on 3/22/2017.
 */

public class UpcomingListFormat {
    public static void setListViewHeightBasedOnChildren(ListView mListView) {
        ArrayAdapter mListAdapter = (ArrayAdapter) mListView.getAdapter();
        Log.i("Tag sdf", mListAdapter.toString());
        if (mListAdapter == null) {
            // when adapter is null
            Log.i("Tag mListAdapter", "null");
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        Log.i("Tag list count", String.valueOf(mListAdapter.getCount()));
        for (int i = 0; i < mListAdapter.getCount(); i++) {

            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        Log.i("Tag list height", String.valueOf(height));

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        //params.height = 100 + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

}
