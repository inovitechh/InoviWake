package com.inovi.wake.util

import android.content.Context
import android.os.*

class VibrationHelper(private val context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // 1. SMOOTH MODU: Kademeli artan, yumuşak bir dalga
    fun startSmoothVibration() {
        // Pattern: Bekle, Titre, Bekle, Titre...
        val pattern = longArrayOf(0, 800, 400, 800)
        // Şiddet: 0'dan 255'e kadar (Yumuşak olması için düşük tuttuk)
        val amplitudes = intArrayOf(0, 60, 0, 90)

        executeVibration(pattern, amplitudes, 0) // 0: Sonsuz döngü
    }

    // 2. HEARTBEAT MODU: "Güm-Güm... Güm-Güm..." ritmi
    fun startHeartbeatVibration() {
        val pattern = longArrayOf(0, 100, 100, 200, 800)
        val amplitudes = intArrayOf(0, 150, 0, 255, 0)

        executeVibration(pattern, amplitudes, 0)
    }

    // 3. EMERGENCY MODU: Zaten çalışan sert sarsıntı
    fun startEmergencyVibration() {
        val pattern = longArrayOf(0, 200, 100, 200, 100)
        val amplitudes = intArrayOf(0, 255, 0, 255, 0)

        executeVibration(pattern, amplitudes, 0)
    }

    // Ortak Çalıştırma Fonksiyonu
    private fun executeVibration(pattern: LongArray, amplitudes: IntArray, repeat: Int) {
        stopVibration() // Önce temizle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, repeat))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeat)
        }
    }

    // CANLI ÖNİZLEME (Tek seferlik çalar)
    fun playPreview(mode: String) {
        stopVibration()
        when (mode) {
            "SMOOTH" -> vibrator.vibrate(VibrationEffect.createOneShot(400, 80))
            "HEARTBEAT" -> vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 200), intArrayOf(0, 150, 0, 255), -1))
            "EMERGENCY" -> vibrator.vibrate(VibrationEffect.createOneShot(500, 255))
        }
    }

    fun stopVibration() {
        vibrator.cancel()
    }
}