package com.mobioptions.ads;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MobiOptionsAds {
    private Context context;
    private InterstitialAd admobInterstitial;

    public MobiOptionsAds(Context context){
        this.context = context;
        admobInterstitial = new InterstitialAd(context);
        admobInterstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        admobInterstitial.loadAd(new AdRequest.Builder().build());
    }

    public void show(){
        admobInterstitial.show();
    }


}
