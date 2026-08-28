package com.example.model

data class ConversionItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val originalText: String,
    val targetTone: ToneType,
    val sourceTone: ToneType? = null,
    val convertedText: String,
    val vocalizedText: String? = null,
    val explanation: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
