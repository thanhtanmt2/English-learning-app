package com.example.english_learning_app.ui.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.example.english_learning_app.workers.ProgressUpdateWorker
import com.example.english_learning_app.workers.QuizReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationHelper {

    private const val WORK_TAG = "daily_reminder"
    private const val QUIZ_WORK_TAG = "quiz_reminder"
    private const val PROGRESS_WORK_TAG = "progress_update"

    fun scheduleReminder(context: Context, reminderTime: String) {
        val parts = reminderTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Nếu giờ đã qua hôm nay, đặt cho ngày mai
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.english_learning_app.ACTION_DAILY_REMINDER"
            putExtra("REMINDER_TIME", reminderTime)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            100, // Request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Hủy báo thức cũ (nếu có) trước khi đặt mới
        alarmManager.cancel(pendingIntent)

        // Đặt báo thức chính xác (exact alarm)
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Android 14+ có thể ném lỗi nếu không có quyền SCHEDULE_EXACT_ALARM
            // Fallback về báo thức không chính xác
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.english_learning_app.ACTION_DAILY_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleQuizReminder(context: Context) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        val initialDelay = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<QuizReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(QUIZ_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            QUIZ_WORK_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelQuizReminder(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(QUIZ_WORK_TAG)
    }

    fun scheduleProgressUpdate(context: Context) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
        }
        val initialDelay = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<ProgressUpdateWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(PROGRESS_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PROGRESS_WORK_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelProgressUpdate(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(PROGRESS_WORK_TAG)
    }
}
