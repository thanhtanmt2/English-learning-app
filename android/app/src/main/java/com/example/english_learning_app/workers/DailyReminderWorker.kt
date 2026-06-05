package com.example.english_learning_app.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.english_learning_app.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// @HiltWorker báo cho Hilt biết: "Class này cần được Hilt quản lý và khởi tạo"
// Nếu không có annotation này, WorkManager sẽ crash khi cố tạo Worker
@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    // @Assisted: 2 tham số này do WorkManager tự truyền vào, không phải Hilt cung cấp
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    // Hàm này được Android gọi đúng vào giờ đã hẹn
    override suspend fun doWork(): Result {
        showReminderNotification()
        return Result.success() // Báo WorkManager: "Xong rồi, thành công!"
    }

    private fun showReminderNotification() {
        val channelId = "daily_reminder"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 (Oreo) trở lên BẮT BUỘC phải tạo Notification Channel trước
        // Channel giống như "danh mục thông báo" — người dùng có thể tắt từng danh mục
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

        // Xây dựng nội dung thông báo hiện lên thanh trạng thái
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Đến giờ học tiếng Anh! 📚")
            .setContentText("Ôn lại từ vựng hôm nay để duy trì streak của bạn 🔥")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Tự biến mất khi người dùng bấm vào
            .build()

        notificationManager.notify(1001, notification) // 1001 là ID của thông báo này
    }
}
