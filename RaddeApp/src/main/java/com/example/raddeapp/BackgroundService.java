package com.example.raddeapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private String titleText;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Radde123 Service: onCreate");
        titleText = "Sleepy running";

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification =
                new Notification(R.drawable.ic_launcher, titleText,System.currentTimeMillis());
                PendingIntent contentIntent =
                        PendingIntent.getActivity(BackgroundService.this, 0,
                                new Intent(BackgroundService.this,   MainActivity.class), 0);
                notification.setLatestEventInfo(BackgroundService.this,"MainActivity", titleText, contentIntent);
                notificationManager.notify(R.string.service_started, notification);
                notificationIntent = new Intent(this, MainActivity.class);
    }
    private void showNotification(){
        CharSequence text = getText(R.string.service_started);

        Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, "MainActivity",titleText, contentIntent);
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationManager.notify(R.string.service_started, notification);
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
        System.out.println("Radde123 Service: onDestroy");
        notificationManager.cancel(R.string.service_started);
        stopSelf();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
