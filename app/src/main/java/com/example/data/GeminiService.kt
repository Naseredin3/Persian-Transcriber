package com.example.data

import android.util.Base64
import com.example.BuildConfig
import com.example.model.ToneType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeminiConversionResponse(
    val originalText: String? = null,
    val convertedText: String,
    val vocalizedText: String,
    val explanation: String
)

object GeminiService {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun convertPersianTone(
        text: String,
        targetTone: ToneType,
        sourceTone: ToneType? = null,
        customApiKey: String? = null
    ): Result<GeminiConversionResponse> = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }
        if (apiKey.isBlank()) {
            // Provide high-quality local fallback if key is not configured
            val localResult = LocalToneConverter.convertLocally(text, targetTone)
            return@withContext Result.success(
                GeminiConversionResponse(
                    originalText = text,
                    convertedText = localResult,
                    vocalizedText = localResult,
                    explanation = "تبدیل با موتور آفلاین واژگانی (برای پردازش فوق‌پیشرفته هوش مصنوعی، کلید دائمی API را در تنظیمات وارد کنید)."
                )
            )
        }

        try {
            val systemPrompt = """
                شما یک زبان‌شناس، ادیب و کارشناس ارشد زبان و ادب فارسی و متخصص گفتاردرمانی و تبدیل لحن هستید.
                وظیفه شما این است که متن ورودی کاربر را با بالاترین کیفیت، سلاست، روانی و فصاحت به لحن مقصد تبدیل نمایید.
                
                دستورالعمل‌های کلیدی:
                1. لحن مقصد: ${targetTone.titleFa} (${targetTone.descriptionFa})
                2. دستور لحن: ${targetTone.promptInstruction}
                3. نسخه سلیس و فصیح: متن را بسیار روان و بدون لکنت برای تبدیل به گفتار (TTS) تنظیم کنید.
                4. نسخه معرب (vocalizedText): متن تبدیل‌شده را به صورت کامل و دقیق با حرکات و اعراب‌گذاری زبان فارسی (فتحه، کسره، ضمه، تنوین، تشدید، سکون، همزه) بنویسید تا موتور خوانش صوتی آن را با تلفظ ۱۰۰٪ دقیق و گوش‌نواز ادا کند.
                5. توضیحات کوتاه (explanation): در ۲ الی ۳ خط به زبان فارسی توضیح دهید چه تغییرات واژگانی یا ساختاری انجام شد.
                
                فرمت خروجی صرفاً به صورت JSON معتبر باشد:
                {
                   "convertedText": "متن تبدیل‌شده با رعایت کامل لحن و فصاحت",
                   "vocalizedText": "مَتْنِ تَبْديلْ‌شُدِهْ هَمْراهْ با اِعْرابْ‌گُذاريِ دَقيقْ بَرايِ گويَنْدِه",
                   "explanation": "توضیح کوتاه تغییرات واژگانی و دستوری"
                }
            """.trimIndent()

            val userContent = """
                متن ورودی:
                $text
                
                لحن خواسته شده: ${targetTone.titleFa}
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "$systemPrompt\n\n$userContent"))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                val fallback = LocalToneConverter.convertLocally(text, targetTone)
                return@withContext Result.success(
                    GeminiConversionResponse(
                        originalText = text,
                        convertedText = fallback,
                        vocalizedText = fallback,
                        explanation = "تبدیل آفلاین (پاسخ سرور: کد ${response.code})"
                    )
                )
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textOutput = parts?.optJSONObject(0)?.optString("text")

            if (!textOutput.isNullOrBlank()) {
                val cleanJson = textOutput.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val parsed = JSONObject(cleanJson)
                val converted = parsed.optString("convertedText", "")
                val vocalized = parsed.optString("vocalizedText", converted)
                val explanation = parsed.optString("explanation", "تبدیل لحن با موفقیت انجام شد.")

                Result.success(
                    GeminiConversionResponse(
                        originalText = text,
                        convertedText = converted.ifBlank { LocalToneConverter.convertLocally(text, targetTone) },
                        vocalizedText = vocalized.ifBlank { converted },
                        explanation = explanation
                    )
                )
            } else {
                val fallback = LocalToneConverter.convertLocally(text, targetTone)
                Result.success(
                    GeminiConversionResponse(
                        originalText = text,
                        convertedText = fallback,
                        vocalizedText = fallback,
                        explanation = "تبدیل محلی انجام گردید."
                    )
                )
            }
        } catch (e: Exception) {
            val fallback = LocalToneConverter.convertLocally(text, targetTone)
            Result.success(
                GeminiConversionResponse(
                    originalText = text,
                    convertedText = fallback,
                    vocalizedText = fallback,
                    explanation = "خطا در برقراری ارتباط با هوش مصنوعی؛ تبدیل با الگوریتم واژگانی آفلاین انجام شد."
                )
            )
        }
    }

    suspend fun processMediaAndConvertTone(
        mediaBytes: ByteArray,
        mimeType: String,
        targetTone: ToneType,
        customApiKey: String? = null
    ): Result<GeminiConversionResponse> = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                Exception("برای پیاده‌سازی و پردازش فایل صوتی یا ویدیویی، لطفاً ابتدا کلید دائمی Gemini API را در تنظیمات وارد نمایید.")
            )
        }

        try {
            val base64Data = Base64.encodeToString(mediaBytes, Base64.NO_WRAP)

            val prompt = """
                شما یک دستیار هوشمند و ادیب متخصص تحلیل صوت، ویدیو و زبان فارسی هستید.
                فایل صوتی یا ویدیویی ارسال شده حاوی گفتار به زبان فارسی است.
                
                وظایف شما:
                ۱. پیاده‌سازی دقیق گفتار فارسی موجود در فایل (Transcribe) و ثبت آن در فیلد 'transcribedText'.
                ۲. بازنویسی و تبدیل متن به لحن مقصد: ${targetTone.titleFa} (${targetTone.descriptionFa}) با دستور: ${targetTone.promptInstruction} و ثبت در فیلد 'convertedText'.
                ۳. ایجاد نسخه معرب و حرکت‌گذاری شده کامل با اعراب دقیق فارسی جهت خوانش صوتی بی‌نقص (vocalizedText).
                ۴. توضیحات کوتاه در ۲ الی ۳ خط درباره متن پیاده‌شده و تغییرات اعمال‌شده در فیلد 'explanation'.
                
                فرمت خروجی صرفاً به صورت JSON معتبر باشد:
                {
                   "transcribedText": "متن فارسی دقیق استخراج‌شده از صوت/فیلم",
                   "convertedText": "متن تبدیل‌شده و سلیس به لحن مقصد",
                   "vocalizedText": "مَتْنِ تَبْديلْ‌شُدِهْ با اِعْرابْ‌گُذاريِ دَقيقْ",
                   "explanation": "توضیح کوتاه تغییرات لحن و استخراج صدا"
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", mimeType)
                                    put("data", base64Data)
                                })
                            })
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                return@withContext Result.failure(Exception("خطا در پردازش فایل رسانه‌ای (کد سرور: ${response.code})"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textOutput = parts?.optJSONObject(0)?.optString("text")

            if (!textOutput.isNullOrBlank()) {
                val cleanJson = textOutput.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val parsed = JSONObject(cleanJson)
                val transcribed = parsed.optString("transcribedText", "")
                val converted = parsed.optString("convertedText", transcribed)
                val vocalized = parsed.optString("vocalizedText", converted)
                val explanation = parsed.optString("explanation", "استخراج و تبدیل موفق صوت/فیلم.")

                Result.success(
                    GeminiConversionResponse(
                        originalText = transcribed,
                        convertedText = converted,
                        vocalizedText = vocalized,
                        explanation = explanation
                    )
                )
            } else {
                Result.failure(Exception("پاسخ معتبری از هوش مصنوعی دریافت نشد."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

