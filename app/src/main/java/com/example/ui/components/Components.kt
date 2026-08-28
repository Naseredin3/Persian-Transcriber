package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianSample
import com.example.data.PersianSamples
import com.example.model.ConversionItem
import com.example.model.ToneType
import com.example.tts.TtsPlaybackState
import com.example.tts.VoiceGender
import com.example.ui.theme.SleekIndigoContainer
import com.example.ui.theme.SleekIndigoDark
import com.example.ui.theme.SleekIndigoPrimary
import com.example.ui.theme.SleekOnIndigoContainer
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekSlate50
import com.example.ui.theme.SleekSlate100
import com.example.ui.theme.SleekSlate200
import com.example.ui.theme.SleekSlate400
import com.example.ui.theme.SleekSlate500
import com.example.ui.theme.SleekSlate600
import com.example.ui.theme.SleekSlate700
import com.example.ui.theme.SleekSlate800
import com.example.ui.theme.ToneConciseColor

@Composable
fun VoiceGenderSelector(
    selectedGender: VoiceGender,
    onGenderSelected: (VoiceGender) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VoiceGender.entries.forEach { gender ->
            val isSelected = gender == selectedGender
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) SleekIndigoPrimary else MaterialTheme.colorScheme.surface,
                label = "genderBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else SleekSlate700,
                label = "genderText"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onGenderSelected(gender) }
                    .testTag("gender_chip_${gender.name.lowercase()}"),
                shape = RoundedCornerShape(50),
                color = bgColor,
                border = if (isSelected) null else BorderStroke(1.dp, SleekSlate200)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (gender == VoiceGender.FEMALE) Icons.Filled.Woman else Icons.Filled.Man,
                        contentDescription = gender.titleFa,
                        tint = if (isSelected) Color.White else SleekIndigoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = gender.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ToneSelectorChips(
    selectedTone: ToneType,
    onToneSelected: (ToneType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToneType.entries.forEach { tone ->
            val isSelected = tone == selectedTone
            val chipColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "chipColor"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onToneSelected(tone) }
                    .testTag("tone_chip_${tone.id}"),
                shape = RoundedCornerShape(50),
                color = chipColor,
                border = if (isSelected) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = tone.icon,
                        contentDescription = tone.titleFa,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else SleekSlate600,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = tone.titleFa,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else SleekSlate700
                    )
                }
            }
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val heights = listOf(
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "h1"
        ),
        infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(320, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "h2"
        ),
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(480, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "h3"
        ),
        infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(360, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "h4"
        ),
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(420, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "h5"
        )
    )

    Row(
        modifier = modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { heightAnim ->
            val actualHeight = if (isSpeaking) (heightAnim.value * 22).dp else 4.dp
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(actualHeight.coerceAtLeast(3.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

@Composable
fun AudioPlayerControlBar(
    ttsState: TtsPlaybackState,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onGenderChange: (VoiceGender) -> Unit = {},
    onExportAudio: () -> Unit = {},
    onSpeedChange: (Float) -> Unit = {},
    onPitchChange: (Float) -> Unit = {},
    onShowSettings: () -> Unit = {},
    targetTitle: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_player_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SleekIndigoContainer
        ),
        border = BorderStroke(1.dp, SleekSlate200)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Play button + Info + Settings/Export buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (ttsState.isSpeaking) onStop() else onPlay()
                            }
                            .testTag("tts_play_pause_button"),
                        shape = RoundedCornerShape(16.dp),
                        color = SleekIndigoPrimary,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (ttsState.isSpeaking) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = if (ttsState.isSpeaking) "توقف خوانش" else "پخش با گوینده",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "گوینده صوتی سلیس (${ttsState.gender.titleFa})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SleekSlate800
                            )
                            if (ttsState.isSpeaking) {
                                AudioWaveformVisualizer(isSpeaking = true)
                            }
                        }
                        Text(
                            text = if (ttsState.isSpeaking && ttsState.totalSentences > 0) {
                                "در حال خواندن جمله ${ttsState.currentSentenceIndex + 1} از ${ttsState.totalSentences}"
                            } else {
                                "خوانش هوشمند و طبیعی به زبان فارسی"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekSlate600
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Export audio file button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, SleekSlate200),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = onExportAudio,
                            enabled = !ttsState.isSynthesizingFile,
                            modifier = Modifier.testTag("tts_export_audio_btn")
                        ) {
                            if (ttsState.isSynthesizingFile) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = SleekIndigoPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = "ذخیره و اشتراک فایل صوتی",
                                    tint = SleekIndigoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Settings dialog button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, SleekSlate200),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = onShowSettings,
                            modifier = Modifier.testTag("tts_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Speed,
                                contentDescription = "تنظیمات سرعت و آهنگ صدا",
                                tint = SleekIndigoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Speaker Gender Quick Selection & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "جنسیت گوینده:",
                        style = MaterialTheme.typography.labelSmall,
                        color = SleekSlate700,
                        fontWeight = FontWeight.SemiBold
                    )
                    VoiceGenderSelector(
                        selectedGender = ttsState.gender,
                        onGenderSelected = onGenderChange
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, SleekSlate200),
                    modifier = Modifier.clickable { onExportAudio() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = SleekIndigoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "خروجی صوت",
                            style = MaterialTheme.typography.labelSmall,
                            color = SleekIndigoDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            AnimatedVisibility(visible = ttsState.isSpeaking && ttsState.totalSentences > 1) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    val progress = if (ttsState.totalSentences > 0) {
                        (ttsState.currentSentenceIndex + 1).toFloat() / ttsState.totalSentences.toFloat()
                    } else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = SleekIndigoPrimary,
                        trackColor = SleekSlate200
                    )
                }
            }
        }
    }
}

