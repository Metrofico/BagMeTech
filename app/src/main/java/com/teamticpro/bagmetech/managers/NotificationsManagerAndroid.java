package com.teamticpro.bagmetech.managers;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.Window;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.teamticpro.bagmetech.R;

import java.security.SecureRandom;

public class NotificationsManagerAndroid {

    private Context context;
    private NotificationManager notifManager;
    private NotificationChannel mChannel;
    private Class classService;

    public NotificationsManagerAndroid(Context context, Class classService) {
        this.context = context;
        this.classService = classService;
    }


    public Context getContext() {
        return context;
    }

    public void createNotifications(String title, String description) {
        SecureRandom random = new SecureRandom();
        int m = random.nextInt(9999 - 1000) + 1000;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            Intent intent = new Intent(context, classService);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        (String.valueOf(m), title, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setSmallIcon(getNotificationIcon()) // required
                    .setContentText(description)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (context.getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = builder.build();
            notifManager.notify(m, notification);
        } else {
            Intent intent = new Intent(context, classService);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;
            pendingIntent = PendingIntent.getActivity(context, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(description));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(m, notificationBuilder.build());
            }
        }
    }

    public void setWindowsDisplay(Dialog d) {
        Window w = d.getWindow();
        int width = (context.getResources().getDisplayMetrics().widthPixels);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.90);
        if (w != null) {
            w.setLayout(width, height);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
}
