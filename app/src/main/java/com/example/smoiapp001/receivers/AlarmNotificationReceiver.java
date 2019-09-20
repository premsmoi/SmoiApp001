package com.example.smoiapp001.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smoiapp001.utilities.NotificationUtils;

import java.util.Calendar;

import timber.log.Timber;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int am_pm = c.get(Calendar.AM_PM);
        Timber.i("hour: "+hour);
        Timber.i("min: "+min);
        Timber.i("sec: "+sec);
        Timber.i("am_pm: "+am_pm);

        if (am_pm != Calendar.PM)
            return;
        if((hour == 3 && min == 0)
                || (hour == 9 && min == 0))
        {
            String action = "com.example.smoiapp001.NOTIFICATION_ACTION";
            if (intent.getAction().equals(action)) {
                NotificationUtils.INSTANCE.remindUserToAddItem(context);
            }
        }
    }
}
