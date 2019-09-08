package com.example.smoiapp001.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.smoiapp001.R;
import com.example.smoiapp001.activities.MainActivity;
import com.example.smoiapp001.activities.ManageTransactionActivity;

public class NotificationUtils {

    private static final int REMINDER_NOTIFICATION_ID = 101;

    private static final int ACTION_ADD_ITEM_INTENT_ID = 1;

    private static final int REMINDER_PENDING_INTENT_ID = 3417;

    private static final String REMINDER_NOTIFICATION_CH_ID = "reminder_notification_channel";

    public static void remindUserToAddItem(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    REMINDER_NOTIFICATION_CH_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,REMINDER_NOTIFICATION_CH_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_payment_small)
                .setContentTitle(context.getString(R.string.add_item_reminder_notification_title))
                .setContentText(context.getString(R.string.add_item_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.add_item_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(addNewItem(context))
                /*.addAction(ignoreReminderAction(context))*/
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static NotificationCompat.Action addNewItem(Context context){
        Intent addItem = new Intent(context, ManageTransactionActivity.class);
        addItem.setAction(ManageTransactionActivity.ACTION_MANAGE_TRANSACTION);
        PendingIntent pendingAddItem = PendingIntent.getActivity(
                context,
                ACTION_ADD_ITEM_INTENT_ID,
                addItem,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action addItemAction =new NotificationCompat.Action(
                R.drawable.common_google_signin_btn_icon_dark,
                "Add",
                pendingAddItem);
        return addItemAction;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
