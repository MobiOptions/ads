package com.mobioptions.adsExemple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mobioptions.ads.Log;
import com.mobioptions.ads.MobiOptionsAds;

public class SplashActivity extends AppCompatActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);


    final MobiOptionsAds ads = MobiOptionsAds.getInstance();
    ads.init("gwOzCPRgVCoWWR8IdN9BJogLnnuy4B", this, new MobiOptionsAds.InitListener() {
        @Override
        public void onInit() {
            Log.D("Here  ss");
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }

        @Override
        public void onError(String error) {
            Log.D(error);
        }
    });
}
}
