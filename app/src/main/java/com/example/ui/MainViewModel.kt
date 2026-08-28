package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ApiKeyStorage
import com.example.data.GeminiService
import com.example.data.HistoryRepository
import com.example.data.PersianSample
import com.example.model.ConversionItem
import com.example.model.ToneType
import com.example.tts.PersianTtsManager
import com.example.tts.TtsPlaybackState
import com.example.tts.VoiceGender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class OutputViewMode(val titleFa: String) {
    STANDARD("متن ساده"),
    VOCALIZED("با اعراب و حرکت"),
    COMPARISON("مقایسه دوطرفه")
}

data class MainUiState(
    val inputText: String = "سلام مهندس، دیروز نرسیدم بیام جلسه چون حالم خوش نبود، میشه اسلایدا رو بفرستی برام؟ مرسی",
    val sourceTone: ToneType = ToneType.COLLOQUIAL,
    val targetTone: ToneType = ToneType.FORMAL,
    val isConverting: Boolean = false,
    val isProcessingMedia: Boolean = false,
    val mediaProgressMessage: String? = null,
    val currentResult: ConversionItem? = null,
    val viewMode: OutputViewMode = OutputViewMode.STANDARD,
    val showSampleSheet: Boolean = false,
    val showHistorySheet: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val customApiKey: String = "",
    val statusMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepo = HistoryRepository()
    private val apiKeyStorage = ApiKeyStorage(application.applicationContext)
    val ttsManager = PersianTtsManager(application.applicationContext)

    val history: StateFlow<List<ConversionItem>> = historyRepo.history
    val ttsState: StateFlow<TtsPlaybackState> = ttsManager.playbackState

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Load stored permanent API key
        val storedKey = apiKeyStorage.getCustomApiKey()
        _uiState.value = _uiState.value.copy(customApiKey = storedKey)

        // Prepare initial sample conversion so the screen is immediately engaging
        convert()
    }

    fun onCustomApiKeyChanged(newKey: String) {
        _uiState.value = _uiState.value.copy(customApiKey = newKey)
    }

    fun saveCustomApiKey(key: String) {
        apiKeyStorage.setCustomApiKey(key)
        _uiState.value = _uiState.value.copy(customApiKey = key.trim())
    }

    fun clearCustomApiKey() {
        apiKeyStorage.clearCustomApiKey()
        _uiState.value = _uiState.value.copy(customApiKey = "")
    }

    fun onInputTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun onSourceToneSelected(tone: ToneType) {
        _uiState.value = _uiState.value.copy(sourceTone = tone)
    }

    fun onTargetToneSelected(tone: ToneType) {
        _uiState.value = _uiState.value.copy(targetTone = tone)
    }

    fun swapTones() {
        val current = _uiState.value
        val newSource = current.targetTone
        val newTarget = current.sourceTone
        val newInput = current.currentResult?.convertedText ?: current.inputText

        _uiState.value = current.copy(
            sourceTone = newSource,
            targetTone = newTarget,
            inputText = newInput
        )
    }

    fun setViewMode(mode: OutputViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
    }

    fun setShowSampleSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSampleSheet = show)
    }

    fun setShowHistorySheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistorySheet = show)
    }

    fun setShowSettingsDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSettingsDialog = show)
    }

    fun setVoiceGender(gender: VoiceGender) {
        ttsManager.setVoiceGender(gender)
    }

    fun loadSample(sample: PersianSample) {
        _uiState.value = _uiState.value.copy(
            inputText = sample.text,
            targetTone = sample.recommendedTargetTone,
            showSampleSheet = false
        )
        convert()
    }

    fun processMediaFile(context: Context, uri: Uri) {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "audio/*"
        var fileName = "فایل چندرسانه‌ای"
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                }
            }
        } catch (e: Exception) {
            // fallback to default name
        }
        processMediaFile(uri, mimeType, fileName)
    }

    fun processMediaFile(uri: Uri, mimeType: String, fileName: String) {
        _uiState.value = _uiState.value.copy(
            isProcessingMedia = true,
            mediaProgressMessage = "در حال بارگذاری و پردازش فایل $fileName..."
        )

        viewModelScope.launch {
            val bytes = withContext(Dispatchers.IO) {
                try {
                    getApplication<Application>().contentResolver.openInputStream(uri)?.use {
                        it.readBytes()
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (bytes == null || bytes.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isProcessingMedia = false,
                    mediaProgressMessage = null,
                    statusMessage = "خطا در خواندن فایل رسانه‌ای انتخاب شده."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                mediaProgressMessage = "هوش مصنوعی در حال استخراج گفتار فارسی و تبدیل لحن..."
            )

            val customKey = _uiState.value.customApiKey
            val target = _uiState.value.targetTone
            val result = GeminiService.processMediaAndConvertTone(
                mediaBytes = bytes,
                mimeType = mimeType,
                targetTone = target,
                customApiKey = customKey
            )

            val response = result.getOrNull()
            if (response != null) {
                val originalExtracted = response.originalText ?: "گفتار استخراج‌شده از $fileName"
                val item = ConversionItem(
                    originalText = originalExtracted,
                    targetTone = target,
                    sourceTone = ToneType.COLLOQUIAL,
                    convertedText = response.convertedText,
                    vocalizedText = response.vocalizedText,
                    explanation = response.explanation
                )
                historyRepo.addItem(item)
                _uiState.value = _uiState.value.copy(
                    inputText = originalExtracted,
                    currentResult = item,
                    isProcessingMedia = false,
                    mediaProgressMessage = null,
                    statusMessage = "استخراج صدا و بازنویسی لحن با موفقیت انجام شد."
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage
                    ?: "پردازش فایل ناموفق بود. لطفاً از اتصال اینترنت و ثبت کلید API در تنظیمات اطمینان حاصل فرمایید."
                _uiState.value = _uiState.value.copy(
                    isProcessingMedia = false,
                    mediaProgressMessage = null,
                    statusMessage = errorMsg
                )
            }
        }
    }

    fun exportAndShareAudio(context: Context) {
        val result = _uiState.value.currentResult
        val isVocalized = _uiState.value.viewMode == OutputViewMode.VOCALIZED
        val textToExport = if (result != null) {
            if (isVocalized) result.vocalizedText ?: result.convertedText else result.convertedText
        } else {
            _uiState.value.inputText
        }
        exportAndShareAudio(context, textToExport, "خروجی صوت بیان گویا")
    }

    fun exportAndShareAudio(context: Context, text: String, title: String = "خروجی صوت فارسی") {
        ttsManager.exportAudioFile(text) { file, err ->
            if (file != null) {
                ttsManager.shareAudio(context, file, title)
            } else {
                Toast.makeText(context, err ?: "خطا در ایجاد فایل صوتی", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun convert() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        val target = _uiState.value.targetTone
        val source = _uiState.value.sourceTone

        _uiState.value = _uiState.value.copy(isConverting = true, statusMessage = null)

        viewModelScope.launch {
            val customKey = _uiState.value.customApiKey
            val responseResult = GeminiService.convertPersianTone(
                text = text,
                targetTone = target,
                sourceTone = source,
                customApiKey = customKey
            )

            val response = responseResult.getOrNull()
            if (response != null) {
                val item = ConversionItem(
                    originalText = text,
                    targetTone = target,
                    sourceTone = source,
                    convertedText = response.convertedText,
                    vocalizedText = response.vocalizedText,
                    explanation = response.explanation
                )
                historyRepo.addItem(item)
                _uiState.value = _uiState.value.copy(
                    isConverting = false,
                    currentResult = item,
                    statusMessage = response.explanation
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isConverting = false,
                    statusMessage = "خطا در برقراری ارتباط."
                )
            }
        }
    }

    fun speak(text: String, useVocalized: Boolean = false, tag: String = "converted") {
        val textToRead = if (useVocalized && !_uiState.value.currentResult?.vocalizedText.isNullOrBlank()) {
            _uiState.value.currentResult?.vocalizedText ?: text
        } else {
            text
        }
        ttsManager.speak(textToRead, tag)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun setSpeechRate(rate: Float) {
        ttsManager.setSpeechRate(rate)
    }

    fun setSpeechPitch(pitch: Float) {
        ttsManager.setPitch(pitch)
    }

    fun toggleFavorite(id: String) {
        historyRepo.toggleFavorite(id)
        if (_uiState.value.currentResult?.id == id) {
            _uiState.value = _uiState.value.copy(
                currentResult = _uiState.value.currentResult?.copy(
                    isFavorite = !(_uiState.value.currentResult?.isFavorite ?: false)
                )
            )
        }
    }

    fun deleteHistoryItem(id: String) {
        historyRepo.deleteItem(id)
    }

    fun selectHistoryItem(item: ConversionItem) {
        _uiState.value = _uiState.value.copy(
            inputText = item.originalText,
            targetTone = item.targetTone,
            sourceTone = item.sourceTone ?: ToneType.COLLOQUIAL,
            currentResult = item,
            showHistorySheet = false
        )
    }

    fun clearInput() {
        _uiState.value = _uiState.value.copy(inputText = "", currentResult = null)
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}

