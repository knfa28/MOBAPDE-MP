package com.example.kurt.kitakasama;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class SmsManager {
    private Context context;

    public SmsManager(Context context) {
        this.context = context;
    }

    public void sendToAll(ArrayList<LocalTracker> localTrackers, String message) {
        for (int i =0; i < localTrackers.size(); i++) {
            send(localTrackers.get(i), message);
        }
    }

    public void send(LocalTracker localTracker, String message) {
        send(localTracker.getTrackerContact(), message);
    }

    public void send(String phoneNumber, String message) {
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);
        android.telephony.SmsManager SMSManager = android.telephony.SmsManager.getDefault();
        SMSManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}