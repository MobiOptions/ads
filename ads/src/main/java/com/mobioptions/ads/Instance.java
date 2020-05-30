package com.mobioptions.ads;

import android.widget.LinearLayout;

import java.util.HashMap;

class Instance {
    static boolean showLogs = true;
    static boolean initialised = false;
    static HashMap<String, InterstitialInstance> InterstitialInstances;
    static HashMap<String, BannerInstance> BannerInstances;
    static LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

}
