package com.mobioptions.ads;

import android.widget.LinearLayout;

import java.util.HashMap;

class Instance {
    static boolean showLogs = true;
    static boolean initialised = false;
    static boolean adsEnabled = false;
    static float adsCounter = 0;
    static String serveMethod = "default";
    static float servePer = 1.0f;
    static int projectId;
    static HashMap<String, InterstitialInstance> InterstitialInstances;
    static HashMap<String, BannerInstance> BannerInstances;
    static LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


    public static boolean shouldShow(){
        float tmp = adsCounter;
        adsCounter++;
        return tmp*servePer == (int) tmp*servePer;
    }
}
