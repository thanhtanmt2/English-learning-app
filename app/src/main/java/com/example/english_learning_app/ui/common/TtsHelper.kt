package com.example.english_learning_app.ui.common

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun rememberTtsSpeaker(): (String) -> Unit {
    val context = LocalContext.current
    var ready by remember { mutableStateOf(false) }

    val tts = remember {
        TextToSpeech(context) { status ->
            ready = status == TextToSpeech.SUCCESS
        }
    }

    LaunchedEffect(ready) {
        if (ready) {
            tts.language = Locale.US
            tts.setSpeechRate(0.95f)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    return remember(ready) {
        { text: String ->
            if (!ready || text.isBlank()) return@remember
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_${text.hashCode()}")
        }
    }
}
