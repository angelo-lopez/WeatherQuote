package com.angelosoft.angelo_romel.weatherquote;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.firebase.client.Firebase;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class WeatherQuote extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1733802498132897~6353267361");
    }
}
