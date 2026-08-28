package com.example.tts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale

enum class VoiceGender(
    val titleFa: String,
    val shortLabel: String,
    val subtitleFa: String,
    val pitchMultiplier: Float,
    val rateMultiplier: Float
) {
    FEMALE(
        titleFa = "گوینده خانم (بانو)",
        shortLabel = "خانم",
        subtitleFa = "صدای زیر، شفاف و آهنگین",
        pitchMultiplier = 1.25f,
        rateMultiplier = 0.98f
    ),
    MALE(
        titleFa = "گوینده آقا (مرد)",
        shortLabel = "آقا",
        subtitleFa = "صدای بم، رسا و مقتدر",
        pitchMultiplier = 0.80f,
        rateMultiplier = 0.92f
    )
}

data class TtsPlaybackState(
    val isSpeaking: Boolean = false,
    val isPaused: Boolean = false,
    val currentSentenceIndex: Int = 0,
    val currentSentenceText: String = "",
    val totalSentences: Int = 0,
    val speechRate: Float = 0.95f,
    val pitch: Float = 1.0f,
    val gender: VoiceGender = VoiceGender.FEMALE,
    val isPersianSupported: Boolean = false,
    val engineName: String = "",
    val isInitialized: Boolean = false,
    val isSynthesizingFile: Boolean = false,
    val exportedAudioPath: String? = null,
    val readingActiveTarget: String = "" // "converted" or "original"
)

class PersianTtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _playbackState = MutableStateFlow(TtsPlaybackState())
    val playbackState: StateFlow<TtsPlaybackState> = _playbackState.asStateFlow()

    private var currentSentences: List<String> = emptyList()
    private var currentIndex = 0
    private var isManuallyStopped = false
    private var onSynthesisCallback: ((File?, String?) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val persianLocale = Locale("fa", "IR")
            val result = tts?.setLanguage(persianLocale)
            val isSupported = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED

            if (!isSupported) {
                // Try generic Persian
                val fallbackFa = Locale("fa")
                val faResult = tts?.setLanguage(fallbackFa)
                val isFaSupported = faResult != TextToSpeech.LANG_MISSING_DATA &&
                        faResult != TextToSpeech.LANG_NOT_SUPPORTED
                _playbackState.value = _playbackState.value.copy(
                    isInitialized = true,
                    isPersianSupported = isFaSupported,
                    engineName = tts?.defaultEngine ?: "Google TTS"
                )
            } else {
                _playbackState.value = _playbackState.value.copy(
                    isInitialized = true,
                    isPersianSupported = true,
                    engineName = tts?.defaultEngine ?: "Google TTS"
                )
            }

            applyVoiceConfig()
            setupProgressListener()
        } else {
            Log.e("PersianTtsManager", "TTS initialization failed with code: $status")
            _playbackState.value = _playbackState.value.copy(
                isInitialized = false,
                isPersianSupported = false
            )
        }
    }

    private fun applyVoiceConfig() {
        val state = _playbackState.value
        val effectivePitch = (state.pitch * state.gender.pitchMultiplier).coerceIn(0.5f, 2.0f)
        val effectiveRate = (state.speechRate * state.gender.rateMultiplier).coerceIn(0.5f, 2.0f)

        tts?.setPitch(effectivePitch)
        tts?.setSpeechRate(effectiveRate)

        // Attempt to select specific system voice if available
        try {
            val voices = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val targetGender = state.gender
                val matchedVoice = voices.find { voice ->
                    val isPersian = voice.locale.language == "fa"
                    if (!isPersian) return@find false
                    when (targetGender) {
                        VoiceGender.FEMALE -> voice.name.contains("female", ignoreCase = true) ||
                                voice.name.contains("fad", ignoreCase = true) ||
                                voice.name.contains("1", ignoreCase = true)
                        VoiceGender.MALE -> voice.name.contains("male", ignoreCase = true) ||
                                voice.name.contains("fab", ignoreCase = true) ||
                                voice.name.contains("2", ignoreCase = true)
                    }
                } ?: voices.find { it.locale.language == "fa" }

                if (matchedVoice != null) {
                    tts?.voice = matchedVoice
                }
            }
        } catch (e: Exception) {
            Log.w("PersianTtsManager", "Voice selection exception: ${e.message}")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                if (utteranceId?.startsWith("synthesis_") == true) {
                    _playbackState.value = _playbackState.value.copy(isSynthesizingFile = true)
                } else {
                    _playbackState.value = _playbackState.value.copy(
                        isSpeaking = true,
                        isPaused = false
                    )
                }
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId?.startsWith("synthesis_") == true) {
                    _playbackState.value = _playbackState.value.copy(isSynthesizingFile = false)
                    val callback = onSynthesisCallback
                    onSynthesisCallback = null
                    val path = _playbackState.value.exportedAudioPath
                    val file = if (path != null) File(path) else null
                    callback?.invoke(file, null)
                    return
                }

                if (isManuallyStopped) return

                currentIndex++
                if (currentIndex < currentSentences.size) {
                    playSentenceAt(currentIndex)
                } else {
                    _playbackState.value = _playbackState.value.copy(
                        isSpeaking = false,
                        isPaused = false,
                        currentSentenceIndex = 0,
                        currentSentenceText = ""
                    )
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                handleError(utteranceId, "خطای خوانش صوتی")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                handleError(utteranceId, "خطای شماره $errorCode در تولید صدا")
            }

            private fun handleError(utteranceId: String?, msg: String) {
                if (utteranceId?.startsWith("synthesis_") == true) {
                    _playbackState.value = _playbackState.value.copy(isSynthesizingFile = false)
                    val callback = onSynthesisCallback
                    onSynthesisCallback = null
                    callback?.invoke(null, msg)
                } else {
                    _playbackState.value = _playbackState.value.copy(
                        isSpeaking = false,
                        isPaused = false
                    )
                }
            }
        })
    }

    fun setVoiceGender(gender: VoiceGender) {
        _playbackState.value = _playbackState.value.copy(gender = gender)
        applyVoiceConfig()
    }

    fun speak(text: String, targetTag: String = "converted") {
        if (text.isBlank()) return
        stop()

        applyVoiceConfig()
        isManuallyStopped = false
        // Split text into meaningful sentences for smooth pacing and UI synchronization
        val rawSentences = text.split(Regex("(?<=[.،!؟?؛\n])\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        currentSentences = if (rawSentences.isNotEmpty()) rawSentences else listOf(text.trim())
        currentIndex = 0

        _playbackState.value = _playbackState.value.copy(
            isSpeaking = true,
            isPaused = false,
            totalSentences = currentSentences.size,
            readingActiveTarget = targetTag
        )

        playSentenceAt(0)
    }

    private fun playSentenceAt(index: Int) {
        if (index >= currentSentences.size) return
        val sentence = currentSentences[index]

        _playbackState.value = _playbackState.value.copy(
            currentSentenceIndex = index,
            currentSentenceText = sentence
        )

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "persian_sentence_$index")
        }

        tts?.speak(sentence, TextToSpeech.QUEUE_FLUSH, params, "persian_sentence_$index")
    }

    fun stop() {
        isManuallyStopped = true
        tts?.stop()
        _playbackState.value = _playbackState.value.copy(
            isSpeaking = false,
            isPaused = false,
            currentSentenceIndex = 0,
            currentSentenceText = ""
        )
    }

    fun setSpeechRate(rate: Float) {
        val clamped = rate.coerceIn(0.5f, 2.0f)
        _playbackState.value = _playbackState.value.copy(speechRate = clamped)
        applyVoiceConfig()
    }

    fun setPitch(pitch: Float) {
        val clamped = pitch.coerceIn(0.5f, 1.8f)
        _playbackState.value = _playbackState.value.copy(pitch = clamped)
        applyVoiceConfig()
    }

    /**
     * Synthesizes Persian text into a standalone .wav audio file for export/share
     */
    fun exportAudioFile(
        text: String,
        onFinished: (file: File?, errorMessage: String?) -> Unit
    ) {
        if (text.isBlank()) {
            onFinished(null, "متن جهت ایجاد خروجی صوتی خالی است.")
            return
        }

        applyVoiceConfig()

        try {
            val audioDir = File(context.cacheDir, "audio").apply { if (!exists()) mkdirs() }
            val outputFile = File(audioDir, "bayan_persian_audio_${System.currentTimeMillis()}.wav")

            val utteranceId = "synthesis_${System.currentTimeMillis()}"
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }

            _playbackState.value = _playbackState.value.copy(
                isSynthesizingFile = true,
                exportedAudioPath = outputFile.absolutePath
            )
            onSynthesisCallback = onFinished

            val status = tts?.synthesizeToFile(text, params, outputFile, utteranceId)
            if (status != TextToSpeech.SUCCESS) {
                _playbackState.value = _playbackState.value.copy(isSynthesizingFile = false)
                onSynthesisCallback = null
                onFinished(null, "موتور تبدیل صوت قادر به تولید فایل خروجی نشد.")
            }
        } catch (e: Exception) {
            _playbackState.value = _playbackState.value.copy(isSynthesizingFile = false)
            onSynthesisCallback = null
            onFinished(null, e.localizedMessage ?: "خطا در ایجاد فایل صوتی")
        }
    }

    /**
     * Share exported audio file via standard Android share sheet
     */
    fun shareAudio(context: Context, audioFile: File, title: String = "صدای بازنویسی‌شده بیان گویا") {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                audioFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/wav"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "فایل صوتی تولیدشده توسط بیان گویا (تبدیل لحن و سلیس‌سازی فارسی)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری فایل صوتی فارسی"))
        } catch (e: Exception) {
            Log.e("PersianTtsManager", "Error sharing audio file", e)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

