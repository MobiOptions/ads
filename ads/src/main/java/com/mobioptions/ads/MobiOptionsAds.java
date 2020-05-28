package com.mobioptions.ads;

import android.content.Context;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.RequiresApi;

public class MobiOptionsAds {

    private static MobiOptionsAds mobiOptionsAdsSingleton;
    private MobiOptionsAds(){};
    public static MobiOptionsAds getInstance(){
        if (mobiOptionsAdsSingleton == null){ //if there is no instance available... create new one
            mobiOptionsAdsSingleton = new MobiOptionsAds();
        }
        return mobiOptionsAdsSingleton;
    }

    public void init(String appID, final Context context, final InitListener initListener) {
        final RequestQueue mRequestQueue;
        String url = "http://159.89.138.255/api/adsproject/get/" + appID;
        mRequestQueue = Volley.newRequestQueue(context);
        final StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.D(response);
                try {
                    Log.D("Here d");

                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("status")) {
                        if (object.getInt("code") == 404) {
                            Log.D("Token Exception: Please specify a valid token");
                            initListener.onError("Token Exception: Please specify a valid token");
                        } else {
                            Log.D("An Error Happened, Please Contact Admin");
                            initListener.onError("An Error Happened, Please Contact Admin");
                        }
                    } else {
                        JSONArray ads = object.getJSONObject("adProject").getJSONArray("ads");
                        Instance.admobAds = new HashMap<>();
                        for (int i = 0; i < ads.length(); i++) {
                            JSONObject ad = ads.getJSONObject(i);
                            if (ad.getString("type").equals("admob_interstitial")) {
//                                AdmobInterAd a = new AdmobInterAd(ad.getString("ad_id"), ad.getString("name"));
                                 InterstitialAd interstitialAd = new InterstitialAd(context);
                                interstitialAd.setAdUnitId(ad.getString("ad_id"));
                                Instance.admobAds.put(ad.getString("name"),interstitialAd);
                            }
                        }
                        initListener.onInit();
                        Log.D("Here");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.D(error.getMessage());
            }
        });
        mRequestQueue.add(mStringRequest);
    }



    public void show(String name) {
        if(name.contains("admob_interstitial"))
            Instance.admobAds.get(name).show();
    }

    public void loadAd(String name) {
        if(name.contains("admob_interstitial"))
            Instance.admobAds.get(name).loadAd(new AdRequest.Builder().build());
    }


    public interface InitListener{
        void onInit();
        void onError(String error);
    }


}
