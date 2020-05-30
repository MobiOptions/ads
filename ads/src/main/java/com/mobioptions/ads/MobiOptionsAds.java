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
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
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

    private MobiOptionsAds() {
    }

    ;

    public static MobiOptionsAds getInstance() {
        if (mobiOptionsAdsSingleton == null) { //if there is no instance available... create new one
            mobiOptionsAdsSingleton = new MobiOptionsAds();
        }
        return mobiOptionsAdsSingleton;
    }

    public void init(String appID, final Context context, final InitListener initListener) {
        AudienceNetworkAds.initialize(context);
        final RequestQueue mRequestQueue;
        String url = "https://mobioptions.com/api/adsproject/get/" + appID;
        mRequestQueue = Volley.newRequestQueue(context);
        final StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.D(response);
                try {
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
                        Instance.InterstitialInstances = new HashMap<>();
                        Instance.BannerInstances = new HashMap<>();
                        for (int i = 0; i < ads.length(); i++) {
                            JSONObject ad = ads.getJSONObject(i);
                            if (ad.getString("type").equals("interstitial")) {
                                //admob stuff
                                InterstitialAd interstitialAd = new InterstitialAd(context);
                                interstitialAd.setAdUnitId(ad.getString("admob_id"));
                                //facebook stuff
                                com.facebook.ads.InterstitialAd finterstitialAd = new com.facebook.ads.InterstitialAd(context, ad.getString("facebook_id"));
                                //adding admob stuff and facebook stuff to mobiotpions stuff
                                Instance.InterstitialInstances.put(ad.getString("name"), new InterstitialInstance(interstitialAd, finterstitialAd));

                            }

                            if (ad.getString("type").equals("banner")) {
                                //admob stuff
                                AdView adView = new AdView(context);
                                adView.setAdSize(AdSize.BANNER);
                                adView.setAdUnitId(ad.getString("admob_id"));
                                //facebook stuff
                                com.facebook.ads.AdView face = new com.facebook.ads.AdView(context, ad.getString("facebook_id"), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

                                //adding admob stuff and facebook stuff to mobiotpions stuff
                                Instance.BannerInstances.put(ad.getString("name"), new BannerInstance(adView, face));

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


    public void loadAd(String name) {
        if (Instance.InterstitialInstances.containsKey(name)) {
            Instance.InterstitialInstances.get(name).getAdmob().loadAd(new AdRequest.Builder().build());
            Instance.InterstitialInstances.get(name).getFacebook().loadAd();
        } else
            Log.D("the string provided '" + name + "' doesn't exist in your project");
    }

    public void show(final String name) {
        if (Instance.InterstitialInstances.containsKey(name)) {
            Instance.InterstitialInstances.get(name).getAdmob().show();
            Instance.InterstitialInstances.get(name).getAdmob().setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    Instance.InterstitialInstances.get(name).getFacebook().show();
                }
            });
        } else
            Log.D("the string provided '" + name + "' doesn't exist in your project");

    }

    public void banner(final LinearLayout container, final String name) {
        Instance.BannerInstances.get(name).getAdmob().loadAd(new AdRequest.Builder().build());
        Instance.BannerInstances.get(name).getAdmob().setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                try {
                    container.addView(Instance.BannerInstances.get(name).getFacebook(), Instance.params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                try {
                    container.addView(Instance.BannerInstances.get(name).getAdmob(), Instance.params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public interface InitListener {
        void onInit();

        void onError(String error);
    }


}
