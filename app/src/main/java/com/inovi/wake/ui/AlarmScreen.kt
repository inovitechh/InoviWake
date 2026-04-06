package com.inovi.wake.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inovi.wake.util.VibrationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(viewModel: AlarmViewModel = viewModel()) {
    val context = LocalContext.current
    val vibrationHelper = remember { VibrationHelper(context) }
    val timePickerState = rememberTimePickerState(is24Hour = true)

    val modes = listOf("SMOOTH", "HEARTBEAT", "EMERGENCY")
    var selectedMode by remember { mutableStateOf(modes[0]) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("INOVI WAKE", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Uyanma vaktini belirle", style = MaterialTheme.typography.titleMedium)

            // 1. BÖLÜM: Pulse Animasyonu ve Saat Seçici
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Saate ekranda daha fazla yer açar
            ) {
                // 1. Dalga Efektleri (Arka Plan)
                // Boyutu Modifier.size(280.dp) yaparak saatin dışına taşmamasını sağladık
                PulseEffect(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

                // 2. Analog Saat (Ön Plan)
                // Modifier eklemedik ki kendi doğal boyutunda kalsın
                TimePicker(state = timePickerState)
            }

            // 2. BÖLÜM: Titreşim Modu Seçici
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Titreşim Modunu Seç ve Hisset:",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(modes) { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = {
                                selectedMode = mode
                                vibrationHelper.playPreview(mode) // Kısa titreşim önizlemesi
                            },
                            label = { Text(mode) },
                            leadingIcon = if (selectedMode == mode) {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }

            // 3. BÖLÜM: Alarm Kur Butonu
            Button(
                onClick = {
                    viewModel.scheduleAlarm(context, timePickerState.hour, timePickerState.minute, selectedMode)
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Alarm, null)
                Spacer(Modifier.width(12.dp))
                Text("ALARM BAŞLAT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PulseEffect(color: Color, delay: Int = 0) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = color, radius = size.minDimension / 4)
        }
    }
}