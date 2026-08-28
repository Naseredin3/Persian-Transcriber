package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToneType(
    val id: String,
    val titleFa: String,
    val subtitleFa: String,
    val descriptionFa: String,
    val badgeLabel: String,
    val icon: ImageVector,
    val promptInstruction: String
) {
    COLLOQUIAL(
        id = "colloquial",
        titleFa = "محاوره و صمیمی",
        subtitleFa = "زبان گفتاری روزمره و خودمانی",
        descriptionFa = "تبدیل به زبان گفتاری، صمیمی، شکسته و روان مورد استفاده در گفتگوی دوستانه",
        badgeLabel = "محاوره‌ای",
        icon = Icons.Filled.ChatBubbleOutline,
        promptInstruction = "متن را به زبان کاملاً محاوره‌ای، صمیمی، عامیانه و گفتاری تهرانی/فارسی رایج تبدیل کن (استفاده از افعال شکسته مثل 'می‌خوام'، 'برم'، 'دیدمش'، 'حله' و ساختار گفتگوی دوستانه)."
    ),
    FORMAL(
        id = "formal",
        titleFa = "رسمی و اداری",
        subtitleFa = "مکاتبات رسمی و نوشتار استاندارد",
        descriptionFa = "تبدیل به زبان معیار، محترمانه و مناسب نامه‌نگاری، گزارش و مکالمات کاری",
        badgeLabel = "رسمی",
        icon = Icons.Filled.EditNote,
        promptInstruction = "متن را به زبان کاملاً رسمی، اداری، مؤدبانه و استاندارد فارسی تبدیل کن (رعایت دستور زبان معیار، افعال کامل و واژگان محترمانه اداری)."
    ),
    LITERARY(
        id = "literary",
        titleFa = "ادبی و فاخر",
        subtitleFa = "نثر کلاسیک، کهن و آهنگین",
        descriptionFa = "استفاده از واژگان غنی، ساختار اصیل زبان فارسی و ترکیب‌های فاخر ادبی",
        badgeLabel = "ادبی",
        icon = Icons.Filled.MenuBook,
        promptInstruction = "متن را به نثری ادیبانه، فاخر، بلیغ، آهنگین و کلاسیک فارسی بازآفرینی کن (با بهره‌گیری از واژگان غنی، ترکیبات شیوای ادب پارسی و صنایع ادبی پیراسته بدون تکلف مفرط)."
    ),
    FLUENT(
        id = "fluent",
        titleFa = "سلیس و روان",
        subtitleFa = "روان‌سازی و رفع لکنت جملات",
        descriptionFa = "اصلاح ایرادات نگارشی، روانی کلام و بیان شفاف بدون پیچیدگی اضافی",
        badgeLabel = "سلیس",
        icon = Icons.Filled.RecordVoiceOver,
        promptInstruction = "متن را به زبان فارسی بسیار سلیس، رسا، روان، شیوا و خوش‌آهنگ ویرایش کن؛ به طوری که خوانش صوتی آن بدون کوچک‌ترین وقفه، لکنت یا ابهام صورت گیرد و برای گوش‌نواز بودن در خوانش صوتی بهینه‌سازی شود."
    ),
    POETIC(
        id = "poetic",
        titleFa = "شاعرانه و پر احساس",
        subtitleFa = "بیان احساسی و پر از استعاره",
        descriptionFa = "لحن شاعرانه، توصیفی، احساسی و گوش‌نواز",
        badgeLabel = "شاعرانه",
        icon = Icons.Filled.FormatQuote,
        promptInstruction = "متن را با لحنی شاعرانه، لطیف، پراحساس و سرشار از استعاره‌ها و تصاویر خیال‌انگیز زبان فارسی بازنویسی نما."
    ),
    CONCISE(
        id = "concise",
        titleFa = "خلاصه و رسا",
        subtitleFa = "حذف زواید و بیان لپ کلام",
        descriptionFa = "بیان فشرده، صریح و بدون حاشیه",
        badgeLabel = "گزیده",
        icon = Icons.Filled.ShortText,
        promptInstruction = "مفاهیم اصلی متن را به کوتاه‌ترین، گویاترین و دقیق‌ترین شکل ممکن خلاصه و رسا بازنویسی کن."
    );

    companion object {
        fun fromId(id: String): ToneType = entries.find { it.id == id } ?: FORMAL
    }
}
