package com.mobioptions.ads;

import com.google.android.gms.ads.AdView;

public class BannerInstance {
    AdView admob;
    com.facebook.ads.AdView facebook;

    public BannerInstance(AdView admob, com.facebook.ads.AdView facebook) {
        this.admob = admob;
        this.facebook = facebook;
    }

    public AdView getAdmob() {
        return admob;
    }

    public void setAdmob(AdView admob) {
        this.admob = admob;
    }

    public com.facebook.ads.AdView getFacebook() {
        return facebook;
    }

    public void setFacebook(com.facebook.ads.AdView facebook) {
        this.facebook = facebook;
    }
}
