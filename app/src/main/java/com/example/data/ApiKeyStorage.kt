package com.example.data

import android.content.Context
import android.content.SharedPreferences

class ApiKeyStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bayan_api_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CUSTOM_GEMINI_API_KEY = "custom_gemini_api_key"
    }

    fun getCustomApiKey(): String {
        return prefs.getString(KEY_CUSTOM_GEMINI_API_KEY, "")?.trim() ?: ""
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString(KEY_CUSTOM_GEMINI_API_KEY, key.trim()).apply()
    }

    fun clearCustomApiKey() {
        prefs.edit().remove(KEY_CUSTOM_GEMINI_API_KEY).apply()
    }

    fun hasCustomApiKey(): Boolean {
        return getCustomApiKey().isNotBlank()
    }
}
