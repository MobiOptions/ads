package com.mobioptions.ads;

import android.content.Context;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;

class Communicator {
    private static Communicator communicatorSingleton;

    private Communicator() {
    }


    static Communicator getInstance() {
        if (communicatorSingleton == null) { //if there is no instance available... create new one
            communicatorSingleton = new Communicator();
        }
        return communicatorSingleton;
    }


    void sendCom(Context context, String url, final HashMap params){
        final RequestQueue mRequestQueue;
        url = "http://mobioptions.com/api/"+url;
        Log.D(url);
        mRequestQueue = Volley.newRequestQueue(context);
        final StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.D(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.D(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
