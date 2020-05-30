package com.mobioptions.ads;

import com.google.android.gms.ads.InterstitialAd;

public class InterstitialInstance {
    InterstitialAd admob;
    com.facebook.ads.InterstitialAd facebook;

    public InterstitialInstance(InterstitialAd admob, com.facebook.ads.InterstitialAd facebook) {
        this.admob = admob;
        this.facebook = facebook;
    }

    public InterstitialAd getAdmob() {
        return admob;
    }

    public void setAdmob(InterstitialAd admob) {
        this.admob = admob;
    }

    public com.facebook.ads.InterstitialAd getFacebook() {
        return facebook;
    }

    public void setFacebook(com.facebook.ads.InterstitialAd facebook) {
        this.facebook = facebook;
    }
}
