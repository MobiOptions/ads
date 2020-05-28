package com.mobioptions.ads;

public class Log {
    public static void D(String Message) {
        if (Instance.showLogs)
            android.util.Log.d("MobiAds", Message);
    }
}
