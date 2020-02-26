package com.teamticpro.bagmetech.servicesBackground;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.teamticpro.bagmetech.managers.NotificationsManagerAndroid;

import java.util.concurrent.ScheduledExecutorService;

public class syncNotifications extends Service {
    ScheduledExecutorService executorService;
    SyncRunnable syncRunnable;
    NotificationsManagerAndroid notificationAndroid;

    public syncNotifications() {

        syncRunnable = new SyncRunnable(notificationAndroid, "192.168.1.100", 1212);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationAndroid = new NotificationsManagerAndroid(getApplicationContext(), syncNotifications.class);
        syncRunnable.setParseNotification(notificationAndroid);
    }

    public void executorService() {

        //executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleWithFixedDelay(syncRunnable, 0, 35, TimeUnit.SECONDS);
        new Thread(syncRunnable).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService();
        return START_STICKY;
    }
}
