package com.mobioptions.adsExemple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import com.mobioptions.ads.MobiOptionsAds;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MobiOptionsAds ads = MobiOptionsAds.getInstance();
        ads.loadAd("interstitial_1");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ads.show("interstitial_1");
            }
        },5500);

        LinearLayout adContainer = findViewById(R.id.ads_holder);
        ads.banner(adContainer,"banner_1");


    }
}