@Composable
fun SentenceHighlightedViewer(
    text: String,
    ttsState: TtsPlaybackState,
    isTargetActive: Boolean,
    modifier: Modifier = Modifier
) {
    val sentences = remember(text) {
        text.split(Regex("(?<=[.،!؟?؛\n])\\s+")).filter { it.isNotBlank() }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (sentences.isEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 28.sp,
                color = SleekSlate800
            )
        } else {
            sentences.forEachIndexed { index, sentence ->
                val isCurrentSpeaking = isTargetActive && ttsState.isSpeaking && ttsState.currentSentenceIndex == index

                val bgColor by animateColorAsState(
                    targetValue = if (isCurrentSpeaking) SleekIndigoContainer else Color.Transparent,
                    label = "sentence_bg"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .padding(horizontal = if (isCurrentSpeaking) 8.dp else 0.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = sentence,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCurrentSpeaking) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isCurrentSpeaking) SleekOnIndigoContainer else SleekSlate800,
                        lineHeight = 30.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamplePickerBottomSheet(
    onDismiss: () -> Unit,
    onSampleSelected: (PersianSample) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleekIndigoContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = SleekIndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "نمونه متن‌های آماده جهت تست سریع",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate800
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PersianSamples.samples.forEach { sample ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSampleSelected(sample) }
                            .testTag("sample_item_${sample.title.take(10)}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, SleekSlate200)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sample.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekIndigoPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SleekIndigoContainer
                                ) {
                                    Text(
                                        text = sample.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SleekOnIndigoContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = sample.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = SleekSlate600,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    history: List<ConversionItem>,
    onDismiss: () -> Unit,
    onItemSelected: (ConversionItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val filteredList = remember(history, showOnlyFavorites) {
        if (showOnlyFavorites) history.filter { it.isFavorite } else history
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تاریخچه تبدیل‌های اخیر",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate800
                )

                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = !showOnlyFavorites },
                    label = { Text("علاقه‌مندی‌ها") },
                    shape = RoundedCornerShape(50),
                    leadingIcon = {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (showOnlyFavorites) Color.Red else SleekSlate600
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ موردی در تاریخچه یافت نشد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SleekSlate600
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    filteredList.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemSelected(item) }
                                .testTag("history_item_${item.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, SleekSlate200)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = SleekIndigoContainer
                                    ) {
                                        Text(
                                            text = "به لحن: ${item.targetTone.titleFa}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekOnIndigoContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = { onToggleFavorite(item.id) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                contentDescription = "علاقه‌مندی",
                                                tint = if (item.isFavorite) Color.Red else SleekSlate600,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteItem(item.id) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "حذف",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.convertedText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = SleekSlate800
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TtsSettingsDialog(
    ttsState: TtsPlaybackState,
    currentApiKey: String = "",
    onGenderChange: (VoiceGender) -> Unit = {},
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onSaveApiKey: (String) -> Unit = {},
    onClearApiKey: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var showApiKey by remember { mutableStateOf(false) }
    var keySavedNotification by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "تنظیمات برنامه و هوش مصنوعی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SleekSlate800
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Section 1: Permanent API Key Configuration
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SleekSlate50,
                    border = BorderStroke(1.dp, SleekSlate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Key,
                                contentDescription = null,
                                tint = SleekIndigoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "کلید اختصاصی و دائمی Gemini API",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = SleekSlate800
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "کلید شما به‌صورت امن و دائمی روی دستگاه ذخیره شده و نیازی به وارد کردن مجدد ندارد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekSlate600,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = {
                                apiKeyInput = it
                                keySavedNotification = false
                            },
                            placeholder = {
                                Text("AIzaSy...", style = MaterialTheme.typography.bodySmall, color = SleekSlate400)
                            },
                            singleLine = true,
                            visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showApiKey = !showApiKey }) {
                                    Icon(
                                        imageVector = if (showApiKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = "نمایش یا پنهان‌سازی کلید",
                                        tint = SleekSlate500,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("api_key_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SleekIndigoPrimary,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onSaveApiKey(apiKeyInput.trim())
                                    keySavedNotification = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SleekIndigoPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_api_key_btn")
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ذخیره کلید", style = MaterialTheme.typography.labelMedium)
                            }

                            if (apiKeyInput.isNotBlank()) {
                                TextButton(
                                    onClick = {
                                        apiKeyInput = ""
                                        onClearApiKey()
                                        keySavedNotification = false
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = SleekRose
                                    ),
                                    modifier = Modifier.testTag("clear_api_key_btn")
                                ) {
                                    Icon(Icons.Filled.KeyOff, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("حذف", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        if (keySavedNotification) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "کلید دائمی با موفقیت ذخیره شد.",
                                style = MaterialTheme.typography.labelSmall,
                                color = ToneConciseColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Speaker Voice & Gender Selection
                Text(
                    text = "جنسیت و نوع گوینده صوتی:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate800
                )
                Spacer(modifier = Modifier.height(6.dp))
                VoiceGenderSelector(
                    selectedGender = ttsState.gender,
                    onGenderSelected = onGenderChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: TTS Speed & Pitch Settings
                Text(
                    text = "سرعت گفتار: ${String.format("%.2f", ttsState.speechRate)}x",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = SleekSlate800
                )
                Slider(
                    value = ttsState.speechRate,
                    onValueChange = onSpeedChange,
                    valueRange = 0.5f..2.0f,
                    steps = 5,
                    modifier = Modifier.testTag("speech_rate_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = SleekIndigoPrimary,
                        activeTrackColor = SleekIndigoPrimary,
                        inactiveTrackColor = SleekSlate200
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("۰.۵x (آهسته)", style = MaterialTheme.typography.labelSmall, color = SleekSlate600)
                    Text("۱.۰x (طبیعی)", style = MaterialTheme.typography.labelSmall, color = SleekSlate600)
                    Text("۲.۰x (سریع)", style = MaterialTheme.typography.labelSmall, color = SleekSlate600)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "زیر و بمی صدا (Pitch): ${String.format("%.2f", ttsState.pitch)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = SleekSlate800
                )
                Slider(
                    value = ttsState.pitch,
                    onValueChange = onPitchChange,
                    valueRange = 0.6f..1.5f,
                    steps = 4,
                    modifier = Modifier.testTag("speech_pitch_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = SleekIndigoPrimary,
                        activeTrackColor = SleekIndigoPrimary,
                        inactiveTrackColor = SleekSlate200
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekIndigoContainer,
                    border = BorderStroke(1.dp, SleekSlate200)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = SleekIndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "نکته: با انتخاب تب «با اعراب و حرکت»، کلمات حرکت‌گذاری شده و تلفظ صوتی به‌صورت ۱۰۰٪ روان انجام می‌شود.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekOnIndigoContainer,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_confirm_btn")
            ) {
                Text(
                    text = "بستن",
                    fontWeight = FontWeight.Bold,
                    color = SleekIndigoPrimary
                )
            }
        }
    )
}

@Composable
fun MediaUploadSection(
    isProcessing: Boolean,
    progressMessage: String?,
    onPickAudio: () -> Unit,
    onPickVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .testTag("media_upload_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SleekSlate200)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SleekIndigoContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.CloudUpload,
                                contentDescription = null,
                                tint = SleekIndigoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "تبدیل مستقیم از فیلم یا صوت",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = SleekSlate800
                        )
                        Text(
                            text = "استخراج هوشمند گفتار و بازنویسی با گوینده انتخابی",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekSlate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isProcessing) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekIndigoContainer,
                    border = BorderStroke(1.dp, SleekSlate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = SleekIndigoPrimary
                        )
                        Column {
                            Text(
                                text = "در حال پردازش فایل رسانه‌ای...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekIndigoDark
                            )
                            if (!progressMessage.isNullOrBlank()) {
                                Text(
                                    text = progressMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SleekSlate600
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pick Audio Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onPickAudio() }
                            .testTag("pick_audio_btn"),
                        shape = RoundedCornerShape(14.dp),
                        color = SleekSlate50,
                        border = BorderStroke(1.dp, SleekSlate200)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AudioFile,
                                contentDescription = null,
                                tint = SleekIndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بارگذاری صوت",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekSlate800
                            )
                        }
                    }

                    // Pick Video Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onPickVideo() }
                            .testTag("pick_video_btn"),
                        shape = RoundedCornerShape(14.dp),
                        color = SleekSlate50,
                        border = BorderStroke(1.dp, SleekSlate200)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VideoFile,
                                contentDescription = null,
                                tint = SleekIndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بارگذاری فیلم",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekSlate800
                            )
                        }
                    }
                }
            }
        }
    }
}
