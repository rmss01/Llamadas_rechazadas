package com.example.llamadas_rechazadas;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, IncomingCallsService.class);
            context.startForegroundService(serviceIntent);
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            // Incoming call is ringing
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d("CallReceiver", "Incoming call from: " + incomingNumber);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            // Call picked up or dialed
            Log.d("CallReceiver", "Call picked up");
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            // Call ended or missed
            Log.d("CallReceiver", "Call ended or missed");
        }
    }
}
