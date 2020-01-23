package com.e.maiplaceapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDex;

public class App extends Application {
    public static final String CHANNEL_1_ID = "order_ready";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }



    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel order_ready = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Order Ready",
                    NotificationManager.IMPORTANCE_HIGH
            );

            order_ready.setDescription("This is for order ready");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(order_ready);

        }
    }
}
