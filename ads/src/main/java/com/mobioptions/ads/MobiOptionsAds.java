package com.mobioptions.ads;

import android.content.Context;
import android.os.Build;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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
                        Instance.admobInterAds = new HashMap<>();
                        Instance.admobBannerAds = new HashMap<>();
                        for (int i = 0; i < ads.length(); i++) {
                            JSONObject ad = ads.getJSONObject(i);
                            if (ad.getString("type").equals("admob_interstitial")) {
                                InterstitialAd interstitialAd = new InterstitialAd(context);
                                interstitialAd.setAdUnitId(ad.getString("ad_id"));
                                Instance.admobInterAds.put(ad.getString("name"),interstitialAd);
                            }
                            if (ad.getString("type").equals("admob_banner")) {
                                AdView adView = new AdView(context);
                                adView.setAdSize(AdSize.BANNER);
                                adView.setAdUnitId(ad.getString("ad_id"));
                                Instance.admobBannerAds.put(ad.getString("name"),adView);
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
        if(name.contains("admob_interstitial")) {
            Instance.admobInterAds.get(name).show();

        }
    }

    public void banner(LinearLayout container, String name) {
        if(name.contains("admob_banner")) {
            Instance.admobBannerAds.get(name).loadAd(new AdRequest.Builder().build());
            container.addView(Instance.admobBannerAds.get(name), Instance.params);
        }
    }

    public void loadAd(String name) {
        if(name.contains("admob_interstitial"))
            Instance.admobInterAds.get(name).loadAd(new AdRequest.Builder().build());
    }

    public void loadAd(String name,final AdListener adListener) {
        if(name.contains("admob_interstitial")) {
            Instance.admobInterAds.get(name).loadAd(new AdRequest.Builder().build());
            Instance.admobInterAds.get(name).setAdListener(new com.google.android.gms.ads.AdListener(){
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    adListener.onAdFailedToLoad(i);
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    adListener.onAdClosed();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    adListener.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    adListener.onAdImpression();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    adListener.onAdLeftApplication();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adListener.onAdLoaded();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    adListener.onAdOpened();
                }
            });
        }
    }


    public interface InitListener{
        void onInit();
        void onError(String error);
    }

    public interface AdListener{
        void onAdFailedToLoad(int i);
        void onAdClosed();
        void onAdClicked();
        void onAdImpression();
        void onAdLeftApplication();
        void onAdLoaded();
        void onAdOpened();
    }



}
