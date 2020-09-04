package com.shramik.india;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestHanler {
    private static RequestHanler instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private RequestHanler(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized RequestHanler getInstance(Context context) {
        if (instance == null) {
            instance = new RequestHanler(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
