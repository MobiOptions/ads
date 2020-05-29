package com.mobioptions.ads;

import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.HashMap;

class Instance {
    static boolean showLogs = true;
    static boolean initialised = false;
    static HashMap<String, InterstitialAd> admobInterAds;
    static HashMap<String, AdView> admobBannerAds;
    static LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

}
