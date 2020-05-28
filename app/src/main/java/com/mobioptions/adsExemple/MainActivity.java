package com.mobioptions.adsExemple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mobioptions.ads.MobiOptionsAds;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MobiOptionsAds ads = MobiOptionsAds.getInstance();
        ads.loadAd("admob_interstitial_1");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MobiAds", "run: ");
                ads.show("admob_interstitial_1");
            }
        },5500);
    }
}
