package com.inovi.wake.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.inovi.wake.util.VibrationHelper

class StopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Titreşimi Kes
        VibrationHelper(context).stopVibration()

        // 2. Bildirimi Kapat
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001) // AlarmReceiver'daki ID ile aynı olmalı

        // 3. Eğer AlarmActivity açıksa onu da kapatmak için bir broadcast daha atılabilir ama şimdilik bu yeterli
    }
}