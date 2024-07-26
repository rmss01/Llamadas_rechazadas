package com.example.llamadas_rechazadas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class IncomingCallsService extends Service {
    private BroadcastReceiver broadcastReceiver; // Instancia tu broadcastreceiver

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiver = new BroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(broadcastReceiver, filter); // Tu bradcastreceiver
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            Log.e("IncomingCallsService", "Service running");
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();

        final String CHANNEL = "Incoming Calls Service";
        NotificationChannel channel = new NotificationChannel(CHANNEL, CHANNEL, NotificationManager.IMPORTANCE_LOW);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL)
                .setContentTitle("Incoming Calls Service")
                .setContentText("Service running...")
                .setSmallIcon(R.drawable.rejected_call2); // Reemplaza con tu icono
        startForeground(1001, notification.build());


        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver); // Tu bradcastreceiver
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
