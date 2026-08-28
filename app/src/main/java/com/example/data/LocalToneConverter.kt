package com.example.data

import com.example.model.ToneType

object LocalToneConverter {

    private val colloquialToFormalMap = mapOf(
        "سلام مهندس" to "جناب مهندس با سلام و احترام",
        "سلام" to "با سلام و درود",
        "ببخشید" to "با پوزش و احترام",
        "نرسیدم بیام" to "امکان حضور برای بنده میسر نگردید",
        "بیام" to "حضور یابم",
        "برم" to "عازم شوم",
        "می‌خوام" to "قصد دارم",
        "میخوام" to "مایلم",
        "میشه" to "آیا امکان دارد",
        "لطف کنی" to "محبت فرموده و",
        "اسلایدا رو" to "اسلایدها را",
        "بفرستی برام" to "برای اینجانب ارسال نمایید",
        "دمت گرم" to "از بذل توجه و همکاری شما صمیمانه سپاسگزارم",
        "مرسی" to "با تشکر فراوان",
        "ممنون" to "با سپاس و امتنان",
        "خیلی باحاله" to "بسیار شایسته و مطلوب است",
        "باحال" to "جذاب و ارزشمند",
        "داشتم می‌رفتم" to "در حال حرکت بودم",
        "دیدمش" to "ایشان را ملاقات نمودم",
        "حله" to "موضوع مورد موافقت قرار گرفت",
        "اوکیه" to "مورد تأیید است",
        "چیکار می‌کنی" to "در چه وضعیتی قرار دارید",
        "نمیدونم" to "اطلاعی در دست نیست",
        "نمی‌دونم" to "اطلاعی ندارم",
        "چته" to "علت ناراحتی شما چیست",
        "خونه" to "منزل",
        "بچه ها" to "همکاران و دوستان",
        "بچه‌ها" to "همکاران گرامی",
        "کارا" to "امور",
        "چیزا" to "موارد",
        "میگه" to "اظهار می‌دارد",
        "گفتم" to "عرض نمودم",
        "باشه" to "بسیار خب، مورد پذیرش است"
    )

    private val formalToColloquialMap = mapOf(
        "احتراماً به استحضار می‌رساند" to "سلام، خواستم بگم که",
        "به استحضار می‌رساند" to "باید بگم که",
        "مقتضی است" to "بهتره که",
        "به عمل آید" to "انجام بشه",
        "حالت تعلیق درآید" to "کنسل بشه",
        "لغو می‌گردد" to "کنسل شد",
        "اینجانب" to "من",
        "حضرتعالی" to "شما",
        "پیرو مذاکرات قبلی" to "راجع به همون صحبتی که داشتیم",
        "ارسال نمایید" to "بفرستید",
        "میسّر نگردید" to "نشد بیام",
        "میسر نگردید" to "نشد",
        "سپاسگزارم" to "مرسی و دمت گرم",
        "با عنایت به" to "با توجه به اینکه",
        "منزل" to "خونه",
        "امور" to "کارا",
        "اسلایدها را" to "اسلایدا رو",
        "فراهم گردید" to "جور شد",
        "اطلاعی ندارم" to "نمی‌دونم"
    )

    fun convertLocally(text: String, targetTone: ToneType): String {
        var result = text.trim()
        when (targetTone) {
            ToneType.FORMAL -> {
                colloquialToFormalMap.forEach { (casual, formal) ->
                    result = result.replace(casual, formal)
                }
                // General heuristics
                result = result
                    .replace(Regex("""(\b)رو(\b)"""), "$1را$2")
                    .replace(Regex("""(\b)ها رو(\b)"""), "$1ها را$2")
                    .replace(Regex("""(\b)شونو(\b)"""), "$1شان را$2")
                    .replace("خوبی؟", "حال شما چطور است؟")
                if (!result.startsWith("با سلام") && !result.startsWith("احتراماً")) {
                    result = "احتراماً، $result"
                }
            }
            ToneType.COLLOQUIAL -> {
                formalToColloquialMap.forEach { (formal, casual) ->
                    result = result.replace(formal, casual)
                }
                result = result
                    .replace(Regex("""(\b)را(\b)"""), "$1رو$2")
                    .replace("می‌توانم", "می‌تونم")
                    .replace("می‌تواند", "می‌تونه")
                    .replace("می‌دانم", "می‌دونم")
                    .replace("می‌خوانم", "می‌خونم")
                    .replace("می‌گویم", "می‌گم")
                    .replace("می‌روم", "می‌رم")
                    .replace("می‌شوم", "می‌شم")
                    .replace("تهران است", "تهرانه")
            }
            ToneType.LITERARY -> {
                result = result
                    .replace("خیلی", "بسی")
                    .replace("خوب", "نیکو و دل‌پسند")
                    .replace("زیبا", "خوب‌رو و مه‌پیکر")
                    .replace("باران", "باران رحمت")
                    .replace("دیدم", "به نظاره نشستم")
                    .replace("رفتم", "رهسپار گشتم")
                    .replace("گفت", "لب به سخن گشود و گفت")
                result = "چنین بود که $result و روزگار بر این قرار گذشت."
            }
            ToneType.FLUENT -> {
                result = result
                    .replace("به خاطر اینکه", "از آنجا که")
                    .replace("بودش", "بود")
                    .replace("نمیدونم", "نمی‌دانم")
                    .replace("باعث معطلی شدم", "موجب تأخیر شدم")
            }
            ToneType.POETIC -> {
                result = "نوای دل‌نشین واژه‌ها: $result؛ چنان که گویی نسیم سحری بر جان می‌وزد."
            }
            ToneType.CONCISE -> {
                val sentences = result.split(Regex("[.،؛\n]")).filter { it.isNotBlank() }
                result = if (sentences.isNotEmpty()) sentences.take(2).joinToString(". ") + "." else result
            }
        }
        return result
    }
}
