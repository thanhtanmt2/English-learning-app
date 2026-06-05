package com.example.english_learning_app.ui.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.english_learning_app.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.english_learning_app.ACTION_DAILY_REMINDER") {
            showReminderNotification(context)
            
            // Hẹn lại cho ngày mai đúng giờ này
            val timeString = intent.getStringExtra("REMINDER_TIME") ?: "08:00"
            NotificationHelper.scheduleReminder(context, timeString)
        }
    }

    private fun showReminderNotification(context: Context) {
        val channelId = "daily_reminder"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở học tập",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo nhắc nhở học từ vựng hàng ngày"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Đến giờ học tiếng Anh! ⏰")
            .setContentText("Báo thức chính xác: Vào ôn lại từ vựng hôm nay nhé 🔥")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Ưu tiên cao để hiện ngay lập tức
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2001, notification)
    }
}
