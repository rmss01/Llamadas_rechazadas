package com.example.llamadas_rechazadas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Switch switchON;
    private Switch switchAgresive;
    private TextView tvAgresive;
    private TextView tvON_OFF;
    private TextView error;

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
        error = (TextView) findViewById(R.id.error);

        /*Intent serviceIntent = new Intent(getApplicationContext(), IncomingCallsService.class);
        startForegroundService(serviceIntent);*/

        if (isRunning()){
            tvON_OFF.setText("ON");
        } else {
            tvON_OFF.setText("OFF");
        }
        switchON.setChecked(isRunning());

        switchON.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        Intent serviceIntent = new Intent(getApplicationContext(), IncomingCallsService.class);
                        startForegroundService(serviceIntent);
                        tvON_OFF.setText("ON");
                    } catch (Exception e){
                        error.setText(e.getMessage());
                    }


                } else {
                    try {
                        Intent stopIntent = new Intent(getApplicationContext(), IncomingCallsService.class);
                        stopService(stopIntent);
                        tvON_OFF.setText("OFF");
                    } catch (Exception e){
                        error.setText(e.getMessage());
                    }

                }
            }
        });
        switchAgresive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvAgresive.setText(R.string.mode_agresive_text_on);
                } else {
                    tvAgresive.setText(R.string.mode_agresive_text_off);
                }
            }
        });
    }
    public boolean isRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (IncomingCallsService.class.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}