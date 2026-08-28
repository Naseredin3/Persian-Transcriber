package com.example.data

import com.example.model.ConversionItem
import com.example.model.ToneType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryRepository {
    private val _history = MutableStateFlow<List<ConversionItem>>(emptyList())
    val history: StateFlow<List<ConversionItem>> = _history.asStateFlow()

    init {
        // Initial sample history items showcasing Persian conversions
        _history.value = listOf(
            ConversionItem(
                originalText = "سلام داداش کجایی دلم برات تنگ شده یه زنگ به من بزن",
                targetTone = ToneType.FORMAL,
                sourceTone = ToneType.COLLOQUIAL,
                convertedText = "با سلام و درود برادر گرامی، جویای احوال شریف شما هستم و مشتاق دیدارتان؛ در صورت امکان تماسی حاصل فرمایید.",
                vocalizedText = "با سَلامْ و دُرودْ بَرادَرِ گِرامي، جويايِ اَحْوالِ شَريفِ شُما هَسْتَمْ و مُشْتاقِ ديدارِتان؛ دَر صورَتِ اِمْكانْ تَماسي حاصِلْ فَرماييد.",
                explanation = "جایگزینی تعابیر عامیانه ('داداش', 'دلم تنگ شده', 'زنگ بزن') با عبارات محترمانه اداری و فارسی معیار.",
                isFavorite = true
            ),
            ConversionItem(
                originalText = "احتراماً مراتب تقدیر و امتنان اینجانب را بابت مساعدت‌های بی‌شائبه حضرتعالی پذیرا باشید.",
                targetTone = ToneType.COLLOQUIAL,
                sourceTone = ToneType.FORMAL,
                convertedText = "خیلی ممنون از همه کمک‌های باارزشت، واقعاً دمت گرم و خسته نباشی.",
                vocalizedText = "خِيلي مَمْنونْ اَز هَمِهْ كُمَك‌هايِ بااَرْزِشِتْ، واقِعاً دَمِتْ گَرْمْ و خَسْتِهْ نَباشي.",
                explanation = "ساده‌سازی عبارات تکلف‌آمیز به واژگان صمیمی و دوستانه روزمره.",
                isFavorite = false
            )
        )
    }

    fun addItem(item: ConversionItem) {
        val current = _history.value.toMutableList()
        current.removeAll { it.originalText == item.originalText && it.targetTone == item.targetTone }
        current.add(0, item)
        _history.value = current.take(50)
    }

    fun toggleFavorite(id: String) {
        _history.value = _history.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun deleteItem(id: String) {
        _history.value = _history.value.filter { it.id != id }
    }

    fun clearAll() {
        _history.value = emptyList()
    }
}
