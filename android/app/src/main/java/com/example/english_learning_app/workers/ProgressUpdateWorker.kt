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
import com.example.english_learning_app.data.local.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProgressUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val db: AppDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val wordCount = db.wordDao().getAll().size
        val grammarCount = db.grammarNoteDao().getAll().size
        showProgressNotification(wordCount, grammarCount)
        return Result.success()
    }

    private fun showProgressNotification(wordCount: Int, grammarCount: Int) {
        val channelId = "progress_update"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Cập nhật tiến độ học tập",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo tổng kết tiến độ học tập hàng tuần"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val contentText = buildString {
            append("Từ vựng: $wordCount từ")
            if (grammarCount > 0) append(" | Ngữ pháp: $grammarCount bài")
            append(" — Tiếp tục cố lên! 💪")
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tiến độ học tập của bạn 📊")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1003, notification)
    }
}
