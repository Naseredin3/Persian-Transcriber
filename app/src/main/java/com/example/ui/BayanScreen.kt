package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ToneType
import com.example.ui.components.AudioPlayerControlBar
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.MediaUploadSection
import com.example.ui.components.SamplePickerBottomSheet
import com.example.ui.components.SentenceHighlightedViewer
import com.example.ui.components.ToneSelectorChips
import com.example.ui.components.TtsSettingsDialog
import com.example.ui.components.VoiceGenderSelector
import com.example.ui.theme.SleekBackgroundLight
import com.example.ui.theme.SleekIndigoContainer
import com.example.ui.theme.SleekIndigoDark
import com.example.ui.theme.SleekIndigoLight
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
import com.example.ui.theme.SleekSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BayanScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val ttsState by viewModel.ttsState.collectAsState()
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // File pickers for audio and video upload
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.processMediaFile(context, it) }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.processMediaFile(context, it) }
    }

    // Ensure full Right-to-Left (RTL) Persian rendering
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SleekIndigoContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = SleekIndigoPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "بیان گویا",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate900
                                )
                                Text(
                                    text = "تبدیل لحن محاوره به رسمی و سلیس",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SleekSlate500
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, SleekSlate200),
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.setShowSampleSheet(true) },
                                modifier = Modifier.testTag("top_samples_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "نمونه‌های آماده",
                                    tint = SleekIndigoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, SleekSlate200),
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.setShowHistorySheet(true) },
                                modifier = Modifier.testTag("top_history_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = "تاریخچه",
                                    tint = SleekIndigoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, SleekSlate200),
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.setShowSettingsDialog(true) },
                                modifier = Modifier.testTag("top_settings_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = "تنظیمات صدا",
                                    tint = SleekIndigoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Stay on Main */ },
                        icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "تبدیل") },
                        label = { Text("تبدیل لحن", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekIndigoPrimary,
                            selectedTextColor = SleekIndigoPrimary,
                            indicatorColor = SleekIndigoContainer
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.setShowSampleSheet(true) },
                        icon = { Icon(Icons.Filled.GraphicEq, contentDescription = "نمونه‌ها") },
                        label = { Text("نمونه‌ها") },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = SleekSlate500,
                            unselectedTextColor = SleekSlate500
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.setShowHistorySheet(true) },
                        icon = { Icon(Icons.Filled.History, contentDescription = "تاریخچه") },
                        label = { Text("تاریخچه") },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = SleekSlate500,
                            unselectedTextColor = SleekSlate500
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.setShowSettingsDialog(true) },
                        icon = { Icon(Icons.Filled.Tune, contentDescription = "تنظیمات") },
                        label = { Text("تنظیمات") },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = SleekSlate500,
                            unselectedTextColor = SleekSlate500
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- 1. Sleek Input Box Card ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp))
                        .testTag("input_container_card"),
                    shape = RoundedCornerShape(24.dp),
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
                                    shape = RoundedCornerShape(50),
                                    color = SleekIndigoContainer
                                ) {
                                    Text(
                                        text = "ورودی ${uiState.sourceTone.badgeLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekIndigoDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Paste from clipboard
                                Surface(
                                    shape = CircleShape,
                                    color = SleekIndigoContainer,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val item = clipboard.primaryClip?.getItemAt(0)
                                            val pasteText = item?.text?.toString()
                                            if (!pasteText.isNullOrBlank()) {
                                                viewModel.onInputTextChanged(pasteText)
                                            }
                                        },
                                        modifier = Modifier.testTag("paste_input_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentPaste,
                                            contentDescription = "چسباندن متن",
                                            tint = SleekIndigoPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Clear
                                if (uiState.inputText.isNotBlank()) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SleekSlate100,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.clearInput() },
                                            modifier = Modifier.testTag("clear_input_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "پاک کردن",
                                                tint = SleekSlate600,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.inputText,
                            onValueChange = { viewModel.onInputTextChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(125.dp)
                                .testTag("persian_input_textfield"),
                            placeholder = {
                                Text(
                                    text = "متن فارسی محاوره‌ای یا عامیانه خود را اینجا بنویسید یا از نمونه‌های بالا انتخاب کنید...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SleekSlate400,
                                    lineHeight = 22.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SleekIndigoPrimary,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uiState.inputText.length} کاراکتر",
                                style = MaterialTheme.typography.labelSmall,
                                color = SleekSlate500
                            )

                            // Quick read input text button
                            if (uiState.inputText.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SleekIndigoContainer,
                                    modifier = Modifier.clickable {
                                        if (ttsState.isSpeaking && ttsState.readingActiveTarget == "original") {
                                            viewModel.stopSpeaking()
                                        } else {
                                            viewModel.speak(uiState.inputText, false, "original")
                                        }
                                    }.testTag("read_original_btn")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (ttsState.isSpeaking && ttsState.readingActiveTarget == "original") Icons.Filled.Stop else Icons.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = SleekIndigoPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (ttsState.isSpeaking && ttsState.readingActiveTarget == "original") "توقف" else "شنیدن متن اولیه",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SleekIndigoDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Media Upload Section (Convert directly from video or audio file)
                MediaUploadSection(
                    isProcessing = uiState.isProcessingMedia,
                    progressMessage = uiState.mediaProgressMessage,
                    onPickAudio = { audioPickerLauncher.launch("audio/*") },
                    onPickVideo = { videoPickerLauncher.launch("video/*") }
                )

                // --- 2. Sleek Tone Selection & Transform Action ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SleekSlate200)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "انتخاب لحن مقصد:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate800
                                )
                                Text(
                                    text = uiState.targetTone.subtitleFa,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SleekSlate500
                                )
                            }

                            // Swap source <-> target
                            FilledTonalButton(
                                onClick = { viewModel.swapTones() },
                                modifier = Modifier.testTag("swap_tones_btn"),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = SleekIndigoContainer,
                                    contentColor = SleekIndigoPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SwapHoriz,
                                    contentDescription = "معکوس کردن لحن",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("معکوس", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tone Selector Chips
                        ToneSelectorChips(
                            selectedTone = uiState.targetTone,
                            onToneSelected = { viewModel.onTargetToneSelected(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Convert Action Button (Pill shaped with glowing indigo tone)
                        Button(
                            onClick = { viewModel.convert() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(50))
                                .testTag("convert_action_button"),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekIndigoPrimary
                            ),
                            enabled = !uiState.isConverting && uiState.inputText.isNotBlank()
                        ) {
                            if (uiState.isConverting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "در حال تبدیل لحن و سلیس‌سازی...",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تبدیل لحن به ${uiState.targetTone.titleFa} و سلیس‌سازی",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // --- 3. Sleek Converted Result Card & Speech Player ---
                uiState.currentResult?.let { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp))
                            .testTag("output_result_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SleekIndigoContainer),
                        border = BorderStroke(1.dp, SleekSlate200)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header
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
                                        shape = RoundedCornerShape(50),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = result.targetTone.icon,
                                                contentDescription = null,
                                                tint = SleekIndigoPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "خروجی ${result.targetTone.titleFa} و سلیس",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekIndigoDark
                                            )
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(result.id) },
                                            modifier = Modifier.testTag("fav_result_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (result.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                contentDescription = "علاقه‌مندی",
                                                tint = if (result.isFavorite) Color.Red else SleekSlate600,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Persian Text", result.convertedText)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "متن در حافظه کپی شد", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.testTag("copy_result_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.ContentCopy,
                                                contentDescription = "کپی",
                                                tint = SleekSlate600,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, result.convertedText)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "اشتراک‌گذاری متن فارسی"))
                                            },
                                            modifier = Modifier.testTag("share_result_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Share,
                                                contentDescription = "اشتراک‌گذاری",
                                                tint = SleekSlate600,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // View Mode Tabs (ساده / با اعراب / مقایسه)
                            SecondaryTabRow(
                                selectedTabIndex = uiState.viewMode.ordinal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .testTag("view_mode_tab_row"),
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                OutputViewMode.entries.forEachIndexed { index, mode ->
                                    Tab(
                                        selected = uiState.viewMode == mode,
                                        onClick = { viewModel.setViewMode(mode) },
                                        text = {
                                            Text(
                                                text = mode.titleFa,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (uiState.viewMode == mode) FontWeight.Bold else FontWeight.Normal,
                                                color = if (uiState.viewMode == mode) SleekIndigoPrimary else SleekSlate600
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Output Body based on selected Tab
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, SleekSlate200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.padding(14.dp)) {
                                    when (uiState.viewMode) {
                                        OutputViewMode.STANDARD -> {
                                            SentenceHighlightedViewer(
                                                text = result.convertedText,
                                                ttsState = ttsState,
                                                isTargetActive = ttsState.readingActiveTarget == "converted"
                                            )
                                        }
                                        OutputViewMode.VOCALIZED -> {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = SleekIndigoContainer,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = "حرکت‌گذاری و اعراب دقیق جهت خوانش ۱۰۰٪ صحیح و بدون اشتباه صوتی:",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = SleekOnIndigoContainer,
                                                        modifier = Modifier.padding(10.dp),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                SentenceHighlightedViewer(
                                                    text = result.vocalizedText ?: result.convertedText,
                                                    ttsState = ttsState,
                                                    isTargetActive = ttsState.readingActiveTarget == "converted"
                                                )
                                            }
                                        }
                                        OutputViewMode.COMPARISON -> {
                                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                // Original Box
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = SleekSlate50,
                                                    border = BorderStroke(1.dp, SleekSlate200),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            text = "متن اولیه (${uiState.sourceTone.titleFa}):",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SleekSlate600
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = result.originalText,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = SleekSlate800
                                                        )
                                                    }
                                                }

                                                // Converted Box
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = SleekIndigoContainer,
                                                    border = BorderStroke(1.dp, SleekSlate200),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            text = "متن بازنویسی‌شده (${result.targetTone.titleFa}):",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SleekIndigoDark
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = result.convertedText,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = SleekSlate900
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Audio Player Control Bar for Output
                            AudioPlayerControlBar(
                                ttsState = ttsState,
                                onPlay = {
                                    val isVocalizedMode = uiState.viewMode == OutputViewMode.VOCALIZED
                                    val textToRead = if (isVocalizedMode) result.vocalizedText ?: result.convertedText else result.convertedText
                                    viewModel.speak(textToRead, isVocalizedMode, "converted")
                                },
                                onStop = { viewModel.stopSpeaking() },
                                onGenderChange = { viewModel.setVoiceGender(it) },
                                onExportAudio = { viewModel.exportAndShareAudio(context) },
                                onSpeedChange = { viewModel.setSpeechRate(it) },
                                onPitchChange = { viewModel.setSpeechPitch(it) },
                                onShowSettings = { viewModel.setShowSettingsDialog(true) },
                                targetTitle = result.targetTone.titleFa
                            )

                            // Linguistic Insights / Explanation
                            if (!result.explanation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, SleekSlate200),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SleekIndigoContainer,
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Filled.Lightbulb,
                                                    contentDescription = null,
                                                    tint = SleekIndigoPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = "نکات زبانی و تغییرات نگارشی:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekIndigoDark
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = result.explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SleekSlate700,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 4. Sleek Quick Action Cards Grid (matching Sleek Interface) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setShowSampleSheet(true) }
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SleekSlate200)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SleekIndigoContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = SleekIndigoPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("نمونه‌های آماده", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SleekSlate800)
                            Text("انتخاب سریع متن", style = MaterialTheme.typography.bodySmall, color = SleekSlate500)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setShowSettingsDialog(true) }
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SleekSlate200)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SleekIndigoContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = SleekIndigoPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("تنظیمات گوینده", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SleekSlate800)
                            Text("سرعت و آهنگ صدا", style = MaterialTheme.typography.bodySmall, color = SleekSlate500)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Sheets and Dialogs
        if (uiState.showSampleSheet) {
            SamplePickerBottomSheet(
                onDismiss = { viewModel.setShowSampleSheet(false) },
                onSampleSelected = { viewModel.loadSample(it) }
            )
        }

        if (uiState.showHistorySheet) {
            HistoryBottomSheet(
                history = history,
                onDismiss = { viewModel.setShowHistorySheet(false) },
                onItemSelected = { viewModel.selectHistoryItem(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onDeleteItem = { viewModel.deleteHistoryItem(it) }
            )
        }

        if (uiState.showSettingsDialog) {
            TtsSettingsDialog(
                ttsState = ttsState,
                currentApiKey = uiState.customApiKey,
                onGenderChange = { viewModel.setVoiceGender(it) },
                onSpeedChange = { viewModel.setSpeechRate(it) },
                onPitchChange = { viewModel.setSpeechPitch(it) },
                onSaveApiKey = { viewModel.saveCustomApiKey(it) },
                onClearApiKey = { viewModel.clearCustomApiKey() },
                onDismiss = { viewModel.setShowSettingsDialog(false) }
            )
        }
    }
}
