package com.inovi.wake.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.inovi.wake.ui.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mode = intent.getStringExtra("VIBRATION_MODE") ?: "SMOOTH"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "inovi_wake_alerts"

        // 1. Bildirim Kanalı Oluşturma (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Inovi Wake Alarm",
                NotificationManager.IMPORTANCE_HIGH // KRİTİK: Ekranı uyandırmak için şart
            ).apply {
                description = "Alarm çalarken gösterilen tam ekran bildirimi"
                setSound(null, null) // Ses istemiyoruz, sadece titreşim
                enableVibration(false) // Titreşimi Activity yöneteceği için burada kapalı
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Kilit Ekranını Uyandıracak Full-Screen Intent (AlarmActivity)
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("VIBRATION_MODE", mode)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Bildirimdeki "DURDUR" Butonu İçin Intent (StopReceiver)
        val stopIntent = Intent(context, StopReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Bildirimi İnşa Et
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Inovi Wake")
            .setContentText("Alarm çalıyor! Durdurmak için dokun.")
            .setPriority(NotificationCompat.PRIORITY_MAX) // En yüksek öncelik
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true) // Kullanıcı silemesin
            .setFullScreenIntent(fullScreenPendingIntent, true) // Ekranı açan asıl komut
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "DURDUR", stopPendingIntent)
            .build()

        // 5. Bildirimi Yayınla
        notificationManager.notify(1001, notification)
    }
}