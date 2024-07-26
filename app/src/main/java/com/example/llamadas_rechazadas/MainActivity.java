package com.example.llamadas_rechazadas;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Switch switchON;
    private Switch switchAgresive;
    private TextView tvAgresive;
    private TextView tvON_OFF;
    private TextView error;
    private static final int PERMISSION_REQUEST_CODE = 100;

    // This method must check each permission, and if any is not granted, it will request it.
    private boolean hasAllPermissions() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        switchON = findViewById(R.id.switchON);
        switchAgresive = findViewById(R.id.switchAgresive);
        tvAgresive = findViewById(R.id.tvAgresive);
        tvON_OFF = findViewById(R.id.tvON_OFF);
        error = findViewById(R.id.error);

        if (isRunning()) {
            tvON_OFF.setText("ON");
        } else {
            tvON_OFF.setText("OFF");
        }
        switchON.setChecked(isRunning());

        switchON.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (hasAllPermissions()) {
                    startService();
                } else {
                    requestPermissions();
                }
            } else {
                stopService();
            }
        });

        switchAgresive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvAgresive.setText(R.string.mode_agresive_text_on);
            } else {
                tvAgresive.setText(R.string.mode_agresive_text_off);
            }
        });
    }

    private void startService() {
        try {
            Intent serviceIntent = new Intent(getApplicationContext(), IncomingCallsService.class);
            startForegroundService(serviceIntent);
            tvON_OFF.setText("ON");
        } catch (Exception e) {
            error.setText(e.getMessage());
        }
    }

    private void stopService() {
        try {
            Intent stopIntent = new Intent(getApplicationContext(), IncomingCallsService.class);
            stopService(stopIntent);
            tvON_OFF.setText("OFF");
        } catch (Exception e) {
            error.setText(e.getMessage());
        }
    }

    public boolean isRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (IncomingCallsService.class.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                startService();
            } else {
                stopService();
                switchON.setChecked(false);
                Toast.makeText(this, "All permissions are required to run this app.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

