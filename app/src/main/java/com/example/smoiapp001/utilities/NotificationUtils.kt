package com.example.smoiapp001.utilities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

import com.example.smoiapp001.R
import com.example.smoiapp001.activities.MainActivity
import com.example.smoiapp001.activities.ManageTransactionActivity
import timber.log.Timber
import java.util.*

object NotificationUtils {

    private val REMINDER_NOTIFICATION_ID = 101

    private val ACTION_ADD_ITEM_INTENT_ID = 1

    private val REMINDER_PENDING_INTENT_ID = 3417

    private val REMINDER_NOTIFICATION_CH_ID = "reminder_notification_channel"

    fun remindUserToAddItem(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                    REMINDER_NOTIFICATION_CH_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CH_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_payment_small)
                .setContentTitle(context.getString(R.string.add_item_reminder_notification_title))
                .setContentText(context.getString(R.string.add_item_reminder_notification_body))
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.add_item_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(addNewItem(context))
                /*.addAction(ignoreReminderAction(context))*/
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }
        notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun addNewItem(context: Context): NotificationCompat.Action {
        val addItem = Intent(context, ManageTransactionActivity::class.java)
        addItem.action = ManageTransactionActivity.ACTION_MANAGE_TRANSACTION
        val pendingAddItem = PendingIntent.getActivity(
                context,
                ACTION_ADD_ITEM_INTENT_ID,
                addItem,
                PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(
                R.drawable.common_google_signin_btn_icon_dark,
                "Add",
                pendingAddItem)
    }

    private fun contentIntent(context: Context): PendingIntent {
        val startActivityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
                context,
                REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun loadNotification(context: Context) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // SET TIME HERE
        val firstCalendar = Calendar.getInstance()
        firstCalendar.set(Calendar.HOUR_OF_DAY, 15)
        firstCalendar.set(Calendar.MINUTE, 0)
        firstCalendar.set(Calendar.SECOND, 0)

        val secondCalendar = Calendar.getInstance()
        secondCalendar.set(Calendar.HOUR_OF_DAY, 21)
        secondCalendar.set(Calendar.MINUTE, 0)
        secondCalendar.set(Calendar.SECOND, 0)

        val when1 = firstCalendar.timeInMillis
        val when2 = secondCalendar.timeInMillis
        val myIntent = Intent("com.example.smoiapp001.NOTIFICATION_ACTION")

        val pendingIntent1 = PendingIntent.getBroadcast(context, 11, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntent2 = PendingIntent.getBroadcast(context, 12, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        Timber.i("pendingIntent1 is $pendingIntent1")
        Timber.i("pendingIntent2 is $pendingIntent2")

        if (pendingIntent1 != null)
            manager.setRepeating(AlarmManager.RTC_WAKEUP, when1, AlarmManager.INTERVAL_DAY, pendingIntent1)
        if (pendingIntent2 != null)
            manager.setRepeating(AlarmManager.RTC_WAKEUP, when2, AlarmManager.INTERVAL_DAY, pendingIntent2)
    }

}
