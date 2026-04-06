package com.inovi.wake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.inovi.wake.ui.AlarmScreen
import com.inovi.wake.ui.theme.InoviWakeTheme // Eğer tema ismin farklıysa burayı düzeltmelisin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 12+ ve 14+ için ekranı tam kapasite (uçtan uca) kullanır
        enableEdgeToEdge()

        setContent {
            // Android Studio'nun otomatik oluşturduğu tema ismi genellikle 'ProjeİsmiTheme' olur.
            InoviWakeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Hazırladığımız ana ekranı burada çağırıyoruz
                    AlarmScreen()
                }
            }
        }
    }
}