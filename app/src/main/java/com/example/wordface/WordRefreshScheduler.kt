package com.example.wordface

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar
import java.util.concurrent.Executors

/** Keeps the feed refresh scheduled for 8:00 and 20:00 device-local time. */
class WordRefreshScheduler : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REFRESH) {
            scheduleNext(context)
            return
        }

        val pendingResult = goAsync()
        executor.execute {
            try {
                WordFeed.refresh(context)
                WordWidgetProvider.updateAll(context)
            } catch (error: Exception) {
                Log.e(TAG, "Scheduled word feed refresh failed", error)
            } finally {
                scheduleNext(context)
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "WordFace"
        private const val ACTION_REFRESH = "com.example.wordface.action.SCHEDULED_REFRESH"
        private val executor = Executors.newSingleThreadExecutor()

        fun ensureScheduled(context: Context) = scheduleNext(context)

        private fun scheduleNext(context: Context) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            val intent = Intent(context, WordRefreshScheduler::class.java).setAction(ACTION_REFRESH)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val now = Calendar.getInstance()
            val next = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.HOUR_OF_DAY, if (now.get(Calendar.HOUR_OF_DAY) < 8) 8 else 20)
                if (timeInMillis <= now.timeInMillis) {
                    if (get(Calendar.HOUR_OF_DAY) == 8) set(Calendar.HOUR_OF_DAY, 20)
                    else { add(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 8) }
                }
            }
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.timeInMillis, pendingIntent)
        }
    }
}
