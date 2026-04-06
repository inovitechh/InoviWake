package com.inovi.wake.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.inovi.wake.receiver.AlarmReceiver
import java.util.*

class AlarmViewModel : ViewModel() {

    fun scheduleAlarm(context: Context, hour: Int, minute: Int, mode: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 1. Android 12+ (API 31+) Hassas Alarm İzni Kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Eğer izin yoksa kullanıcıyı sistem ayarlarına yönlendiriyoruz
                Toast.makeText(context, "Lütfen hassas alarm izni verin.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }

        // 2. Alarm Zamanını Ayarla (Calendar kullanarak)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Eğer seçilen saat şu andan önceyse (örn: saat 23:00 ve biz 07:00'ye kuruyorsak)
            // Alarmı bir sonraki güne (yarına) erteler.
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // 3. Alarm Çaldığında Tetiklenecek Intent
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            // Hangi titreşim modunun çalacağını receiver'a gönderiyoruz
            putExtra("VIBRATION_MODE", mode)
        }

        // Android 12+ için FLAG_IMMUTABLE veya FLAG_MUTABLE zorunludur
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, // Farklı alarmlar için bu ID'yi değiştirebilirsin
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Alarmı Sisteme Kaydet
        try {
            // setExactAndAllowWhileIdle: Cihaz uykudayken (Doze Mode) bile tam vaktinde çalışır
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            val timeText = String.format("%02d:%02d", hour, minute)
            Toast.makeText(context, "Alarm $timeText için kuruldu ($mode)", Toast.LENGTH_SHORT).show()
            Log.d("InoviWake", "Alarm kuruldu: $timeText, Mod: $mode")

        } catch (e: SecurityException) {
            Log.e("InoviWake", "Alarm kurma hatası: ${e.message}")
        }
    }
}