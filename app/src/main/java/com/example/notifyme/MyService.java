package com.example.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    private volatile boolean stopSendingNotification = false;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String titleKey = "titleKey";
    public static final String descriptionKey = "descriptionKey";
    public static final String durationKey = "durationKey";
    public SharedPreferences sharedpreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        new Thread(() -> {
            while (true) {
                if (!stopSendingNotification) {
                    Log.d("TAG", "run: 1");
                    try {
                        generateNotification(getApplicationContext());
                        Thread.sleep((long) sharedpreferences.getInt(durationKey, 1) * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopSendingNotification = true;
    }

    private static void generateNotification(Context context) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/quite_impressed.mp3");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default_notification_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(sharedpreferences.getString(titleKey, "Title"))
                .setSound(sound)
                .setContentText(sharedpreferences.getString(descriptionKey, "Hello! This is my first push notification"));
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new
                    NotificationChannel("default_notification_channel_id", "default_notification_channel_id", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationChannel.setSound(sound, audioAttributes);
            mBuilder.setChannelId("default_notification_channel_id");
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(),
                mBuilder.build());
    }
}