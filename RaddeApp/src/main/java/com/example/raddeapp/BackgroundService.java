package com.example.raddeapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Radovan on 2013-07-02.
 */
public class BackgroundService extends Service {

    private Double finalDestinationlat;
    private Double finalDestinationlng;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Radde123 Service: onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Radde123 Service: onStartCommand");

        finalDestinationlng = intent.getDoubleExtra("lng",0.0);
        finalDestinationlat = intent.getDoubleExtra("lat",0.0);

        System.out.println("Radde123 Service: lat: " + finalDestinationlat +
                " lng: " + finalDestinationlng);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Radde123 Service: onDestroy");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
