package com.example.llamadas_rechazadas;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.Provider;
import java.util.List;
import java.util.Map;

public class LocationSender extends Service implements LocationListener {
    private LocationManager locationManager;
    private String phoneNumber;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e("LocationService", "Permission error: " + e.getMessage());
            stopSelf();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            phoneNumber = intent.getStringExtra("incomingNumber");
        }
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String locationMessage = "Estoy aquí: https://maps.google.com/?q=" + latitude + "," + longitude;
        if (phoneNumber != null) {
            locationMessage += "\nTeléfono: " + phoneNumber;
        }

        sendWhatsAppMessage(locationMessage);
        stopSelf();

    }

    private void sendWhatsAppMessage(String message) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("LocationService", "WhatsApp not installed: " + e.getMessage());
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
