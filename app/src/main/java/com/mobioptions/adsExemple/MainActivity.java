package com.mobioptions.adsExemple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.mobioptions.ads.MobiOptionsAds;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MobiOptionsAds ads = new MobiOptionsAds(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ads.show();
            }
        },1500);
    }
}
