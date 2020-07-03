package com.mobioptions.ads;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.LongDef;
import androidx.annotation.RequiresApi;

public class MobiOptionsAds {

    private static MobiOptionsAds mobiOptionsAdsSingleton;
    private Context context;

    private MobiOptionsAds() {
    }


    public static MobiOptionsAds getInstance() {
        if (mobiOptionsAdsSingleton == null) { //if there is no instance available... create new one
            mobiOptionsAdsSingleton = new MobiOptionsAds();
        }
        return mobiOptionsAdsSingleton;
    }

    public void init(String appID, final Context context, final InitListener initListener) {
        AudienceNetworkAds.initialize(context);
        this.context = context;
        final RequestQueue mRequestQueue;
        String url = "https://api.mobioptions.com/api/adsproject/get/" + appID;
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
                        Instance.projectId = object.getJSONObject("adProject").getInt("id");
                        Instance.adsEnabled = object.getJSONObject("adProject").getInt("ads_activated") == 1;
                        Instance.serveMethod = object.getJSONObject("adProject").getString("ads_provider");
                        Instance.servePer = object.getJSONObject("adProject").getLong("serve_perc");
                        JSONArray ads = object.getJSONObject("adProject").getJSONArray("ads");
                        Instance.InterstitialInstances = new HashMap<>();
                        Instance.BannerInstances = new HashMap<>();
                        for (int i = 0; i < ads.length(); i++) {
                            JSONObject ad = ads.getJSONObject(i);
                            if (ad.getString("type").equals("interstitial")) {
                                //admob stuff
                                InterstitialAd interstitialAd = new InterstitialAd(context);
                                interstitialAd.setAdUnitId(Instance.serveMethod.equals("facebook") ? "" : ad.getString("admob_id"));
                                //facebook stuff
                                com.facebook.ads.InterstitialAd finterstitialAd = new com.facebook.ads.InterstitialAd(context,Instance.serveMethod.equals("admob") ? "" :  ad.getString("facebook_id"));
                                //adding admob stuff and facebook stuff to mobiotpions stuff
                                Instance.InterstitialInstances.put(ad.getString("name"), new InterstitialInstance(interstitialAd, finterstitialAd));

                            }

                            if (ad.getString("type").equals("banner")) {
                                //admob stuff
                                AdView adView = new AdView(context);
                                adView.setAdSize(AdSize.BANNER);
                                adView.setAdUnitId(Instance.serveMethod.equals("facebook") ? "" : ad.getString("admob_id"));
                                //facebook stuff
                                com.facebook.ads.AdView face = new com.facebook.ads.AdView(context,Instance.serveMethod.equals("admob") ? "" :  ad.getString("facebook_id"), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

                                //adding admob stuff and facebook stuff to mobiotpions stuff
                                Instance.BannerInstances.put(ad.getString("name"), new BannerInstance(adView, face));

                            }
                        }
                        initListener.onInit();
                        sendOpenInstallData(context);
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

    private void sendOpenInstallData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        String last = preferences.getString("last_open", "-");

        int opentime = preferences.getInt("open_time", 0);
        Log.D("opened " + opentime + "");

        boolean unique = false, second = opentime == 1;
        if (!sdf.format(new Date()).equals(last)) {
            unique = true;
        }

        if (last.equals("-"))
            sendInstallData(context);

        HashMap<String, String> key = new HashMap<>();
        key.put("ads_projects_id", Instance.projectId + "");
        key.put("unique", unique ? "true" : "false");
        key.put("second", second ? "true" : "false");
        Communicator.getInstance().sendCom(context, "adsprojectopen/add", key);

        preferences.edit().putString("last_open", sdf.format(new Date())).apply();
        preferences.edit().putInt("open_time", opentime + 1).apply();

    }

    private void sendInstallData(Context context) {
        HashMap<String, String> key = new HashMap<>();
        key.put("ads_projects_id", Instance.projectId + "");
        key.put("playstore", verifyInstallerId(context) ? "true" : "false");
        Communicator.getInstance().sendCom(context, "adsprojectinstalls/add", key);
    }

    private boolean verifyInstallerId(Context context) {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    public void loadAd(String name) {
        if (Instance.adsEnabled) {
            if (Instance.InterstitialInstances.containsKey(name)) {
                Instance.InterstitialInstances.get(name).getAdmob().loadAd(new AdRequest.Builder().build());
                Instance.InterstitialInstances.get(name).getFacebook().loadAd();
            } else
                Log.D("the string provided '" + name + "' doesn't exist in your project");
        }
    }

    public void show(final String name) {
        if (Instance.adsEnabled && Instance.shouldShow()) {
            if (Instance.InterstitialInstances.containsKey(name)) {
                Instance.InterstitialInstances.get(name).getAdmob().show();
                Instance.InterstitialInstances.get(name).getAdmob().setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        Instance.InterstitialInstances.get(name).getFacebook().show();
                        Instance.InterstitialInstances.get(name).getFacebook().setAdListener(new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                sendComAd("fi");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {

                            }

                            @Override
                            public void onAdLoaded(Ad ad) {

                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        });
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        sendComAd("ai");
                    }

                });
            } else
                Log.D("the string provided '" + name + "' doesn't exist in your project");
        }
    }

    public void banner(final LinearLayout container, final String name) {
        if (Instance.adsEnabled && Instance.shouldShow()) {
            if (Instance.InterstitialInstances.containsKey(name)) {
                Instance.BannerInstances.get(name).getAdmob().loadAd(new AdRequest.Builder().build());
                Instance.BannerInstances.get(name).getAdmob().setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        try {
                            container.addView(Instance.BannerInstances.get(name).getFacebook(), Instance.params);
                            Instance.BannerInstances.get(name).getFacebook().setAdListener(new InterstitialAdListener() {
                                @Override
                                public void onInterstitialDisplayed(Ad ad) {
                                    sendComAd("fb");
                                }

                                @Override
                                public void onInterstitialDismissed(Ad ad) {

                                }

                                @Override
                                public void onError(Ad ad, AdError adError) {

                                }

                                @Override
                                public void onAdLoaded(Ad ad) {

                                }

                                @Override
                                public void onAdClicked(Ad ad) {

                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        try {
                            container.addView(Instance.BannerInstances.get(name).getAdmob(), Instance.params);
                            sendComAd("ab");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else
                Log.D("the string provided '" + name + "' doesn't exist in your project");
        }
    }


    public interface InitListener {
        void onInit();

        void onError(String error);
    }

    private void sendComAd(String k) {
        HashMap<String, String> key = new HashMap<>();
        key.put("ads_projects_id", Instance.projectId + "");
        key.put(k, "true");
        Communicator.getInstance().sendCom(context, "adsprojectadstats/add", key);
    }

}
