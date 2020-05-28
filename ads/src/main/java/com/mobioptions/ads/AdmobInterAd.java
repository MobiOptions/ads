package com.mobioptions.ads;

import com.google.android.gms.ads.InterstitialAd;

public class AdmobInterAd{
    InterstitialAd interstitialAd;
    String id;
    String name;

    AdmobInterAd(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
