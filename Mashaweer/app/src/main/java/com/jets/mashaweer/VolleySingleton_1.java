package com.jets.mashaweer;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by toqae on 14/03/2017.
 */

public class VolleySingleton_1 {

    private static VolleySingleton_1 mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;
    private VolleySingleton_1(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton_1 getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton_1(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelRequestFromQueue(String tag){
        if(mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
