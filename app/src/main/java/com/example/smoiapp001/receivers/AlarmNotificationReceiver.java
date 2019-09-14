package com.example.smoiapp001.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smoiapp001.utilities.NotificationUtils;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Test", "Received!");
        String action = "com.example.smoiapp001.NOTIFICATION_ACTION";
        if (intent.getAction().equals(action)) {
            NotificationUtils.INSTANCE.remindUserToAddItem(context);
        }

    }
}
