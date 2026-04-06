package com.inovi.wake.ui

import android.app.NotificationManager
import android.content.Context
import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inovi.wake.util.VibrationHelper

class AlarmActivity : ComponentActivity() {

    private lateinit var vibrationHelper: VibrationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setupLockScreen() // Kilit ekranı ayarları (Öncekiyle aynı)
        super.onCreate(savedInstanceState)

        vibrationHelper = VibrationHelper(this)
        val mode = intent.getStringExtra("VIBRATION_MODE") ?: "EMERGENCY"

        setContent {
            // EK: Titreşimi ekran açıldığı an başlatmak için LaunchedEffect kullanıyoruz
            LaunchedEffect(Unit) {
                when(mode) {
                    "HEARTBEAT" -> vibrationHelper.startHeartbeatVibration()
                    "EMERGENCY" -> vibrationHelper.startEmergencyVibration()
                    else -> vibrationHelper.startSmoothVibration()
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFB00020)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("INOVI WAKE", color = Color.White, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(50.dp))

                    Button(
                        onClick = {
                            // 1. Titreşimi Durdur
                            vibrationHelper.stopVibration()

                            // 2. Bildirimi Kapat (ID: 1001 olmalı)
                            val ns = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            ns.cancel(1001)

                            // 3. Ekranı Kapat
                            finish()
                        },
                        modifier = Modifier.size(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ) {
                        Text("DURDUR", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    private fun setupLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }
}