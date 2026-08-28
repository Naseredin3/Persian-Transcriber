package com.example.data

import com.example.model.ToneType

data class PersianSample(
    val title: String,
    val text: String,
    val recommendedTargetTone: ToneType,
    val category: String
)

object PersianSamples {
    val samples = listOf(
        PersianSample(
            title = "پیام عذرخواهی به مدیر (تبدیل به رسمی)",
            text = "سلام مهندس، ببخشید دیروز نرسیدم بیام جلسه چون حالم اصلاً خوب نبود و سرما خورده بودم. میشه لطف کنی خلاصه حرفا و اسلایدا رو برام بفرستی؟ دمت گرم.",
            recommendedTargetTone = ToneType.FORMAL,
            category = "کاری و اداری"
        ),
        PersianSample(
            title = "متن اداری سنگین (تبدیل به محاوره و ساده)",
            text = "احتراماً به استحضار می‌رساند با عنایت به تقارن ایام پایانی هفته جاری با تعطیلات رسمی، مقتضی است هماهنگی‌های لازم جهت لغو موقت جلسات کاری معمول به عمل آید.",
            recommendedTargetTone = ToneType.COLLOQUIAL,
            category = "عامیانه و خودمانی"
        ),
        PersianSample(
            title = "خاطره بارانی (تبدیل به نثر فاخر و ادبی)",
            text = "غروب بود و باران تندی می‌بارید. خیابان‌ها کاملاً خلوت شده بود. یاد خاطرات گذشته افتادم و حسابی دلتنگ دوستان قدیمی شدم.",
            recommendedTargetTone = ToneType.LITERARY,
            category = "ادبیات و کهن"
        ),
        PersianSample(
            title = "متن ناهموار (تبدیل به سلیس و خوش‌خوان)",
            text = "من به خاطر اینکه ترافیک خیلی سنگین بودش نتونستم که به موقع خودم رو برسونم و از این بابت که باعث معطلی شدم عذرخواهی می‌کنم.",
            recommendedTargetTone = ToneType.FLUENT,
            category = "سلیس و ویرایش"
        ),
        PersianSample(
            title = "توصیف غروب و دریا (تبدیل به شاعرانه)",
            text = "خورشید در حال غروب کردن بود و نور قرمزی روی آب دریا افتاده بود. باد آرامی می‌وزید و موج‌ها به ساحل می‌خوردند.",
            recommendedTargetTone = ToneType.POETIC,
            category = "شاعرانه"
        ),
        PersianSample(
            title = "گزارش وضعیت پروژه (تبدیل به خلاصه و رسا)",
            text = "طی هفته گذشته با پیگیری‌های مستمر تیم فنی، بیش از هشتاد درصد باگ‌های سیستمی برطرف گردیده و فاز آزمایشی با موفقیت در محیط تستی پشت سر گذاشته شد.",
            recommendedTargetTone = ToneType.CONCISE,
            category = "خلاصه"
        )
    )
}
