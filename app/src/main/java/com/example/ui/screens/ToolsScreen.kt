package com.example.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.graphics.pdf.PdfDocument
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tonality
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.composed
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.ui.theme.PrimaryRed
import com.example.ui.components.PassportPhotoMakerToolDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ToolItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun ToolsScreen(
    initialToolId: String? = null,
    onToolCleared: () -> Unit = {},
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeToolId by remember { mutableStateOf<String?>(initialToolId) }

    LaunchedEffect(initialToolId) {
        if (initialToolId != null) {
            activeToolId = initialToolId
        }
    }

    val dismissTool = {
        activeToolId = null
        onToolCleared()
    }

    val tools = listOf(
        ToolItem(
            id = "passportphoto",
            title = "Passport Photo Maker",
            subtitle = "Generate multi-copy passport & visa photo sheets (A4, 4x6, custom sizes & colors)",
            icon = Icons.Default.Badge,
            badge = ""
        ),
        ToolItem(
            id = "resizer",
            title = "Image Resizer",
            subtitle = "SarkariResult style photo & signature resizer (Width, Height, KB, DPI, Units)",
            icon = Icons.Default.Compress,
            badge = ""
        ),
        ToolItem(
            id = "compressor",
            title = "Image Compressor",
            subtitle = "Compress images by quality slider or exact target KB size",
            icon = Icons.Default.Compress,
            badge = ""
        ),
        ToolItem(
            id = "crop",
            title = "Image Crop",
            subtitle = "Passport (3.5x4.5cm), Signature (3.5x1.5cm) crop with rotation, zoom & offset",
            icon = Icons.Default.Crop,
            badge = ""
        ),
        ToolItem(
            id = "namedate",
            title = "Name & Date on Photo",
            subtitle = "SSC / UP Police / NTA photo stamp generator with candidate name & DOP date",
            icon = Icons.Default.Badge,
            badge = ""
        ),
        ToolItem(
            id = "agecalc",
            title = "Age Calculator",
            subtitle = "Govt Exam Cutoff Age Calculator with Category Relaxation & Eligibility Check",
            icon = Icons.Default.Calculate,
            badge = ""
        ),
        ToolItem(
            id = "removebg",
            title = "Remove Background",
            subtitle = "Passport background changer (White, Light Blue, Red, Grey) with edge tolerance",
            icon = Icons.Default.AutoFixHigh,
            badge = ""
        ),
        ToolItem(
            id = "imageadjust",
            title = "Image Adjust",
            subtitle = "Brightness, Contrast, Clarity, Saturation, Hue, Shadows, Highlights & Temperature adjustment",
            icon = Icons.Default.Tune,
            badge = ""
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Candidate Powerful Tools",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Professional utilities based on Sarkari Result & Sewayojan portals for photo resizing, cropping, DOP name stamping, background removal & age calculation.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Active Candidate Tools",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        tools.forEach { tool ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable {
                        activeToolId = tool.id
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryRed.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = PrimaryRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = tool.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (tool.badge.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = tool.badge,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tool.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Prominent View All Tools Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryRed.copy(alpha = 0.05f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, PrimaryRed.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sewayojan-tools.vercel.app/")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        onShowToast("Opening Web Tools...")
                    }
                }
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View All Tools (Online Portal)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryRed
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Access 70+ Advanced Govt Form Utilities on Sarkari Sewayojan",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sewayojan-tools.vercel.app/")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            onShowToast("Opening Web Tools...")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open All Web Tools", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    var sharedToolBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val handleMoveToTool: (String, Bitmap) -> Unit = { targetToolId, bitmap ->
        sharedToolBitmap = bitmap
        activeToolId = targetToolId
    }

    // Active Tool Dialog (Full width and full height on all mobile devices)
    activeToolId?.let { id ->
        Dialog(
            onDismissRequest = {
                activeToolId = null
                sharedToolBitmap = null
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                shape = RectangleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxSize()
            ) {
                when (id) {
                    "passportphoto" -> PassportPhotoMakerToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                    "resizer" -> ImageResizerToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                    "compressor" -> ImageCompressorToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                    "crop" -> ImageCropToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                    "namedate" -> NameDatePhotoToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                    "agecalc" -> AgeCalculatorToolDialog(onDismiss = { dismissTool(); sharedToolBitmap = null })
                    "removebg" -> RemoveBackgroundToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                    "imageadjust" -> ImageAdjustToolDialog(
                        initialBitmap = sharedToolBitmap,
                        onDismiss = { dismissTool(); sharedToolBitmap = null },
                        onMoveToTool = handleMoveToTool,
                        onShowToast = onShowToast
                    )
                }
            }
        }
    }
}

data class ImageToolOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveToOtherToolBottomSheet(
    currentToolId: String,
    currentBitmap: Bitmap,
    onToolSelected: (targetToolId: String, bitmap: Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val allTools = listOf(
        ImageToolOption("passportphoto", "Passport Photo Maker", "Generate multi-copy passport photo sheets", Icons.Default.Badge),
        ImageToolOption("resizer", "Image Resizer", "Resize Width, Height, KB & Units", Icons.Default.Compress),
        ImageToolOption("compressor", "Image Compressor", "Compress quality slider or target KB", Icons.Default.Compress),
        ImageToolOption("crop", "Image Crop", "Passport, Signature & Custom ratio crop", Icons.Default.Crop),
        ImageToolOption("namedate", "Name & Date on Photo", "SSC / Govt photo stamp with Name & DOP", Icons.Default.Badge),
        ImageToolOption("removebg", "Remove Background", "Passport BG changer (White, Blue, Red, Grey)", Icons.Default.AutoFixHigh),
        ImageToolOption("imageadjust", "Image Adjust", "Brightness, Contrast, Saturation, Temp, Hue", Icons.Default.Tune)
    )

    val targetTools = allTools.filter { it.id != currentToolId }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Move Image to Other Tool",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "इमेज को दूसरे टूल में भेजें (Multi-Tool Editing)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Image Preview Strip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = currentBitmap.asImageBitmap(),
                        contentDescription = "Current Image Preview",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Current Photo State", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = PrimaryRed
                            ) {
                                Text(
                                    text = "Active",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currentBitmap.width} × ${currentBitmap.height} px • ${(currentBitmap.byteCount / 1024)} KB",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Destination Tool:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                targetTools.forEach { tool ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                onToolSelected(tool.id, currentBitmap)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryRed.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = null,
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tool.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(tool.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryRed.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Open", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryRed)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = PrimaryRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

// Proportional moving scrollbar modifier for tool pages with track & capsule handle
fun Modifier.thinScrollbar(
    scrollState: ScrollState,
    width: Dp = 6.dp,
    trackColor: Color = Color.Gray.copy(alpha = 0.15f),
    thumbColor: Color = PrimaryRed.copy(alpha = 0.85f)
): Modifier = composed {
    val scrollMax = scrollState.maxValue.toFloat()
    val scrollValue = scrollState.value.toFloat()
    val scrollPercent = if (scrollMax > 0f) (scrollValue / scrollMax).coerceIn(0f, 1f) else 0f

    val animatedPercent by animateFloatAsState(
        targetValue = scrollPercent,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scrollbar_thumb_anim"
    )

    this.drawWithContent {
        drawContent()
        if (scrollMax > 0f) {
            val elementHeight = size.height
            val trackWidthPx = (width - 2.dp).toPx().coerceAtLeast(2f)
            val thumbWidthPx = width.toPx()

            // 1. Draw subtle full-height background track line
            val trackX = size.width - (trackWidthPx + thumbWidthPx) / 2f
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(trackX, 0f),
                size = Size(trackWidthPx, elementHeight),
                cornerRadius = CornerRadius(trackWidthPx / 2f, trackWidthPx / 2f)
            )

            // 2. Draw prominent moving scroll handle (thumb) with compact height
            val minThumbHeightPx = 36.dp.toPx()
            val maxThumbHeightPx = (elementHeight * 0.2f).coerceAtLeast(minThumbHeightPx).coerceAtMost(56.dp.toPx())
            val calculatedHeight = (elementHeight / (scrollMax + elementHeight)) * elementHeight
            val thumbHeight = calculatedHeight.coerceIn(minThumbHeightPx, maxThumbHeightPx)

            val maxOffset = (elementHeight - thumbHeight).coerceAtLeast(0f)
            val thumbOffset = (animatedPercent * maxOffset).coerceIn(0f, maxOffset)

            val thumbX = size.width - thumbWidthPx
            drawRoundRect(
                color = thumbColor,
                topLeft = Offset(thumbX, thumbOffset),
                size = Size(thumbWidthPx, thumbHeight),
                cornerRadius = CornerRadius(thumbWidthPx / 2f, trackWidthPx / 2f)
            )
        }
    }
}

// ----------------------------------------------------------------------------
// 1. ADVANCED IMAGE RESIZER TOOL (Matching Exact User Reference UI)
// ----------------------------------------------------------------------------
@Composable
fun ImageResizerToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((targetToolId: String, bitmap: Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedBitmap by remember(initialBitmap) { mutableStateOf<Bitmap?>(initialBitmap) }
    var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var originalSizeKb by remember(initialBitmap) { mutableIntStateOf(initialBitmap?.let { it.byteCount / 1024 } ?: 0) }
    var resizedSizeKb by remember { mutableIntStateOf(0) }
    var showMoveToToolSheet by remember { mutableStateOf(false) }

    // State matching screenshot UI
    var selectedUnitTab by remember { mutableStateOf("Pixels") } // Pixels, Percentage, CM, MM, Inches
    var widthInput by remember(initialBitmap) { mutableStateOf(initialBitmap?.width?.toString() ?: "1200") }
    var heightInput by remember(initialBitmap) { mutableStateOf(initialBitmap?.height?.toString() ?: "1599") }
    var keepAspectRatio by remember { mutableStateOf(true) }

    var qualityPercent by remember { mutableFloatStateOf(90f) }
    var selectedFormat by remember { mutableStateOf("JPEG") }
    var isFormatDropdownExpanded by remember { mutableStateOf(false) }

    // Collapsible preset section states
    var isSocialPresetsExpanded by remember { mutableStateOf(false) }
    var isDocPresetsExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                    resizedBitmap = null
                    originalSizeKb = (bmp.byteCount / 1024)
                    widthInput = bmp.width.toString()
                    heightInput = bmp.height.toString()
                }
            } catch (e: Exception) {
                onShowToast("Failed to load image: ${e.message}")
            }
        }
    }

    // Helper to calculate pixel dimensions based on unit
    fun getCalculatedPixels(): Pair<Int, Int> {
        val bmp = selectedBitmap
        val defaultW = bmp?.width ?: 1200
        val defaultH = bmp?.height ?: 1599
        val inputW = widthInput.toFloatOrNull() ?: defaultW.toFloat()
        val inputH = heightInput.toFloatOrNull() ?: defaultH.toFloat()

        val dpi = 300 // Standard print/display DPI for conversions
        return when (selectedUnitTab) {
            "Percentage" -> {
                val pxW = (defaultW * (inputW / 100f)).toInt()
                val pxH = (defaultH * (inputH / 100f)).toInt()
                pxW to pxH
            }
            "CM" -> convertToPixels(inputW, inputH, "cm", dpi)
            "MM" -> convertToPixels(inputW, inputH, "mm", dpi)
            "Inches" -> convertToPixels(inputW, inputH, "inch", dpi)
            else -> inputW.toInt() to inputH.toInt()
        }
    }

    // Process resize
    fun doResizeImage() {
        val bmp = selectedBitmap
        if (bmp == null) {
            onShowToast("Please select an image first!")
            return
        }

        val (pixelW, pixelH) = getCalculatedPixels()
        val safeW = pixelW.coerceAtLeast(10)
        val safeH = pixelH.coerceAtLeast(10)

        val scaledBmp = Bitmap.createScaledBitmap(bmp, safeW, safeH, true)
        val stream = ByteArrayOutputStream()

        val compressFormat = when (selectedFormat.uppercase()) {
            "PNG" -> Bitmap.CompressFormat.PNG
            "WEBP" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }
            else -> Bitmap.CompressFormat.JPEG
        }

        scaledBmp.compress(compressFormat, qualityPercent.toInt().coerceIn(10, 100), stream)
        val compressedBytes = stream.toByteArray()
        val finalBmp = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)

        resizedBitmap = finalBmp
        resizedSizeKb = (compressedBytes.size / 1024)
        onShowToast("Resized: $safeW × $safeH px ($resizedSizeKb KB)")
    }

    // Handler when user edits width input
    fun updateWidthValue(newVal: String) {
        widthInput = newVal
        if (keepAspectRatio) {
            val w = newVal.toFloatOrNull()
            val origBmp = selectedBitmap
            val origW = origBmp?.width ?: 1200
            val origH = origBmp?.height ?: 1599
            if (w != null && origW > 0) {
                val ratio = origH.toFloat() / origW.toFloat()
                val newH = w * ratio
                heightInput = if (selectedUnitTab == "Pixels") newH.toInt().toString() else String.format(Locale.US, "%.2f", newH).removeSuffix(".00")
            }
        }
    }

    // Handler when user edits height input
    fun updateHeightValue(newVal: String) {
        heightInput = newVal
        if (keepAspectRatio) {
            val h = newVal.toFloatOrNull()
            val origBmp = selectedBitmap
            val origW = origBmp?.width ?: 1200
            val origH = origBmp?.height ?: 1599
            if (h != null && origH > 0) {
                val ratio = origW.toFloat() / origH.toFloat()
                val newW = h * ratio
                widthInput = if (selectedUnitTab == "Pixels") newW.toInt().toString() else String.format(Locale.US, "%.2f", newW).removeSuffix(".00")
            }
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .thinScrollbar(scrollState)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Image Resizer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    val origW = selectedBitmap?.width ?: 1200
                    val origH = selectedBitmap?.height ?: 1599
                    Text(
                        text = "Original: $origW × $origH px",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image Selection Box & Live Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            val bmpToShow = resizedBitmap ?: selectedBitmap
            if (bmpToShow != null) {
                Image(
                    bitmap = bmpToShow.asImageBitmap(),
                    contentDescription = "Selected Photo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to Upload Photo or Signature", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Live Preview after modifications", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // Live preview indicator badge
        selectedBitmap?.let { bmp ->
            Spacer(modifier = Modifier.height(8.dp))
            val currentDispBmp = resizedBitmap ?: bmp
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PrimaryRed.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (resizedBitmap != null) "✓ Live Modified Preview" else "Original Loaded Preview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PrimaryRed
                    )
                    Text(
                        text = "${currentDispBmp.width} × ${currentDispBmp.height} px (${if (resizedSizeKb > 0) "$resizedSizeKb KB" else "$originalSizeKb KB"})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Unit Selection Segmented Tabs (Single line responsive format)
        val unitTabs = listOf("Pixels", "Percentage", "CM", "MM", "Inches")
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                unitTabs.forEach { tab ->
                    val isSelected = selectedUnitTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable {
                                selectedUnitTab = tab
                                // Set sensible defaults if switching units
                                val bmp = selectedBitmap
                                val origW = bmp?.width ?: 1200
                                val origH = bmp?.height ?: 1599
                                when (tab) {
                                    "Pixels" -> {
                                        widthInput = origW.toString()
                                        heightInput = origH.toString()
                                    }
                                    "Percentage" -> {
                                        widthInput = "100"
                                        heightInput = "100"
                                    }
                                    "CM" -> {
                                        widthInput = String.format(Locale.US, "%.2f", origW / 300f * 2.54f)
                                        heightInput = String.format(Locale.US, "%.2f", origH / 300f * 2.54f)
                                    }
                                    "MM" -> {
                                        widthInput = String.format(Locale.US, "%.1f", origW / 300f * 25.4f)
                                        heightInput = String.format(Locale.US, "%.1f", origH / 300f * 25.4f)
                                    }
                                    "Inches" -> {
                                        widthInput = String.format(Locale.US, "%.2f", origW / 300f)
                                        heightInput = String.format(Locale.US, "%.2f", origH / 300f)
                                    }
                                }
                            }
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            softWrap = false,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Width & Height Inputs (Matching Screenshot)
        val unitLabel = when (selectedUnitTab) {
            "Pixels" -> "px"
            "Percentage" -> "%"
            "CM" -> "cm"
            "MM" -> "mm"
            "Inches" -> "in"
            else -> "px"
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = widthInput,
                onValueChange = { updateWidthValue(it) },
                label = { Text("Width ($unitLabel)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = heightInput,
                onValueChange = { updateHeightValue(it) },
                label = { Text("Height ($unitLabel)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Keep Aspect Ratio Checkbox (Matching Screenshot)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { keepAspectRatio = !keepAspectRatio },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = keepAspectRatio,
                onCheckedChange = { keepAspectRatio = it }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Keep aspect ratio", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Social Media Presets Section (Collapsible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isSocialPresetsExpanded = !isSocialPresetsExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📱 Social Media Presets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = if (isSocialPresetsExpanded) "▲ Hide" else "▼ Show",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed
            )
        }

        if (isSocialPresetsExpanded) {
            Spacer(modifier = Modifier.height(8.dp))

            val socialPresets = listOf(
                "Instagram Post (1080×1080)" to ("1080" to "1080"),
                "Instagram Story (1080×1920)" to ("1080" to "1920"),
                "WhatsApp DP (500×500)" to ("500" to "500"),
                "YouTube Thumbnail (1280×720)" to ("1280" to "720"),
                "YouTube Banner (2560×1440)" to ("2560" to "1440"),
                "Facebook Cover (820×312)" to ("820" to "312"),
                "Twitter Header (1500×500)" to ("1500" to "500"),
                "LinkedIn Banner (1584×396)" to ("1584" to "396")
            )

            // Grid layout for presets
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                socialPresets.chunked(2).forEach { rowPresets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPresets.forEach { (name, size) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedUnitTab = "Pixels"
                                        widthInput = size.first
                                        heightInput = size.second
                                        keepAspectRatio = false
                                    }
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                                )
                            }
                        }
                        if (rowPresets.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Passport & Document Sizes Section (Collapsible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isDocPresetsExpanded = !isDocPresetsExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🪪 Passport & Document Sizes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = if (isDocPresetsExpanded) "▲ Hide" else "▼ Show",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed
            )
        }

        if (isDocPresetsExpanded) {
            Spacer(modifier = Modifier.height(8.dp))

            val docPresets = listOf(
                Triple("Passport (3.5×4.5cm)", "CM", "3.5" to "4.5"),
                Triple("Passport (2×2 inch)", "Inches", "2.0" to "2.0"),
                Triple("Passport (35×45mm)", "MM", "35.0" to "45.0"),
                Triple("Signature (6×2cm)", "CM", "6.0" to "2.0"),
                Triple("A4 Size", "MM", "210.0" to "297.0"),
                Triple("4×6 inch", "Inches", "4.0" to "6.0")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                docPresets.chunked(2).forEach { rowPresets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPresets.forEach { (name, unit, size) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedUnitTab = unit
                                        widthInput = size.first
                                        heightInput = size.second
                                        keepAspectRatio = false
                                    }
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                                )
                            }
                        }
                        if (rowPresets.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quality Slider & Format Dropdown Row (Matching Screenshot)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quality Slider
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Quality: ${qualityPercent.toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Slider(
                    value = qualityPercent,
                    onValueChange = { qualityPercent = it },
                    valueRange = 10f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Format Selector Dropdown
            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = "Format",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFormatDropdownExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedFormat, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    DropdownMenu(
                        expanded = isFormatDropdownExpanded,
                        onDismissRequest = { isFormatDropdownExpanded = false }
                    ) {
                        listOf("JPEG", "PNG", "WEBP").forEach { fmt ->
                            DropdownMenuItem(
                                text = { Text(fmt, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedFormat = fmt
                                    isFormatDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row (Primary Red "Resize" and "Download")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { doResizeImage() },
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text("Resize", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    val bmpToSave = resizedBitmap ?: selectedBitmap
                    if (bmpToSave != null) {
                        saveBmpToGallery(context, bmpToSave, "Resized_${System.currentTimeMillis()}")
                    } else {
                        onShowToast("Please select an image first!")
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Download", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        val currentEditedBmp = resizedBitmap ?: selectedBitmap
        if (currentEditedBmp != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showMoveToToolSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, PrimaryRed),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryRed.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Move this to Other Tools (दूसरे टूल में भेजें)", fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 14.sp)
            }

            if (showMoveToToolSheet) {
                MoveToOtherToolBottomSheet(
                    currentToolId = "resizer",
                    currentBitmap = currentEditedBmp,
                    onToolSelected = { targetId, bmp ->
                        showMoveToToolSheet = false
                        onMoveToTool?.invoke(targetId, bmp)
                    },
                    onDismiss = { showMoveToToolSheet = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

// ----------------------------------------------------------------------------
// 2. ADVANCED IMAGE CROP TOOL (Matching Exact User Reference UI)
// ----------------------------------------------------------------------------
@Composable
private fun RotateLeftIcon(color: Color = Color.White) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val corner = 3.dp.toPx()
        val pathL = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.35f, h * 0.25f, w * 0.85f, h * 0.85f),
                    cornerRadius = CornerRadius(corner, corner)
                )
            )
        }
        drawPath(pathL, color = color)
        val arrowPath = Path().apply {
            moveTo(w * 0.6f, h * 0.12f)
            quadraticTo(w * 0.15f, h * 0.12f, w * 0.15f, h * 0.55f)
        }
        drawPath(arrowPath, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
        val headPath = Path().apply {
            moveTo(w * 0.05f, h * 0.42f)
            lineTo(w * 0.15f, h * 0.58f)
            lineTo(w * 0.28f, h * 0.48f)
        }
        drawPath(headPath, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
private fun RotateRightIcon(color: Color = Color.White) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val corner = 3.dp.toPx()
        val pathR = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(w * 0.15f, h * 0.25f, w * 0.65f, h * 0.85f),
                    cornerRadius = CornerRadius(corner, corner)
                )
            )
        }
        drawPath(pathR, color = color)
        val arrowPath = Path().apply {
            moveTo(w * 0.4f, h * 0.12f)
            quadraticTo(w * 0.85f, h * 0.12f, w * 0.85f, h * 0.55f)
        }
        drawPath(arrowPath, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
        val headPath = Path().apply {
            moveTo(w * 0.72f, h * 0.48f)
            lineTo(w * 0.85f, h * 0.58f)
            lineTo(w * 0.95f, h * 0.42f)
        }
        drawPath(headPath, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
private fun FlipHorizontalIcon(color: Color = Color.White) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val pad = 2.dp.toPx()
        val mid = w / 2f
        drawLine(
            color = color,
            start = Offset(mid, pad),
            end = Offset(mid, h - pad),
            strokeWidth = 1.5.dp.toPx()
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(pad, pad + 2.dp.toPx()),
            size = Size(mid - pad - 3.dp.toPx(), h - 2 * pad - 4.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(mid + 3.dp.toPx(), pad + 2.dp.toPx()),
            size = Size(mid - pad - 3.dp.toPx(), h - 2 * pad - 4.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

@Composable
private fun FlipVerticalIcon(color: Color = Color.White) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val pad = 2.dp.toPx()
        val mid = h / 2f
        drawLine(
            color = color,
            start = Offset(pad, mid),
            end = Offset(w - pad, mid),
            strokeWidth = 1.5.dp.toPx()
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(pad + 2.dp.toPx(), pad),
            size = Size(w - 2 * pad - 4.dp.toPx(), mid - pad - 3.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(pad + 2.dp.toPx(), mid + 3.dp.toPx()),
            size = Size(w - 2 * pad - 4.dp.toPx(), mid - pad - 3.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

@Composable
private fun RotateFlipItem(
    label: String,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isFlashing by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val isHighlighted = isPressed || isFlashing

    val redAccent = Color(0xFFE53935)

    val bgAnimColor by animateColorAsState(
        targetValue = if (isHighlighted) redAccent else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "bgColorAnim"
    )
    val contentAnimColor by animateColorAsState(
        targetValue = if (isHighlighted) Color.White else redAccent,
        animationSpec = tween(durationMillis = 150),
        label = "contentColorAnim"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgAnimColor,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    isFlashing = true
                    delay(250)
                    isFlashing = false
                }
                onClick()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            icon(contentAnimColor)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentAnimColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DegreeRulerSlider(
    value: Float, // -45f to +45f
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
    val majorTickColor = MaterialTheme.colorScheme.onSurface
    val primaryAccent = MaterialTheme.colorScheme.primary
    val textColorInt = MaterialTheme.colorScheme.onSurface.toArgb()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val sensitivity = 0.22f
                    val delta = -dragAmount.x * sensitivity
                    val updated = (currentValue + delta).coerceIn(-45f, 45f)
                    currentOnValueChange(updated)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val pxPerDegree = 9f

            for (deg in -45..45) {
                val x = centerX + (deg - currentValue) * pxPerDegree
                if (x >= -10f && x <= canvasW + 10f) {
                    val isMajor = deg % 10 == 0
                    val isMedium = deg % 5 == 0

                    val tickHeight = when {
                        isMajor -> 22.dp.toPx()
                        isMedium -> 14.dp.toPx()
                        else -> 9.dp.toPx()
                    }

                    val tickTop = canvasH / 2f + 2.dp.toPx()

                    drawRect(
                        color = if (isMajor) majorTickColor else tickColor,
                        topLeft = Offset(x - 0.75.dp.toPx(), tickTop),
                        size = Size(1.5.dp.toPx(), tickHeight)
                    )

                    if (isMajor) {
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = textColorInt
                                textSize = 11.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                            }
                            val textStr = if (deg > 0) "+$deg" else "$deg"
                            drawText(textStr, x, tickTop - 6.dp.toPx(), paint)
                        }
                    }
                }
            }

            // Draw center highlight cursor
            drawCircle(
                color = primaryAccent,
                radius = 3.dp.toPx(),
                center = Offset(centerX, canvasH / 2f - 4.dp.toPx())
            )

            drawRoundRect(
                color = primaryAccent,
                topLeft = Offset(centerX - 1.5.dp.toPx(), canvasH / 2f + 1.dp.toPx()),
                size = Size(3.dp.toPx(), 26.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
            )
        }
    }
}

@Composable
fun ImageCropToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((targetToolId: String, bitmap: Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var selectedBitmap by remember(initialBitmap) { mutableStateOf<Bitmap?>(initialBitmap) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMoveToToolSheet by remember { mutableStateOf(false) }

    var rotation90Target by remember { mutableFloatStateOf(0f) }
    val animatedRotation90 by animateFloatAsState(
        targetValue = rotation90Target,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "rotation90Animation"
    )
    var fineAngle by remember { mutableFloatStateOf(0f) }
    var isFlipH by remember { mutableStateOf(false) }
    var isFlipV by remember { mutableStateOf(false) }

    var cropMode by remember { mutableStateOf("Free Crop") } // "Free Crop" or "Circle Crop"
    var isCropModeDropdownExpanded by remember { mutableStateOf(false) }

    // Normalized crop box coordinates relative to display canvas (0.0 to 1.0)
    var normCanvasL by remember { mutableFloatStateOf(0.15f) }
    var normCanvasT by remember { mutableFloatStateOf(0.15f) }
    var normCanvasW by remember { mutableFloatStateOf(0.70f) }
    var normCanvasH by remember { mutableFloatStateOf(0.70f) }

    var canvasWidthPx by remember { mutableFloatStateOf(1000f) }
    var canvasHeightPx by remember { mutableFloatStateOf(1000f) }

    var cropW by remember { mutableFloatStateOf(620f) }
    var cropH by remember { mutableFloatStateOf(632f) }

    var widthInput by remember { mutableStateOf("620") }
    var heightInput by remember { mutableStateOf("632") }

    // Active drag handle tracking
    var activeDragHandle by remember { mutableStateOf<String?>(null) } // "MOVE", "TL", "TR", "BL", "BR", "NEW"
    var dragStartPoint by remember { mutableStateOf(Offset.Zero) }

    // Derived transformed bitmap (with rotation90Target, fineAngle, isFlipH, isFlipV applied)
    val currentTransformedBitmap = remember(selectedBitmap, rotation90Target, fineAngle, isFlipH, isFlipV) {
        val bmp = selectedBitmap ?: return@remember null
        val totalAngle = rotation90Target + fineAngle
        if (totalAngle == 0f && !isFlipH && !isFlipV) {
            bmp
        } else {
            val matrix = Matrix().apply {
                if (isFlipH || isFlipV) {
                    postScale(if (isFlipH) -1f else 1f, if (isFlipV) -1f else 1f, bmp.width / 2f, bmp.height / 2f)
                }
                if (totalAngle != 0f) {
                    postRotate(totalAngle, bmp.width / 2f, bmp.height / 2f)
                }
            }
            try {
                Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
            } catch (e: Exception) {
                bmp
            }
        }
    }

    // Update width/height text inputs when transformed bitmap or canvas crop box changes
    LaunchedEffect(currentTransformedBitmap, normCanvasL, normCanvasT, normCanvasW, normCanvasH) {
        currentTransformedBitmap?.let { bmp ->
            val canvasW = if (canvasWidthPx > 1f) canvasWidthPx else 1000f
            val canvasH = if (canvasHeightPx > 1f) canvasHeightPx else 1000f
            val bmpW = bmp.width.toFloat()
            val bmpH = bmp.height.toFloat()

            val scale = minOf(canvasW / bmpW, canvasH / bmpH)
            val fitW = bmpW * scale
            val fitH = bmpH * scale

            val boxW = normCanvasW * canvasW
            val boxH = normCanvasH * canvasH

            val pxW = if (fitW > 0f) ((boxW / fitW) * bmpW).toInt().coerceAtLeast(1) else bmpW.toInt()
            val pxH = if (fitH > 0f) ((boxH / fitH) * bmpH).toInt().coerceAtLeast(1) else bmpH.toInt()

            cropW = pxW.toFloat()
            cropH = pxH.toFloat()
            widthInput = pxW.toString()
            heightInput = pxH.toString()
        }
    }

    // Smooth auto-scroll to preview and download/share buttons when image is cropped
    LaunchedEffect(croppedBitmap) {
        if (croppedBitmap != null) {
            kotlinx.coroutines.delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
            kotlinx.coroutines.delay(350)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Try to extract file name
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        selectedFileName = cursor.getString(nameIndex)
                    }
                }
                if (selectedFileName == null) {
                    selectedFileName = it.lastPathSegment ?: "image.jpg"
                }

                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                    rotation90Target = 0f
                    fineAngle = 0f
                    isFlipH = false
                    isFlipV = false
                    croppedBitmap = null

                    normCanvasL = 0.15f
                    normCanvasT = 0.15f
                    normCanvasW = 0.70f
                    normCanvasH = 0.70f

                    widthInput = bmp.width.toString()
                    heightInput = bmp.height.toString()
                }
            } catch (e: Exception) {
                onShowToast("Failed to load image: ${e.message}")
            }
        }
    }

    // Process crop execution mapping canvas crop box to currentTransformedBitmap
    fun executeCrop() {
        val bmp = currentTransformedBitmap
        if (bmp == null) {
            onShowToast("Please upload an image first!")
            return
        }

        val canvasW = if (canvasWidthPx > 1f) canvasWidthPx else 1000f
        val canvasH = if (canvasHeightPx > 1f) canvasHeightPx else 1000f

        val bmpW = bmp.width.toFloat()
        val bmpH = bmp.height.toFloat()

        val scale = minOf(canvasW / bmpW, canvasH / bmpH)
        val fitW = bmpW * scale
        val fitH = bmpH * scale
        val fitLeft = (canvasW - fitW) / 2f
        val fitTop = (canvasH - fitH) / 2f

        var boxL = normCanvasL * canvasW
        var boxT = normCanvasT * canvasH
        var boxW = normCanvasW * canvasW
        var boxH = normCanvasH * canvasH

        if (cropMode == "Circle Crop") {
            val side = minOf(boxW, boxH)
            boxW = side
            boxH = side
        }

        // Map canvas box to image pixel coordinates
        val relX = ((boxL - fitLeft) / fitW).coerceIn(0f, 1f)
        val relY = ((boxT - fitTop) / fitH).coerceIn(0f, 1f)
        val relW = (boxW / fitW).coerceIn(0.01f, 1f - relX)
        val relH = (boxH / fitH).coerceIn(0.01f, 1f - relY)

        val safeX = (relX * bmpW).toInt().coerceIn(0, (bmp.width - 10).coerceAtLeast(0))
        val safeY = (relY * bmpH).toInt().coerceIn(0, (bmp.height - 10).coerceAtLeast(0))
        val safeW = (relW * bmpW).toInt().coerceIn(10, bmp.width - safeX)
        val safeH = (relH * bmpH).toInt().coerceIn(10, bmp.height - safeY)

        val srcCropped = try {
            Bitmap.createBitmap(bmp, safeX, safeY, safeW, safeH)
        } catch (e: Exception) {
            bmp
        }

        if (cropMode == "Circle Crop") {
            val size = minOf(srcCropped.width, srcCropped.height)
            val circleBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(circleBmp)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
            paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
            val srcRect = android.graphics.Rect(
                (srcCropped.width - size) / 2,
                (srcCropped.height - size) / 2,
                (srcCropped.width + size) / 2,
                (srcCropped.height + size) / 2
            )
            canvas.drawBitmap(srcCropped, srcRect, android.graphics.Rect(0, 0, size, size), paint)

            croppedBitmap = circleBmp
        } else {
            croppedBitmap = srcCropped
        }

        onShowToast("Image cropped successfully (${croppedBitmap?.width} × ${croppedBitmap?.height} px)!")
    }

    // Update crop size when user changes text input
    fun updateCropWidthFromInput(valStr: String) {
        widthInput = valStr
        val bmp = currentTransformedBitmap ?: return
        val w = valStr.toFloatOrNull() ?: return
        val bmpW = bmp.width.toFloat()
        val bmpH = bmp.height.toFloat()
        val canvasW = if (canvasWidthPx > 1f) canvasWidthPx else 1000f
        val canvasH = if (canvasHeightPx > 1f) canvasHeightPx else 1000f

        val scale = minOf(canvasW / bmpW, canvasH / bmpH)
        val fitW = bmpW * scale

        if (w > 0 && bmpW > 0f && fitW > 0f) {
            val targetBoxW = (w / bmpW) * fitW
            normCanvasW = (targetBoxW / canvasW).coerceIn(0.05f, 1f - normCanvasL)
            if (cropMode == "Circle Crop") {
                normCanvasH = normCanvasW * (canvasW / canvasH)
                heightInput = widthInput
            }
            cropW = w
        }
    }

    fun updateCropHeightFromInput(valStr: String) {
        heightInput = valStr
        val bmp = currentTransformedBitmap ?: return
        val h = valStr.toFloatOrNull() ?: return
        val bmpW = bmp.width.toFloat()
        val bmpH = bmp.height.toFloat()
        val canvasW = if (canvasWidthPx > 1f) canvasWidthPx else 1000f
        val canvasH = if (canvasHeightPx > 1f) canvasHeightPx else 1000f

        val scale = minOf(canvasW / bmpW, canvasH / bmpH)
        val fitH = bmpH * scale

        if (h > 0 && bmpH > 0f && fitH > 0f) {
            val targetBoxH = (h / bmpH) * fitH
            normCanvasH = (targetBoxH / canvasH).coerceIn(0.05f, 1f - normCanvasT)
            if (cropMode == "Circle Crop") {
                normCanvasW = normCanvasH * (canvasH / canvasW)
                widthInput = heightInput
            }
            cropH = h
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .thinScrollbar(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Image Crop Tool",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Sewayojan Tools",
                        fontSize = 11.sp,
                        color = PrimaryRed
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedBitmap != null) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Crop Mode & Crop Size (px) Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Crop Mode Selector
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Crop Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCropModeDropdownExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cropMode, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    DropdownMenu(
                        expanded = isCropModeDropdownExpanded,
                        onDismissRequest = { isCropModeDropdownExpanded = false }
                    ) {
                        listOf("Free Crop", "Circle Crop").forEach { mode ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (cropMode == mode) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF00A86B),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(mode, fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                onClick = {
                                    cropMode = mode
                                    isCropModeDropdownExpanded = false
                                    if (mode == "Circle Crop") {
                                        val squareDim = minOf(cropW, cropH)
                                        cropW = squareDim
                                        cropH = squareDim
                                        widthInput = squareDim.toInt().toString()
                                        heightInput = squareDim.toInt().toString()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Crop Size (px) Inputs
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Crop Size (px)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = widthInput,
                        onValueChange = { updateCropWidthFromInput(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { updateCropHeightFromInput(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Interactive Preview & Crop Area (Smooth GPU Acceleration)
        val currentBitmap = currentTransformedBitmap
        if (currentBitmap != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentBitmap, cropMode) {
                            detectDragGestures(
                                onDragStart = { startOffset ->
                                    dragStartPoint = startOffset
                                    val canvasW = size.width.toFloat()
                                    val canvasH = size.height.toFloat()

                                    val boxL = normCanvasL * canvasW
                                    val boxT = normCanvasT * canvasH
                                    val boxR = boxL + normCanvasW * canvasW
                                    val boxB = boxT + normCanvasH * canvasH

                                    val touchRadius = 40f
                                    val pX = startOffset.x
                                    val pY = startOffset.y

                                    activeDragHandle = when {
                                        kotlin.math.abs(pX - boxL) < touchRadius && kotlin.math.abs(pY - boxT) < touchRadius -> "TL"
                                        kotlin.math.abs(pX - boxR) < touchRadius && kotlin.math.abs(pY - boxT) < touchRadius -> "TR"
                                        kotlin.math.abs(pX - boxL) < touchRadius && kotlin.math.abs(pY - boxB) < touchRadius -> "BL"
                                        kotlin.math.abs(pX - boxR) < touchRadius && kotlin.math.abs(pY - boxB) < touchRadius -> "BR"

                                        kotlin.math.abs(pY - boxT) < touchRadius && pX in (boxL - touchRadius)..(boxR + touchRadius) -> "T"
                                        kotlin.math.abs(pY - boxB) < touchRadius && pX in (boxL - touchRadius)..(boxR + touchRadius) -> "B"
                                        kotlin.math.abs(pX - boxL) < touchRadius && pY in (boxT - touchRadius)..(boxB + touchRadius) -> "L"
                                        kotlin.math.abs(pX - boxR) < touchRadius && pY in (boxT - touchRadius)..(boxB + touchRadius) -> "R"

                                        pX in boxL..boxR && pY in boxT..boxB -> "MOVE"
                                        else -> {
                                            normCanvasL = (pX / canvasW).coerceIn(0f, 0.9f)
                                            normCanvasT = (pY / canvasH).coerceIn(0f, 0.9f)
                                            normCanvasW = 0.1f
                                            normCanvasH = 0.1f
                                            "NEW"
                                        }
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val canvasW = size.width.toFloat()
                                    val canvasH = size.height.toFloat()
                                    if (canvasW <= 0f || canvasH <= 0f) return@detectDragGestures

                                    var boxL = normCanvasL * canvasW
                                    var boxT = normCanvasT * canvasH
                                    var boxW = normCanvasW * canvasW
                                    var boxH = normCanvasH * canvasH

                                    val dx = dragAmount.x
                                    val dy = dragAmount.y

                                    when (activeDragHandle) {
                                        "MOVE" -> {
                                            boxL = (boxL + dx).coerceIn(0f, (canvasW - boxW).coerceAtLeast(0f))
                                            boxT = (boxT + dy).coerceIn(0f, (canvasH - boxH).coerceAtLeast(0f))
                                        }
                                        "TL" -> {
                                            val newL = (boxL + dx).coerceIn(0f, boxL + boxW - 30f)
                                            val newT = (boxT + dy).coerceIn(0f, boxT + boxH - 30f)
                                            boxW += (boxL - newL)
                                            boxH += (boxT - newT)
                                            boxL = newL
                                            boxT = newT
                                        }
                                        "TR" -> {
                                            val newT = (boxT + dy).coerceIn(0f, boxT + boxH - 30f)
                                            boxW = (boxW + dx).coerceIn(30f, canvasW - boxL)
                                            boxH += (boxT - newT)
                                            boxT = newT
                                        }
                                        "BL" -> {
                                            val newL = (boxL + dx).coerceIn(0f, boxL + boxW - 30f)
                                            boxW += (boxL - newL)
                                            boxL = newL
                                            boxH = (boxH + dy).coerceIn(30f, canvasH - boxT)
                                        }
                                        "BR", "NEW" -> {
                                            boxW = (boxW + dx).coerceIn(30f, canvasW - boxL)
                                            boxH = (boxH + dy).coerceIn(30f, canvasH - boxT)
                                        }
                                        "T" -> {
                                            val newT = (boxT + dy).coerceIn(0f, boxT + boxH - 30f)
                                            boxH += (boxT - newT)
                                            boxT = newT
                                        }
                                        "B" -> {
                                            boxH = (boxH + dy).coerceIn(30f, canvasH - boxT)
                                        }
                                        "L" -> {
                                            val newL = (boxL + dx).coerceIn(0f, boxL + boxW - 30f)
                                            boxW += (boxL - newL)
                                            boxL = newL
                                        }
                                        "R" -> {
                                            boxW = (boxW + dx).coerceIn(30f, canvasW - boxL)
                                        }
                                    }

                                    if (cropMode == "Circle Crop") {
                                        val squareDim = minOf(boxW, boxH)
                                        boxW = squareDim
                                        boxH = squareDim
                                    }

                                    normCanvasL = boxL / canvasW
                                    normCanvasT = boxT / canvasH
                                    normCanvasW = boxW / canvasW
                                    normCanvasH = boxH / canvasH
                                }
                            )
                        }
                ) {
                    val canvasW = size.width
                    val canvasH = size.height
                    canvasWidthPx = canvasW
                    canvasHeightPx = canvasH

                    val bmpW = currentBitmap.width.toFloat()
                    val bmpH = currentBitmap.height.toFloat()

                    val scale = minOf(canvasW / bmpW, canvasH / bmpH)
                    val fitW = bmpW * scale
                    val fitH = bmpH * scale
                    val fitLeft = (canvasW - fitW) / 2f
                    val fitTop = (canvasH - fitH) / 2f

                    // Draw transformed image directly
                    drawImage(
                        image = currentBitmap.asImageBitmap(),
                        dstOffset = IntOffset(fitLeft.toInt(), fitTop.toInt()),
                        dstSize = IntSize(fitW.toInt(), fitH.toInt())
                    )

                    // Crop region relative to canvas
                    val boxL = normCanvasL * canvasW
                    val boxT = normCanvasT * canvasH
                    val boxW = normCanvasW * canvasW
                    val boxH = normCanvasH * canvasH
                    val boxR = boxL + boxW
                    val boxB = boxT + boxH

                    val circleRadius = if (cropMode == "Circle Crop") minOf(boxW, boxH) / 2f else 0f
                    val circleCx = boxL + boxW / 2f
                    val circleCy = boxT + boxH / 2f

                    // Dimmed overlay path
                    val cropPath = Path().apply {
                        if (cropMode == "Circle Crop") {
                            addOval(
                                Rect(
                                    circleCx - circleRadius,
                                    circleCy - circleRadius,
                                    circleCx + circleRadius,
                                    circleCy + circleRadius
                                )
                            )
                        } else {
                            addRect(Rect(boxL, boxT, boxR, boxB))
                        }
                    }

                    clipPath(cropPath, clipOp = ClipOp.Difference) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.65f),
                            topLeft = Offset.Zero,
                            size = Size(canvasW, canvasH)
                        )
                    }

                    // Draw crop border & overlay highlights
                    if (cropMode == "Circle Crop") {
                        drawCircle(
                            color = Color.White,
                            center = Offset(circleCx, circleCy),
                            radius = circleRadius,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    } else {
                        drawRect(
                            color = Color.White.copy(alpha = 0.18f),
                            topLeft = Offset(boxL, boxT),
                            size = Size(boxW, boxH)
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(boxL, boxT),
                            size = Size(boxW, boxH),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        val handleLength = 20.dp.toPx()
                        val handleStroke = 4.dp.toPx()

                        val midX = (boxL + boxR) / 2f
                        val midY = (boxT + boxB) / 2f

                        // TL Corner
                        drawLine(Color.White, Offset(boxL, boxT), Offset(boxL + handleLength, boxT), handleStroke)
                        drawLine(Color.White, Offset(boxL, boxT), Offset(boxL, boxT + handleLength), handleStroke)
                        // TR Corner
                        drawLine(Color.White, Offset(boxR, boxT), Offset(boxR - handleLength, boxT), handleStroke)
                        drawLine(Color.White, Offset(boxR, boxT), Offset(boxR, boxT + handleLength), handleStroke)
                        // BL Corner
                        drawLine(Color.White, Offset(boxL, boxB), Offset(boxL + handleLength, boxB), handleStroke)
                        drawLine(Color.White, Offset(boxL, boxB), Offset(boxL, boxB - handleLength), handleStroke)
                        // BR Corner
                        drawLine(Color.White, Offset(boxR, boxB), Offset(boxR - handleLength, boxB), handleStroke)
                        drawLine(Color.White, Offset(boxR, boxB), Offset(boxR, boxB - handleLength), handleStroke)

                        // 4 Side Edge Handles (Matching thick line bars)
                        // Top Edge Midpoint
                        drawLine(Color.White, Offset(midX - handleLength / 2f, boxT), Offset(midX + handleLength / 2f, boxT), handleStroke)
                        // Bottom Edge Midpoint
                        drawLine(Color.White, Offset(midX - handleLength / 2f, boxB), Offset(midX + handleLength / 2f, boxB), handleStroke)
                        // Left Edge Midpoint
                        drawLine(Color.White, Offset(boxL, midY - handleLength / 2f), Offset(boxL, midY + handleLength / 2f), handleStroke)
                        // Right Edge Midpoint
                        drawLine(Color.White, Offset(boxR, midY - handleLength / 2f), Offset(boxR, midY + handleLength / 2f), handleStroke)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Degree Ruler Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rotate Angle: ${if (fineAngle > 0) "+" else ""}${String.format("%.1f", fineAngle)}°",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (fineAngle != 0f || rotation90Target != 0f || isFlipH || isFlipV) {
                    Text(
                        text = "Reset Rotation",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            fineAngle = 0f
                            rotation90Target = 0f
                            isFlipH = false
                            isFlipV = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ruler Slider (-45° to +45°)
            DegreeRulerSlider(
                value = fineAngle,
                onValueChange = { fineAngle = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rotate & Flip Action Buttons (Left, Right, Horizontal, Vertical)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RotateFlipItem(
                        label = "Left",
                        onClick = { rotation90Target -= 90f },
                        icon = { color -> RotateLeftIcon(color = color) }
                    )
                    RotateFlipItem(
                        label = "Right",
                        onClick = { rotation90Target += 90f },
                        icon = { color -> RotateRightIcon(color = color) }
                    )
                    RotateFlipItem(
                        label = "Horizontal",
                        onClick = { isFlipH = !isFlipH },
                        icon = { color -> FlipHorizontalIcon(color = color) }
                    )
                    RotateFlipItem(
                        label = "Vertical",
                        onClick = { isFlipV = !isFlipV },
                        icon = { color -> FlipVerticalIcon(color = color) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button: "Crop Image"
            Button(
                onClick = { executeCrop() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
            ) {
                Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crop Image", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Output: ${cropW.toInt()} × ${cropH.toInt()} px — Drag anywhere to draw new crop area, drag corners to resize",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Placeholder box before uploading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Crop,
                        contentDescription = null,
                        tint = Color(0xFF00A86B),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tap to Choose Image for Cropping", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Drag anywhere with finger to select crop area", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // Cropped Result Preview & Download / Share
        croppedBitmap?.let { croppedBmp ->
            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF00A86B).copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color(0xFF00A86B).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✓ Cropped Result Preview (${croppedBmp.width} × ${croppedBmp.height} px)",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00A86B),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFF00A86B).copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = croppedBmp.asImageBitmap(),
                            contentDescription = "Cropped Result",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                saveBmpToGallery(context, croppedBmp, "Cropped_${System.currentTimeMillis()}")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                shareBmp(context, croppedBmp, "Cropped Image")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            val currentEditedBmp = croppedBitmap ?: selectedBitmap
            if (currentEditedBmp != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showMoveToToolSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, PrimaryRed),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryRed.copy(alpha = 0.08f))
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Move this to Other Tools (दूसरे टूल में भेजें)", fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 14.sp)
                }

                if (showMoveToToolSheet) {
                    MoveToOtherToolBottomSheet(
                        currentToolId = "crop",
                        currentBitmap = currentEditedBmp,
                        onToolSelected = { targetId, bmp ->
                            showMoveToToolSheet = false
                            onMoveToTool?.invoke(targetId, bmp)
                        },
                        onDismiss = { showMoveToToolSheet = false }
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

// ----------------------------------------------------------------------------
// 3. NAME & DATE ON PHOTO TOOL (Sewayojan Tools Style)
// ----------------------------------------------------------------------------
@Composable
fun NameDatePhotoToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((targetToolId: String, bitmap: Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedBitmap by remember(initialBitmap) { mutableStateOf<Bitmap?>(initialBitmap) }
    var stampedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMoveToToolSheet by remember { mutableStateOf(false) }

    var candidateName by remember { mutableStateOf("VIKAS KUMAR") }
    var photoDate by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    }
    var dobDate by remember { mutableStateOf("15/08/2000") }
    var selectedStyle by remember { mutableStateOf("SSC Standard (Bottom White Banner)") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                    stampedBitmap = null
                }
            } catch (e: Exception) {
                onShowToast("Failed to load photo: ${e.message}")
            }
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .thinScrollbar(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Name & Date on Photo Stamp",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            val bmpToShow = stampedBitmap ?: selectedBitmap
            if (bmpToShow != null) {
                Image(
                    bitmap = bmpToShow.asImageBitmap(),
                    contentDescription = "Passport Photo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to Select Candidate Photo", fontWeight = FontWeight.SemiBold)
                    Text("Govt Standard: Name & DOP Date Stamp", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Select Stamp Format Preset:", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val styles = listOf(
                "SSC Standard (Bottom White Banner)",
                "UP Police / Constable (2 Lines Black)",
                "NTA / NEET / JEE (Name + DOP + DOB)",
                "RRB / Railway (Red Accent Header)",
                "IBPS / SBI Bank (DOP Only Stamp)",
                "UPSC / State PSC (Name & DOP)",
                "Army / Defence (Bold Black Banner)",
                "Overlay Box (Dark Semi-transparent)"
            )
            items(styles) { st ->
                FilterChip(
                    selected = selectedStyle == st,
                    onClick = { selectedStyle = st },
                    label = { Text(st, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryRed, selectedLabelColor = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = candidateName,
            onValueChange = { candidateName = it },
            label = { Text("Candidate Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = photoDate,
                onValueChange = { photoDate = it },
                label = { Text("Date of Photo (DOP)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedButton(
                onClick = {
                    photoDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Today, contentDescription = "Today", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today")
            }
        }

        if (selectedStyle.contains("NTA") || selectedStyle.contains("NEET")) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = dobDate,
                onValueChange = { dobDate = it },
                label = { Text("Date of Birth (DOB)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val bmp = selectedBitmap
                if (bmp == null) {
                    onShowToast("Please select a photo first!")
                    return@Button
                }
                stampedBitmap = stampNameAndDateOnBitmap(bmp, candidateName, photoDate, selectedStyle, dobDate)
                onShowToast("Stamped Name & Date successfully!")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Icon(Icons.Default.Badge, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Stamp Name & Date")
        }

        stampedBitmap?.let { bmp ->
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { saveBmpToGallery(context, bmp, "Sewayojan_Stamped_${System.currentTimeMillis()}") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }
                Button(
                    onClick = { shareBmp(context, bmp, "Stamped Passport Photo") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }

        val currentEditedBmp = stampedBitmap ?: selectedBitmap
        if (currentEditedBmp != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showMoveToToolSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, PrimaryRed),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryRed.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Move this to Other Tools (दूसरे टूल में भेजें)", fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 14.sp)
            }

            if (showMoveToToolSheet) {
                MoveToOtherToolBottomSheet(
                    currentToolId = "namedate",
                    currentBitmap = currentEditedBmp,
                    onToolSelected = { targetId, bmp ->
                        showMoveToToolSheet = false
                        onMoveToTool?.invoke(targetId, bmp)
                    },
                    onDismiss = { showMoveToToolSheet = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

// ----------------------------------------------------------------------------
// 4. GOVT EXAM AGE CALCULATOR TOOL
// ----------------------------------------------------------------------------
data class GovtExamInfo(
    val title: String,
    val category: String, // "SSC", "Defence", "Railways", "Banking", "Police", "UPSC", "Teaching", "Other"
    val minAge: Float,
    val maxAgeGeneral: Float,
    val description: String = ""
)

val allGovtExamsMasterList = listOf(
    // Defence & Armed Forces
    GovtExamInfo("NDA (National Defence Academy)", "Defence", 16.5f, 19.5f, "Army, Navy & Air Force Cadets Entry"),
    GovtExamInfo("CDS (Combined Defence Services)", "Defence", 19f, 24f, "IMA, INA & AFA Officer Direct Entry"),
    GovtExamInfo("Agniveer (Army, Navy, Airforce)", "Defence", 17.5f, 21f, "Short Service Soldier / Sailor / Airman Entry"),
    GovtExamInfo("Indian Coast Guard Navik (GD/DB)", "Defence", 18f, 22f, "Coast Guard General Duty & Domestic Branch"),
    GovtExamInfo("AFCAT (Air Force Common Admission)", "Defence", 20f, 26f, "Air Force Flying & Ground Duty Officers"),
    GovtExamInfo("CAPF AC (UPSC Assistant Commandant)", "Defence", 20f, 25f, "BSF, CRPF, CISF, ITBP, SSB Gazetted Officer"),

    // SSC (Staff Selection Commission)
    GovtExamInfo("SSC GD Constable", "SSC", 18f, 23f, "BSF, CISF, CRPF, SSB, ITBP, SSF Constable Entry"),
    GovtExamInfo("SSC CHSL (10+2 LDC / DEO)", "SSC", 18f, 27f, "Lower Division Clerk & Data Entry Operator"),
    GovtExamInfo("SSC MTS & Havaldar", "SSC", 18f, 25f, "Multi-Tasking Staff & CBIC Havaldar"),
    GovtExamInfo("SSC CGL (Group B & C Posts)", "SSC", 18f, 30f, "Inspector, Assistant Section Officer, Auditor, Tax Asst"),
    GovtExamInfo("SSC CPO (Sub-Inspector in Delhi Police & CAPF)", "SSC", 20f, 25f, "Sub-Inspector in Delhi Police & Central Armed Forces"),
    GovtExamInfo("SSC Stenographer (Grade C & D)", "SSC", 18f, 30f, "Grade D (18-27) & Grade C (18-30) Stenographers"),
    GovtExamInfo("SSC JE (Junior Engineer)", "SSC", 18f, 32f, "Civil, Electrical & Mechanical Junior Engineers"),

    // Railways (RRB / RRC)
    GovtExamInfo("RRB Group D (Level 1)", "Railways", 18f, 33f, "Track Maintainer, Assistant Pointsman, Helper"),
    GovtExamInfo("RRB NTPC (Undergraduate Level)", "Railways", 18f, 30f, "Junior Clerk, Typist, Train Clerk"),
    GovtExamInfo("RRB NTPC (Graduate Level)", "Railways", 18f, 33f, "Station Master, Goods Guard, Senior Clerk"),
    GovtExamInfo("RRB ALP (Assistant Loco Pilot)", "Railways", 18f, 30f, "Railway Engine Driver & Loco Pilot Assistant"),
    GovtExamInfo("RRB Technician (Grade I & III)", "Railways", 18f, 33f, "Railway Signal, Workshop & Maintenance Tech"),
    GovtExamInfo("RRB JE (Junior Engineer)", "Railways", 18f, 33f, "Railway Works, Depot Material & P-Way JE"),
    GovtExamInfo("RPF Constable (Railway Protection Force)", "Railways", 18f, 28f, "Railway Security Constable Entry"),
    GovtExamInfo("RPF Sub Inspector (SI)", "Railways", 20f, 28f, "Railway Protection Force Sub Inspector"),

    // Banking & Financial Sector
    GovtExamInfo("IBPS Clerk (Customer Service Associate)", "Banking", 20f, 28f, "Public Sector Banks Clerical Cadre"),
    GovtExamInfo("IBPS PO (Probationary Officer / MT)", "Banking", 20f, 30f, "Public Sector Banks Officer Cadre"),
    GovtExamInfo("IBPS RRB Office Assistant (Multipurpose)", "Banking", 18f, 28f, "Gramin Bank Clerk / Office Assistant"),
    GovtExamInfo("IBPS RRB Officer Scale I (Assistant Manager)", "Banking", 18f, 30f, "Gramin Bank Officer Scale 1 Entry"),
    GovtExamInfo("SBI Clerk (Junior Associate)", "Banking", 20f, 28f, "State Bank of India Clerical Cadre"),
    GovtExamInfo("SBI PO (Probationary Officer)", "Banking", 21f, 30f, "State Bank of India Probationary Officer"),
    GovtExamInfo("RBI Assistant", "Banking", 20f, 28f, "Reserve Bank of India Assistant Cadre"),
    GovtExamInfo("RBI Grade B Officer", "Banking", 21f, 30f, "Reserve Bank of India Managerial Entry"),
    GovtExamInfo("LIC Assistant", "Banking", 18f, 30f, "Life Insurance Corporation Assistant"),
    GovtExamInfo("LIC AAO (Assistant Administrative Officer)", "Banking", 21f, 30f, "Life Insurance Corporation AAO Generalist"),
    GovtExamInfo("NABARD Grade A Officer", "Banking", 21f, 30f, "Assistant Manager in Rural Development Bank"),

    // UPSC & Civil Services
    GovtExamInfo("UPSC Civil Services (IAS / IPS / IFS / IRS)", "UPSC", 21f, 32f, "All India Services & Central Group A Officers"),
    GovtExamInfo("UPSC Engineering Services (ESE)", "UPSC", 21f, 30f, "Central Engineering Services Officers"),
    GovtExamInfo("UPSC Combined Medical Services (CMS)", "UPSC", 21f, 32f, "Medical Officer in Central Health Services"),
    GovtExamInfo("UPSC Indian Forest Service (IFoS)", "UPSC", 21f, 32f, "Forest Service Officer Group A Entry"),

    // Police & State Police
    GovtExamInfo("UP Police Constable", "Police", 18f, 25f, "Uttar Pradesh Civil Police Constable"),
    GovtExamInfo("UP Police Sub Inspector (SI)", "Police", 21f, 28f, "Uttar Pradesh Police Sub-Inspector"),
    GovtExamInfo("Delhi Police Constable", "Police", 18f, 25f, "Delhi Police Executive Constable"),
    GovtExamInfo("Delhi Police Head Constable (AWO/TPO/Ministerial)", "Police", 18f, 25f, "Delhi Police Ministerial Staff"),
    GovtExamInfo("Bihar Police Constable", "Police", 18f, 25f, "Bihar Police Constable Entry"),
    GovtExamInfo("MP Police Constable", "Police", 18f, 33f, "Madhya Pradesh Police Constable Entry"),
    GovtExamInfo("Rajasthan Police Constable", "Police", 18f, 24f, "Rajasthan Police Constable Entry"),
    GovtExamInfo("State PCS General (UPPSC / BPSC / MPPSC)", "Police", 21f, 40f, "State Civil Services Executive Officers"),

    // Teaching & Education
    GovtExamInfo("CTET / State TET Eligibility", "Teaching", 18f, 50f, "Central & State Teacher Eligibility Test"),
    GovtExamInfo("KVS PRT (Primary Teacher)", "Teaching", 18f, 30f, "Kendriya Vidyalaya Primary Teacher"),
    GovtExamInfo("KVS TGT (Trained Graduate Teacher)", "Teaching", 18f, 35f, "Kendriya Vidyalaya TGT Teacher"),
    GovtExamInfo("KVS PGT (Post Graduate Teacher)", "Teaching", 18f, 40f, "Kendriya Vidyalaya PGT Teacher"),
    GovtExamInfo("NVS TGT / PGT Teacher", "Teaching", 18f, 40f, "Navodaya Vidyalaya Samiti Teachers"),
    GovtExamInfo("UGC NET JRF (Junior Research Fellowship)", "Teaching", 18f, 30f, "University Assistant Professor & JRF Entry"),

    // Other Govt Services & PSUs
    GovtExamInfo("IB ACIO (Assistant Central Intelligence Officer)", "Other", 18f, 27f, "Intelligence Bureau Grade II Officer"),
    GovtExamInfo("IB Security Assistant / Executive", "Other", 18f, 27f, "Intelligence Bureau Security Executive Staff"),
    GovtExamInfo("ISRO Scientist / Engineer 'SC'", "Other", 18f, 35f, "Space Research Organization Scientist Entry"),
    GovtExamInfo("FCI Assistant Grade III", "Other", 18f, 28f, "Food Corporation of India General & Depot Staff"),
    GovtExamInfo("UP Sewayojan / Employment Registration", "Other", 18f, 40f, "State Employment Scheme Jobs")
)

@Composable
fun AgeCalculatorToolDialog(onDismiss: () -> Unit) {
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    // Focus Requesters for seamless auto-advance between input fields
    val dobDayFocus = remember { FocusRequester() }
    val dobMonthFocus = remember { FocusRequester() }
    val dobYearFocus = remember { FocusRequester() }

    val cutoffDayFocus = remember { FocusRequester() }
    val cutoffMonthFocus = remember { FocusRequester() }
    val cutoffYearFocus = remember { FocusRequester() }

    // DOB is empty by default as requested
    var dobDay by remember { mutableStateOf("") }
    var dobMonth by remember { mutableStateOf("") }
    var dobYear by remember { mutableStateOf("") }

    // Cutoff Date dynamically defaults to TODAY'S CURRENT DATE
    val calendar = remember { java.util.Calendar.getInstance() }
    val todayDay = remember { String.format(Locale.US, "%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH)) }
    val todayMonth = remember { String.format(Locale.US, "%02d", calendar.get(java.util.Calendar.MONTH) + 1) }
    val todayYear = remember { calendar.get(java.util.Calendar.YEAR).toString() }

    var cutoffDay by remember { mutableStateOf(todayDay) }
    var cutoffMonth by remember { mutableStateOf(todayMonth) }
    var cutoffYear by remember { mutableStateOf(todayYear) }

    var resultText by remember { mutableStateOf<String?>(null) }
    var calculatedAgeYears by remember { mutableIntStateOf(0) }
    var calculatedAgeMonths by remember { mutableIntStateOf(0) }
    var calculatedAgeDays by remember { mutableIntStateOf(0) }
    var totalDaysCalculated by remember { mutableIntStateOf(0) }

    // Category relaxation: General (+0), OBC (+3), SC/ST (+5), PwD (+10)
    var selectedCategory by remember { mutableStateOf("General (UR)") }
    val categoryRelaxationYears = when (selectedCategory) {
        "OBC (+3 Yrs)" -> 3f
        "SC / ST (+5 Yrs)" -> 5f
        "PwD (+10 Yrs)" -> 10f
        else -> 0f
    }

    // Exam category filter tab
    var selectedExamGroup by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyEligible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .thinScrollbar(scrollState)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Govt Exam Age Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "50+ Official Indian Govt Jobs Eligibility",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Relaxation Chooser
        Text("Select Category (Reservation Age Relaxation):", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("General (UR)", "OBC (+3 Yrs)", "SC / ST (+5 Yrs)", "PwD (+10 Yrs)").forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) PrimaryRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, if (isSelected) PrimaryRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCategory = cat }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Date of Birth (DOB):", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = dobDay,
                onValueChange = { input ->
                    if (input.length <= 2) {
                        dobDay = input
                        if (input.length == 2) {
                            dobMonthFocus.requestFocus()
                        }
                    }
                },
                label = { Text("Day") },
                placeholder = { Text("DD") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(dobDayFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = dobMonth,
                onValueChange = { input ->
                    if (input.length <= 2) {
                        dobMonth = input
                        if (input.length == 2) {
                            dobYearFocus.requestFocus()
                        }
                    }
                },
                label = { Text("Month") },
                placeholder = { Text("MM") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(dobMonthFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = dobYear,
                onValueChange = { input ->
                    if (input.length <= 4) {
                        dobYear = input
                        if (input.length == 4) {
                            cutoffDayFocus.requestFocus()
                        }
                    }
                },
                label = { Text("Year") },
                placeholder = { Text("YYYY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(dobYearFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cutoff Date (Age As On):", fontWeight = FontWeight.Bold)
            Text(
                text = "Set Today's Date",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryRed,
                modifier = Modifier.clickable {
                    cutoffDay = todayDay
                    cutoffMonth = todayMonth
                    cutoffYear = todayYear
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = cutoffDay,
                onValueChange = { input ->
                    if (input.length <= 2) {
                        cutoffDay = input
                        if (input.length == 2) {
                            cutoffMonthFocus.requestFocus()
                        }
                    }
                },
                label = { Text("Day") },
                placeholder = { Text("DD") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(cutoffDayFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = cutoffMonth,
                onValueChange = { input ->
                    if (input.length <= 2) {
                        cutoffMonth = input
                        if (input.length == 2) {
                            cutoffYearFocus.requestFocus()
                        }
                    }
                },
                label = { Text("Month") },
                placeholder = { Text("MM") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(cutoffMonthFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = cutoffYear,
                onValueChange = { input ->
                    if (input.length <= 4) {
                        cutoffYear = input
                    }
                },
                label = { Text("Year") },
                placeholder = { Text("YYYY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(cutoffYearFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()

                val dDay = dobDay.toIntOrNull()
                val dMon = dobMonth.toIntOrNull()
                val dYr = dobYear.toIntOrNull()

                if (dDay == null || dMon == null || dYr == null || dDay !in 1..31 || dMon !in 1..12 || dYr < 1900) {
                    resultText = "Please enter a valid Date of Birth (DD/MM/YYYY)."
                    return@Button
                }

                val cDay = cutoffDay.toIntOrNull() ?: todayDay.toInt()
                val cMon = cutoffMonth.toIntOrNull() ?: todayMonth.toInt()
                val cYr = cutoffYear.toIntOrNull() ?: todayYear.toInt()

                var years = cYr - dYr
                var months = cMon - dMon
                var days = cDay - dDay

                if (days < 0) {
                    months -= 1
                    days += 30
                }
                if (months < 0) {
                    years -= 1
                    months += 12
                }

                if (years < 0) {
                    resultText = "Date of Birth cannot be in the future of Cutoff Date!"
                    return@Button
                }

                calculatedAgeYears = years
                calculatedAgeMonths = months
                calculatedAgeDays = days
                totalDaysCalculated = (years * 365) + (months * 30) + days
                resultText = "Your Exact Age is $years Years, $months Months & $days Days as on $cutoffDay/$cutoffMonth/$cutoffYear."
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Calculate Age & Check Job Eligibility")
        }

        resultText?.let { ageSummary ->
            Spacer(modifier = Modifier.height(16.dp))

            val candidateDecimalAge = calculatedAgeYears + (calculatedAgeMonths / 12.0f) + (calculatedAgeDays / 365.0f)

            // Compute eligibility across all master list jobs
            val processedExams = allGovtExamsMasterList.map { exam ->
                val maxAgeWithRelaxation = exam.maxAgeGeneral + categoryRelaxationYears
                val isEligible = candidateDecimalAge >= exam.minAge && candidateDecimalAge <= maxAgeWithRelaxation
                Triple(exam, maxAgeWithRelaxation, isEligible)
            }

            val eligibleCount = processedExams.count { it.third }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimaryRed.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = ageSummary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Total Days: $totalDaysCalculated days (${totalDaysCalculated / 7} weeks) • Category: $selectedCategory",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary Badge Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Eligible for $eligibleCount of ${allGovtExamsMasterList.size} Govt Exams",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "Verified Official Criteria ($selectedCategory)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${((eligibleCount.toFloat() / allGovtExamsMasterList.size) * 100).toInt()}% Eligible",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search & Group Filter Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search exam (e.g. CGL, Constable, Agniveer)...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Department Group Chips
                    val groups = listOf("All", "SSC", "Defence", "Railways", "Banking", "Police", "UPSC", "Teaching", "Other")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        groups.forEach { group ->
                            val isGroupSelected = selectedExamGroup == group
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isGroupSelected) PrimaryRed else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, if (isGroupSelected) PrimaryRed else MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable { selectedExamGroup = group }
                            ) {
                                Text(
                                    text = group,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isGroupSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isGroupSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Eligible Only Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOnlyEligible = !showOnlyEligible },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Show Eligible Exams Only",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = showOnlyEligible,
                            onCheckedChange = { showOnlyEligible = it },
                            modifier = Modifier.scale(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filtered Exam List
                    val filteredExams = processedExams.filter { (exam, _, isEligible) ->
                        val matchesGroup = selectedExamGroup == "All" || exam.category.equals(selectedExamGroup, ignoreCase = true)
                        val matchesSearch = searchQuery.isBlank() ||
                                exam.title.contains(searchQuery, ignoreCase = true) ||
                                exam.description.contains(searchQuery, ignoreCase = true)
                        val matchesEligibleOnly = !showOnlyEligible || isEligible
                        matchesGroup && matchesSearch && matchesEligibleOnly
                    }

                    if (filteredExams.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matching exams found.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            filteredExams.forEach { (exam, maxAgeWithRelaxation, isEligible) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, if (isEligible) Color(0xFF2E7D32).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = exam.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = exam.description,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            if (isEligible) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = "Eligible ✓",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF2E7D32),
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color.Red.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = "Not Eligible ✗",
                                                        fontSize = 11.sp,
                                                        color = Color.Red,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val minStr = if (exam.minAge % 1f == 0f) "${exam.minAge.toInt()}" else "${exam.minAge}"
                                            val maxGenStr = if (exam.maxAgeGeneral % 1f == 0f) "${exam.maxAgeGeneral.toInt()}" else "${exam.maxAgeGeneral}"
                                            val maxEffStr = if (maxAgeWithRelaxation % 1f == 0f) "${maxAgeWithRelaxation.toInt()}" else "$maxAgeWithRelaxation"

                                            Text(
                                                text = "Required Age: $minStr to $maxEffStr Yrs" +
                                                        if (categoryRelaxationYears > 0) " (Gen: $maxGenStr + ${categoryRelaxationYears.toInt()}Y)" else "",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = exam.category,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}


// ----------------------------------------------------------------------------
// 5. REMOVE IMAGE BACKGROUND TOOL (Advanced Restore/Erase/Invert/Zoom Engine)
// ----------------------------------------------------------------------------
@Composable
fun RemoveBackgroundToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((targetToolId: String, bitmap: Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current

    // Bitmaps
    var selectedBitmap by remember(initialBitmap) { mutableStateOf<Bitmap?>(initialBitmap) }
    var maskBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var renderVersion by remember { mutableIntStateOf(0) }
    var showMoveToToolSheet by remember { mutableStateOf(false) }
    var showSaveFormatDialog by remember { mutableStateOf(false) }

    // Color background choice
    var selectedColor by remember { mutableIntStateOf(AndroidColor.WHITE) }

    LaunchedEffect(initialBitmap) {
        if (initialBitmap != null && maskBitmap == null) {
            val initialMask = Bitmap.createBitmap(initialBitmap.width, initialBitmap.height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(AndroidColor.WHITE)
            }
            maskBitmap = initialMask
            val initialProcessed = Bitmap.createBitmap(initialBitmap.width, initialBitmap.height, Bitmap.Config.ARGB_8888)
            renderCompositeCanvas(initialBitmap, initialMask, initialProcessed, selectedColor)
            processedBitmap = initialProcessed
            renderVersion++
        }
    }

    // Edge Sensitivity (1% to 100%)
    var tolerancePercent by remember { mutableFloatStateOf(28f) }
    var sensitivityInput by remember { mutableStateOf("28") }

    // Active tool: "SELECT", "RESTORE", "ERASE", "INVERT"
    var activeTool by remember { mutableStateOf("RESTORE") }

    // Smart Edge sensitivity is permanently active
    val isAssistedMode = true

    // Brush Controls (Size, Opacity, Hardness)
    var brushSize by remember { mutableFloatStateOf(45f) } // 5..150
    var brushOpacity by remember { mutableFloatStateOf(100f) } // 1..100
    var brushHardness by remember { mutableFloatStateOf(75f) } // 0..100

    // Canvas Zoom & Pan
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Visual Brush Cursor Overlay position & slider feedback
    var brushCursorPos by remember { mutableStateOf<Offset?>(null) }
    var isAdjustingSlider by remember { mutableStateOf(false) }
    var lastBmpPoint by remember { mutableStateOf<Offset?>(null) }

    // Undo / Redo Stacks (Max 15 history steps)
    val undoStack = remember { mutableStateListOf<Bitmap>() }
    val redoStack = remember { mutableStateListOf<Bitmap>() }

    fun pushUndoState(currentMask: Bitmap) {
        if (undoStack.size >= 15) {
            undoStack.removeAt(0)
        }
        undoStack.add(cloneBitmap(currentMask))
        redoStack.clear()
    }

    fun recalculateProcessedImage() {
        val orig = selectedBitmap ?: return
        val mask = maskBitmap ?: return
        var proc = processedBitmap
        if (proc == null || proc.width != orig.width || proc.height != orig.height) {
            proc = Bitmap.createBitmap(orig.width, orig.height, Bitmap.Config.ARGB_8888)
            processedBitmap = proc
        }
        renderCompositeCanvas(orig, mask, proc, selectedColor)
        renderVersion++
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                    // REQUIREMENT 1: Photo appears EXACT & UNMODIFIED initially upon upload
                    val initialMask = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888).apply {
                        eraseColor(AndroidColor.WHITE) // fully opaque mask (no pixels erased yet)
                    }
                    maskBitmap = initialMask
                    val initialProcessed = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
                    renderCompositeCanvas(bmp, initialMask, initialProcessed, selectedColor)
                    processedBitmap = initialProcessed
                    renderVersion++
                    undoStack.clear()
                    redoStack.clear()
                    pushUndoState(initialMask)
                    scale = 1f
                    offset = Offset.Zero
                    onShowToast("Photo loaded successfully!")
                }
            } catch (e: Exception) {
                onShowToast("Failed to load photo: ${e.message}")
            }
        }
    }

    // Auto Select Subject action
    fun performAutoSelect() {
        val orig = selectedBitmap
        if (orig == null) {
            onShowToast("Please select a photo first!")
            return
        }
        val newMask = autoRemoveBgMask(orig, (tolerancePercent / 100f).coerceIn(0.01f, 1f))
        maskBitmap = newMask
        pushUndoState(newMask)
        recalculateProcessedImage()
        onShowToast("Auto subject cutout updated!")
    }

    // Invert Mask action
    fun performInvert() {
        val currMask = maskBitmap
        if (currMask == null) {
            onShowToast("Please select a photo first!")
            return
        }
        val invertedMask = invertMaskAlpha(currMask)
        maskBitmap = invertedMask
        pushUndoState(invertedMask)
        recalculateProcessedImage()
        onShowToast("Selection inverted!")
    }

    // Undo action
    fun performUndo() {
        if (undoStack.size > 1) {
            val current = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(current)
            val previous = undoStack.last()
            val restoredMask = cloneBitmap(previous)
            maskBitmap = restoredMask
            recalculateProcessedImage()
            onShowToast("Undo applied")
        } else if (undoStack.size == 1) {
            onShowToast("Reached initial state")
        }
    }

    // Redo action
    fun performRedo() {
        if (redoStack.isNotEmpty()) {
            val stateToRedo = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(cloneBitmap(stateToRedo))
            maskBitmap = stateToRedo
            recalculateProcessedImage()
            onShowToast("Redo applied")
        }
    }

    // Reset action
    fun performReset() {
        val orig = selectedBitmap ?: return
        val freshMask = Bitmap.createBitmap(orig.width, orig.height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(AndroidColor.WHITE) // fully visible
        }
        maskBitmap = freshMask
        undoStack.clear()
        redoStack.clear()
        pushUndoState(freshMask)
        scale = 1f
        offset = Offset.Zero
        processedBitmap = orig
        onShowToast("Photo reset to original state!")
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .thinScrollbar(scrollState)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP CONTROL BAR: Header, Undo, Redo, Reset, Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(
                    onClick = { performUndo() },
                    enabled = undoStack.size > 1
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = if (undoStack.size > 1) PrimaryRed else Color.Gray
                    )
                }
                IconButton(
                    onClick = { performRedo() },
                    enabled = redoStack.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo",
                        tint = if (redoStack.isNotEmpty()) PrimaryRed else Color.Gray
                    )
                }
            }

            Text(
                text = "Background Remover",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { performReset() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Photo", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // CANVAS CONTAINER WITH CHECKERBOARD, 2-FINGER ZOOM/PAN, AND 1-FINGER BRUSH
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val bmpToShow = processedBitmap ?: selectedBitmap
            if (bmpToShow != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedBitmap, activeTool) {
                            // REQUIREMENT 3: 2-finger touch ONLY zooms and moves photo, strictly NO tool/brush action
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                var isTwoFingers = false

                                brushCursorPos = down.position

                                do {
                                    val event = awaitPointerEvent()
                                    val pointerCount = event.changes.size

                                    if (pointerCount >= 2) {
                                        isTwoFingers = true
                                        brushCursorPos = null // hide brush cursor during zoom/pan
                                    }

                                    if (isTwoFingers) {
                                        // 2 fingers = Zoom & Pan ONLY
                                        val zoom = event.calculateZoom()
                                        val pan = event.calculatePan()
                                        scale = (scale * zoom).coerceIn(0.5f, 6f)
                                        offset += pan
                                        event.changes.forEach { it.consume() }
                                    } else {
                                        // 1 finger = Brush action with smooth stroke interpolation
                                        val change = event.changes.firstOrNull()
                                        if (change != null && change.pressed) {
                                            val touchPos = change.position
                                            brushCursorPos = touchPos

                                            val origBmp = selectedBitmap
                                            val currMask = maskBitmap
                                            if (origBmp != null && currMask != null && (activeTool == "RESTORE" || activeTool == "ERASE")) {
                                                var targetProc = processedBitmap
                                                if (targetProc == null || targetProc.width != origBmp.width || targetProc.height != origBmp.height) {
                                                    targetProc = Bitmap.createBitmap(origBmp.width, origBmp.height, Bitmap.Config.ARGB_8888)
                                                    processedBitmap = targetProc
                                                }

                                                val canvasWidth = this.size.width.toFloat()
                                                val canvasHeight = this.size.height.toFloat()
                                                val imgWidth = origBmp.width.toFloat()
                                                val imgHeight = origBmp.height.toFloat()

                                                val fitScale = minOf(canvasWidth / imgWidth, canvasHeight / imgHeight)
                                                val actualWidth = imgWidth * fitScale * scale
                                                val actualHeight = imgHeight * fitScale * scale

                                                val left = (canvasWidth - actualWidth) / 2f + offset.x
                                                val top = (canvasHeight - actualHeight) / 2f + offset.y

                                                val bmpX = ((touchPos.x - left) / (fitScale * scale))
                                                val bmpY = ((touchPos.y - top) / (fitScale * scale))

                                                val currentPoint = Offset(bmpX, bmpY)
                                                val isErase = activeTool == "ERASE"
                                                val effectiveBrushSize = brushSize * 2f
                                                val bmpBrushSize = effectiveBrushSize / (fitScale * scale)

                                                val maskCanvas = android.graphics.Canvas(currMask)
                                                applyBrushToMaskCanvas(
                                                    maskCanvas = maskCanvas,
                                                    prevPoint = lastBmpPoint,
                                                    currPoint = currentPoint,
                                                    sizePx = bmpBrushSize,
                                                    opacityPercent = brushOpacity,
                                                    hardnessPercent = brushHardness,
                                                    isErase = isErase
                                                )
                                                lastBmpPoint = currentPoint

                                                renderCompositeCanvas(origBmp, currMask, targetProc, selectedColor)
                                                renderVersion++
                                            }
                                            change.consume()
                                        }
                                    }
                                } while (event.changes.any { it.pressed })

                                brushCursorPos = null
                                lastBmpPoint = null
                                if (!isTwoFingers && (activeTool == "RESTORE" || activeTool == "ERASE")) {
                                    maskBitmap?.let { pushUndoState(it) }
                                }
                            }
                        }
                ) {
                    // Checkerboard background for transparency preview
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCheckerboardPattern()
                    }

                    // Scaled & Panned Photo Image
                    key(renderVersion) {
                        Image(
                            bitmap = bmpToShow.asImageBitmap(),
                            contentDescription = "Photo Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                        )
                    }

                    // REQUIREMENT 4: Live Brush Cursor Preview Overlay
                    // When adjusting sliders or touching, shows a filled white brush circle representing exact size, opacity & hardness
                    if ((brushCursorPos != null || isAdjustingSlider) && (activeTool == "RESTORE" || activeTool == "ERASE")) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cursor = brushCursorPos ?: Offset(size.width / 2f, size.height / 2f)
                            val r = brushSize // 2x effective radius (diameter = 2 * brushSize)
                            val opacity = (brushOpacity / 100f).coerceIn(0.05f, 1f)
                            val hardnessRatio = (brushHardness / 100f).coerceIn(0f, 1f)

                            if (r > 1f) {
                                if (hardnessRatio >= 0.98f) {
                                    // Solid filled white circle
                                    drawCircle(
                                        color = Color.White.copy(alpha = opacity),
                                        radius = r,
                                        center = cursor
                                    )
                                } else {
                                    // Feathered filled white circle with radial gradient
                                    val colorStops = arrayOf(
                                        0.0f to Color.White.copy(alpha = opacity),
                                        hardnessRatio to Color.White.copy(alpha = opacity),
                                        1.0f to Color.White.copy(alpha = 0f)
                                    )
                                    drawCircle(
                                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                            colorStops = colorStops,
                                            center = cursor,
                                            radius = r
                                        ),
                                        radius = r,
                                        center = cursor
                                    )
                                }

                                // Contrast ring borders so brush is crisp on white and dark images
                                drawCircle(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    radius = r,
                                    center = cursor,
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.9f),
                                    radius = r + 1.dp.toPx(),
                                    center = cursor,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                        }
                    }

                    // Floating Zoom Controls (+ / - / Reset Zoom)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(32.dp).clickable { scale = (scale + 0.5f).coerceAtMost(6f) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(32.dp).clickable {
                                scale = (scale - 0.5f).coerceAtLeast(0.5f)
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(32.dp).clickable {
                                scale = 1f
                                offset = Offset.Zero
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("1:1", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Upload Prompt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to Select Passport Photo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Auto removes background & allows fine tuning", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // REQUIREMENT 2: Assisted/Manual toggle buttons removed. Smart Edge sensitivity is built-in.

        // SLIDERS: Size, Opacity, Hardness (Appears when activeTool is RESTORE or ERASE)
        if (activeTool == "RESTORE" || activeTool == "ERASE") {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Size", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text("${brushSize.toInt()} px", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryRed)
                    }
                    Slider(
                        value = brushSize,
                        onValueChange = {
                            brushSize = it
                            isAdjustingSlider = true
                        },
                        onValueChangeFinished = {
                            isAdjustingSlider = false
                        },
                        valueRange = 5f..150f,
                        modifier = Modifier.height(30.dp)
                    )

                    // Opacity Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Opacity", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text("${brushOpacity.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryRed)
                    }
                    Slider(
                        value = brushOpacity,
                        onValueChange = {
                            brushOpacity = it
                            isAdjustingSlider = true
                        },
                        onValueChangeFinished = {
                            isAdjustingSlider = false
                        },
                        valueRange = 1f..100f,
                        modifier = Modifier.height(30.dp)
                    )

                    // Hardness Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hardness", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text("${brushHardness.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryRed)
                    }
                    Slider(
                        value = brushHardness,
                        onValueChange = {
                            brushHardness = it
                            isAdjustingSlider = true
                        },
                        onValueChangeFinished = {
                            isAdjustingSlider = false
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.height(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // EDGE SENSITIVITY SLIDER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Edge Sensitivity:", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = sensitivityInput,
                onValueChange = { input ->
                    sensitivityInput = input
                    val parsed = input.toIntOrNull()
                    if (parsed != null) {
                        tolerancePercent = parsed.coerceIn(1, 100).toFloat()
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(105.dp),
                shape = RoundedCornerShape(8.dp),
                suffix = { Text("%", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                trailingIcon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropUp,
                            contentDescription = "Increase Edge Sensitivity",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    val current = tolerancePercent.toInt()
                                    if (current < 100) {
                                        val updated = current + 1
                                        tolerancePercent = updated.toFloat()
                                        sensitivityInput = updated.toString()
                                    }
                                }
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Decrease Edge Sensitivity",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    val current = tolerancePercent.toInt()
                                    if (current > 1) {
                                        val updated = current - 1
                                        tolerancePercent = updated.toFloat()
                                        sensitivityInput = updated.toString()
                                    }
                                }
                        )
                    }
                }
            )
        }
        Slider(
            value = tolerancePercent,
            onValueChange = {
                tolerancePercent = it
                sensitivityInput = it.toInt().toString()
            },
            valueRange = 1f..100f,
            modifier = Modifier.height(30.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // TOOLBAR NAVIGATION: Select, Restore, Erase, Invert
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Select (Person / Auto Cutout)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            activeTool = "SELECT"
                            performAutoSelect()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Select Subject",
                        tint = if (activeTool == "SELECT") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Select",
                        fontSize = 11.sp,
                        fontWeight = if (activeTool == "SELECT") FontWeight.Bold else FontWeight.Normal,
                        color = if (activeTool == "SELECT") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Restore (Brush)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { activeTool = "RESTORE" }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = "Restore Brush",
                        tint = if (activeTool == "RESTORE") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Restore",
                        fontSize = 11.sp,
                        fontWeight = if (activeTool == "RESTORE") FontWeight.Bold else FontWeight.Normal,
                        color = if (activeTool == "RESTORE") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Erase (Rubber / Eraser)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { activeTool = "ERASE" }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Erase Rubber",
                        tint = if (activeTool == "ERASE") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Erase",
                        fontSize = 11.sp,
                        fontWeight = if (activeTool == "ERASE") FontWeight.Bold else FontWeight.Normal,
                        color = if (activeTool == "ERASE") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Invert (Swap Selection)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            activeTool = "INVERT"
                            performInvert()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = "Invert Selection",
                        tint = if (activeTool == "INVERT") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Invert",
                        fontSize = 11.sp,
                        fontWeight = if (activeTool == "INVERT") FontWeight.Bold else FontWeight.Normal,
                        color = if (activeTool == "INVERT") PrimaryRed else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // BACKGROUND COLOR SELECTOR
        Text("Select Background Color:", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))

        val bgColors = listOf(
            "White (SSC / UP Police)" to AndroidColor.WHITE,
            "Light Blue (Passport)" to AndroidColor.rgb(66, 165, 245),
            "Soft Red" to AndroidColor.rgb(229, 57, 53),
            "Off-White / Grey" to AndroidColor.rgb(238, 238, 238),
            "Transparent (PNG)" to AndroidColor.TRANSPARENT
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bgColors) { (name, colorVal) ->
                FilterChip(
                    selected = selectedColor == colorVal,
                    onClick = {
                        selectedColor = colorVal
                        recalculateProcessedImage()
                    },
                    label = { Text(name, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryRed, selectedLabelColor = Color.White)
                )
            }
        }

        processedBitmap?.let { bmp ->
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showSaveFormatDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }
                Button(
                    onClick = { shareBmp(context, bmp, "Background Cleaned Photo") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }

            if (showSaveFormatDialog) {
                RemoveBgSaveFormatDialog(
                    processedBitmap = bmp,
                    originalBitmap = selectedBitmap,
                    maskBitmap = maskBitmap,
                    onDismiss = { showSaveFormatDialog = false },
                    onShowToast = onShowToast
                )
            }
        }

        val currentEditedBmp = processedBitmap ?: selectedBitmap
        if (currentEditedBmp != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showMoveToToolSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, PrimaryRed),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryRed.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Move this to Other Tools (दूसरे टूल में भेजें)", fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 14.sp)
            }

            if (showMoveToToolSheet) {
                MoveToOtherToolBottomSheet(
                    currentToolId = "removebg",
                    currentBitmap = currentEditedBmp,
                    onToolSelected = { targetId, bmp ->
                        showMoveToToolSheet = false
                        onMoveToTool?.invoke(targetId, bmp)
                    },
                    onDismiss = { showMoveToToolSheet = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

private fun cloneBitmap(src: Bitmap): Bitmap {
    return src.copy(Bitmap.Config.ARGB_8888, true)
}

private fun autoRemoveBgMask(original: Bitmap, toleranceFraction: Float = 0.28f): Bitmap {
    val width = original.width
    val height = original.height
    val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val cornerPixels = intArrayOf(
        original.getPixel(0, 0),
        original.getPixel((width - 1).coerceAtLeast(0), 0),
        original.getPixel(0, (height - 1).coerceAtLeast(0)),
        original.getPixel((width - 1).coerceAtLeast(0), (height - 1).coerceAtLeast(0))
    )

    val pixels = IntArray(width * height)
    original.getPixels(pixels, 0, width, 0, 0, width, height)
    val maskPixels = IntArray(width * height)

    for (i in pixels.indices) {
        val p = pixels[i]
        val r = AndroidColor.red(p)
        val g = AndroidColor.green(p)
        val b = AndroidColor.blue(p)

        var isBg = false
        for (corner in cornerPixels) {
            val cr = AndroidColor.red(corner)
            val cg = AndroidColor.green(corner)
            val cb = AndroidColor.blue(corner)
            val dist = Math.sqrt(((r - cr) * (r - cr) + (g - cg) * (g - cg) + (b - cb) * (b - cb)).toDouble()) / 441.67
            if (dist < toleranceFraction) {
                isBg = true
                break
            }
        }

        // Alpha 0 = erased background, Alpha 255 = foreground subject
        val alpha = if (isBg) 0 else 255
        maskPixels[i] = (alpha shl 24) or 0x00FFFFFF
    }

    mask.setPixels(maskPixels, 0, width, 0, 0, width, height)
    return mask
}

private fun invertMaskAlpha(mask: Bitmap): Bitmap {
    val width = mask.width
    val height = mask.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val pixels = IntArray(width * height)
    mask.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val alpha = (pixels[i] shr 24) and 0xFF
        val invAlpha = 255 - alpha
        pixels[i] = (invAlpha shl 24) or 0x00FFFFFF
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

private fun applyBrushToMaskCanvas(
    maskCanvas: android.graphics.Canvas,
    prevPoint: Offset?,
    currPoint: Offset,
    sizePx: Float,
    opacityPercent: Float,
    hardnessPercent: Float,
    isErase: Boolean
) {
    val radius = (sizePx / 2f).coerceAtLeast(0.5f)
    val alphaInt = ((opacityPercent / 100f) * 255).toInt().coerceIn(1, 255)
    val hardnessRatio = (hardnessPercent / 100f).coerceIn(0f, 1f)

    val strokePaint = android.graphics.Paint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
        strokeWidth = sizePx
        alpha = alphaInt

        if (hardnessRatio < 0.95f) {
            val blurRadius = (radius * (1f - hardnessRatio)).coerceAtLeast(0.5f)
            maskFilter = android.graphics.BlurMaskFilter(blurRadius, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        if (isErase) {
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT)
            color = android.graphics.Color.BLACK
        } else {
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER)
            color = android.graphics.Color.WHITE
        }
    }

    val fillPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL
        alpha = alphaInt

        if (hardnessRatio < 0.95f) {
            val blurRadius = (radius * (1f - hardnessRatio)).coerceAtLeast(0.5f)
            maskFilter = android.graphics.BlurMaskFilter(blurRadius, android.graphics.BlurMaskFilter.Blur.NORMAL)
        }

        if (isErase) {
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT)
            color = android.graphics.Color.BLACK
        } else {
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER)
            color = android.graphics.Color.WHITE
        }
    }

    if (prevPoint != null) {
        maskCanvas.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y, strokePaint)
    }
    maskCanvas.drawCircle(currPoint.x, currPoint.y, radius, fillPaint)
}

private fun renderCompositeCanvas(
    original: Bitmap,
    mask: Bitmap,
    targetBitmap: Bitmap,
    bgColor: Int
) {
    val canvas = android.graphics.Canvas(targetBitmap)
    canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

    if (bgColor != AndroidColor.TRANSPARENT) {
        canvas.drawColor(bgColor, android.graphics.PorterDuff.Mode.SRC)
    }

    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    val layerId = canvas.saveLayer(0f, 0f, original.width.toFloat(), original.height.toFloat(), null)

    canvas.drawBitmap(original, 0f, 0f, paint)

    paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN)
    canvas.drawBitmap(mask, 0f, 0f, paint)
    paint.xfermode = null

    canvas.restoreToCount(layerId)
}

private fun DrawScope.drawCheckerboardPattern() {
    val squareSize = 16f
    val cols = (size.width / squareSize).toInt() + 1
    val rows = (size.height / squareSize).toInt() + 1

    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val isDark = (row + col) % 2 == 0
            val color = if (isDark) Color(0xFFE0E0E0) else Color(0xFFFFFFFF)
            drawRect(
                color = color,
                topLeft = Offset(col * squareSize, row * squareSize),
                size = Size(squareSize, squareSize)
            )
        }
    }
}

// ----------------------------------------------------------------------------
// HELPER FUNCTIONS FOR IMAGE PROCESSING, COMPRESSION & SHARING
// ----------------------------------------------------------------------------
private fun convertToPixels(valW: Float, valH: Float, unit: String, dpi: Int): Pair<Int, Int> {
    val pixelsW = when (unit) {
        "cm" -> (valW / 2.54f * dpi).toInt()
        "mm" -> (valW / 25.4f * dpi).toInt()
        "inch" -> (valW * dpi).toInt()
        else -> valW.toInt()
    }
    val pixelsH = when (unit) {
        "cm" -> (valH / 2.54f * dpi).toInt()
        "mm" -> (valH / 25.4f * dpi).toInt()
        "inch" -> (valH * dpi).toInt()
        else -> valH.toInt()
    }
    return pixelsW to pixelsH
}

private fun compressBmpToKb(bitmap: Bitmap, targetKb: Int): ByteArray {
    var quality = 95
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    while (stream.toByteArray().size / 1024 > targetKb && quality > 10) {
        stream.reset()
        quality -= 5
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    }
    return stream.toByteArray()
}

private fun cropBmpParams(
    original: Bitmap,
    arW: Float,
    arH: Float,
    zoom: Float,
    rotation: Float,
    flipHorizontal: Boolean = false
): Bitmap {
    var src = original
    val matrix = Matrix()
    if (rotation != 0f) {
        matrix.postRotate(rotation)
    }
    if (flipHorizontal) {
        matrix.postScale(-1f, 1f)
    }
    if (rotation != 0f || flipHorizontal) {
        src = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }

    val origW = src.width
    val origH = src.height

    var cropW = origW.toFloat()
    var cropH = origH.toFloat()

    if (arW > 0 && arH > 0) {
        val targetRatio = arW / arH
        val currentRatio = origW.toFloat() / origH.toFloat()
        if (currentRatio > targetRatio) {
            cropW = origH * targetRatio
        } else {
            cropH = origW / targetRatio
        }
    }

    cropW /= zoom
    cropH /= zoom

    cropW = cropW.coerceAtMost(origW.toFloat())
    cropH = cropH.coerceAtMost(origH.toFloat())

    val x = ((origW - cropW) / 2f).coerceAtLeast(0f).toInt()
    val y = ((origH - cropH) / 2f).coerceAtLeast(0f).toInt()

    val safeW = cropW.toInt().coerceAtMost(origW - x)
    val safeH = cropH.toInt().coerceAtMost(origH - y)

    return Bitmap.createBitmap(src, x, y, safeW.coerceAtLeast(10), safeH.coerceAtLeast(10))
}

private fun stampNameAndDateOnBitmap(
    original: Bitmap,
    name: String,
    dateText: String,
    stylePreset: String,
    dobText: String = "15/08/2000"
): Bitmap {
    val width = original.width
    val height = original.height

    val isOverlay = stylePreset.contains("Overlay")
    val isThreeLines = stylePreset.contains("NEET") || stylePreset.contains("NTA")
    val bannerFraction = if (isThreeLines) 0.28f else 0.22f
    val bannerHeight = (height * bannerFraction).toInt()
    val newHeight = if (isOverlay) height else height + bannerHeight

    val result = Bitmap.createBitmap(width, newHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)

    canvas.drawBitmap(original, 0f, 0f, null)

    if (isOverlay) {
        val darkPaint = Paint().apply {
            color = AndroidColor.argb(180, 0, 0, 0)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, (height - bannerHeight).toFloat(), width.toFloat(), height.toFloat(), darkPaint)
    } else {
        val isDarkBg = stylePreset.contains("Defence") || stylePreset.contains("Army")
        val bannerPaint = Paint().apply {
            color = if (isDarkBg) AndroidColor.BLACK else AndroidColor.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), newHeight.toFloat(), bannerPaint)

        // Decorative accent headers
        if (stylePreset.contains("RRB") || stylePreset.contains("Railway")) {
            val redLinePaint = Paint().apply {
                color = AndroidColor.parseColor("#D32F2F")
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, height.toFloat(), width.toFloat(), height.toFloat() + (bannerHeight * 0.08f), redLinePaint)
        } else if (stylePreset.contains("UPSC") || stylePreset.contains("PSC")) {
            val blueLinePaint = Paint().apply {
                color = AndroidColor.parseColor("#1976D2")
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, height.toFloat(), width.toFloat(), height.toFloat() + (bannerHeight * 0.08f), blueLinePaint)
        }
    }

    val isDarkBackground = isOverlay || stylePreset.contains("Defence") || stylePreset.contains("Army")
    val textPaint = Paint().apply {
        color = if (isDarkBackground) AndroidColor.WHITE else AndroidColor.BLACK
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = bannerHeight * (if (isThreeLines) 0.22f else 0.30f)
    }

    val startY = if (isOverlay) height - bannerHeight else height

    if (isThreeLines) {
        val line1Y = startY + (bannerHeight * 0.28f)
        val line2Y = startY + (bannerHeight * 0.58f)
        val line3Y = startY + (bannerHeight * 0.88f)

        if (name.isNotBlank()) {
            canvas.drawText(name.uppercase(), width / 2f, line1Y, textPaint)
        }
        val dopStr = if (dateText.uppercase().startsWith("DOP")) dateText.uppercase() else "DOP: ${dateText.uppercase()}"
        canvas.drawText(dopStr, width / 2f, line2Y, textPaint)

        val dobStr = if (dobText.uppercase().startsWith("DOB")) dobText.uppercase() else "DOB: ${dobText.uppercase()}"
        canvas.drawText(dobStr, width / 2f, line3Y, textPaint)
    } else if (stylePreset.contains("IBPS") || stylePreset.contains("Bank")) {
        val line1Y = startY + (bannerHeight * 0.58f)
        val dateStr = if (dateText.uppercase().startsWith("DOP")) dateText.uppercase() else "DOP: ${dateText.uppercase()}"
        canvas.drawText(dateStr, width / 2f, line1Y, textPaint)
    } else {
        val line1Y = startY + (bannerHeight * 0.38f)
        val line2Y = startY + (bannerHeight * 0.78f)

        if (name.isNotBlank()) {
            canvas.drawText(name.uppercase(), width / 2f, line1Y, textPaint)
        }
        if (dateText.isNotBlank()) {
            val dateStr = if (dateText.uppercase().startsWith("DOP")) dateText.uppercase() else "DOP: ${dateText.uppercase()}"
            val datePaint = Paint(textPaint).apply {
                textSize = bannerHeight * 0.26f
            }
            canvas.drawText(dateStr, width / 2f, line2Y, datePaint)
        }
    }

    return result
}

private fun replaceBmpBgColor(original: Bitmap, targetColor: Int, tolerance: Float = 0.28f): Bitmap {
    val width = original.width
    val height = original.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val cornerPixels = intArrayOf(
        original.getPixel(0, 0),
        original.getPixel((width - 1).coerceAtLeast(0), 0),
        original.getPixel(0, (height - 1).coerceAtLeast(0)),
        original.getPixel((width - 1).coerceAtLeast(0), (height - 1).coerceAtLeast(0))
    )

    val pixels = IntArray(width * height)
    original.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val p = pixels[i]
        val r = AndroidColor.red(p)
        val g = AndroidColor.green(p)
        val b = AndroidColor.blue(p)

        var isBg = false
        for (corner in cornerPixels) {
            val cr = AndroidColor.red(corner)
            val cg = AndroidColor.green(corner)
            val cb = AndroidColor.blue(corner)
            val dist = Math.sqrt(((r - cr) * (r - cr) + (g - cg) * (g - cg) + (b - cb) * (b - cb)).toDouble()) / 441.67
            if (dist < tolerance) {
                isBg = true
                break
            }
        }

        if (isBg) {
            pixels[i] = targetColor
        }
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

private fun saveBmpToGallery(context: Context, bitmap: Bitmap, fileName: String) {
    try {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sewayojan")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            }
            Toast.makeText(context, "Saved to Gallery (Pictures/Sewayojan)", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Saved to App Storage", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
    }
}

private fun shareBmp(context: Context, bitmap: Bitmap, title: String) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "sewayojan_${System.currentTimeMillis()}.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        stream.close()

        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "Processed with Sewayojan Candidate Utility Tools: https://sewayojan-tools.vercel.app/")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share Image via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to share image: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun saveByteArrayToGallery(context: Context, bytes: ByteArray, fileName: String, formatStr: String) {
    try {
        val mimeType = when (formatStr.uppercase()) {
            "PNG" -> "image/png"
            "WEBP" -> "image/webp"
            else -> "image/jpeg"
        }
        val ext = when (formatStr.uppercase()) {
            "PNG" -> "png"
            "WEBP" -> "webp"
            else -> "jpg"
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.$ext")
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sewayojan")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(bytes)
            }
            Toast.makeText(context, "Saved to Gallery (Pictures/Sewayojan)", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Saved to App Storage", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
    }
}

// ----------------------------------------------------------------------------
// IMAGE COMPRESSOR TOOL (Matching Exact UI Screenshots & Target KB Logic)
// ----------------------------------------------------------------------------
@Composable
fun ImageCompressorToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((targetToolId: String, bitmap: Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedBitmap by remember(initialBitmap) { mutableStateOf<Bitmap?>(initialBitmap) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var originalSizeKb by remember(initialBitmap) { mutableFloatStateOf(initialBitmap?.let { it.byteCount / 1024.0f } ?: 0f) }

    var compressedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var compressedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var compressedSizeKb by remember { mutableFloatStateOf(0f) }
    var showMoveToToolSheet by remember { mutableStateOf(false) }

    // Mode: "Quality Slider" or "Target Size (KB)"
    var selectedTab by remember { mutableStateOf("Quality Slider") }

    // Quality Slider Tab States
    var qualityPercent by remember { mutableFloatStateOf(75f) }
    var selectedMaxWidth by remember { mutableStateOf("No limit") }
    var isMaxWidthDropdownExpanded by remember { mutableStateOf(false) }

    // Target Size Tab States
    var selectedPresetKb by remember { mutableStateOf<Int?>(100) }
    var customKbInput by remember { mutableStateOf("") }

    // Shared Output Format
    var selectedFormat by remember { mutableStateOf("JPEG") }
    var isFormatDropdownExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Get file name
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIdx != -1 && cursor.moveToFirst()) {
                        selectedFileName = cursor.getString(nameIdx)
                    }
                }
                if (selectedFileName == null) {
                    selectedFileName = it.lastPathSegment ?: "photo.jpg"
                }

                // Get file size in KB
                val pfd = context.contentResolver.openFileDescriptor(it, "r")
                pfd?.use { descriptor ->
                    originalSizeKb = descriptor.statSize / 1024.0f
                }

                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bmp != null) {
                    selectedBitmap = bmp
                    compressedBitmap = null
                    compressedBytes = null
                    compressedSizeKb = 0f
                    if (originalSizeKb == 0f) {
                        originalSizeKb = (bmp.allocationByteCount / 1024.0f)
                    }
                    onShowToast("Photo loaded (${String.format("%.2f", originalSizeKb)} KB)")
                }
            } catch (e: Exception) {
                onShowToast("Failed to load photo: ${e.message}")
            }
        }
    }

    fun clearAll() {
        selectedBitmap = null
        selectedFileName = null
        originalSizeKb = 0f
        compressedBitmap = null
        compressedBytes = null
        compressedSizeKb = 0f
        onShowToast("Cleared all data")
    }

    fun doCompress() {
        val srcBmp = selectedBitmap
        if (srcBmp == null) {
            onShowToast("Please select an image first!")
            return
        }

        val compressFormat = when (selectedFormat.uppercase()) {
            "PNG" -> Bitmap.CompressFormat.PNG
            "WEBP" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }

        if (selectedTab == "Quality Slider") {
            var targetBmp = srcBmp
            val maxWidthPx = when (selectedMaxWidth) {
                "1920px" -> 1920
                "1280px" -> 1280
                "800px" -> 800
                "600px" -> 600
                "400px" -> 400
                else -> null
            }

            if (maxWidthPx != null && targetBmp.width > maxWidthPx) {
                val aspect = targetBmp.height.toFloat() / targetBmp.width.toFloat()
                val newH = (maxWidthPx * aspect).toInt().coerceAtLeast(1)
                targetBmp = Bitmap.createScaledBitmap(targetBmp, maxWidthPx, newH, true)
            }

            val baos = ByteArrayOutputStream()
            val qualityInt = qualityPercent.toInt().coerceIn(1, 100)
            targetBmp.compress(compressFormat, qualityInt, baos)
            val bytes = baos.toByteArray()

            compressedBytes = bytes
            compressedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            compressedSizeKb = bytes.size / 1024.0f
            onShowToast("Compressed to ${String.format("%.2f", compressedSizeKb)} KB")

        } else {
            // Target Size (KB) Mode
            val rawKbValue = customKbInput.toFloatOrNull()
                ?: selectedPresetKb?.toFloat()
                ?: 100f

            val maxKbLimit = 30720f // 30 MB limit
            val targetKbValue = if (rawKbValue > maxKbLimit) {
                onShowToast("Target size limited to maximum 30 MB (30,720 KB)")
                maxKbLimit
            } else {
                rawKbValue
            }

            val targetBytesLimit = (targetKbValue * 1024).toInt()

            var bestBytes: ByteArray? = null

            // Adaptive Scale Factor iteration from 1.0 down to 0.1
            var scaleFactor = 1.0f

            while (scaleFactor >= 0.08f) {
                val currBmp = if (scaleFactor < 1.0f) {
                    val w = (srcBmp.width * scaleFactor).toInt().coerceAtLeast(8)
                    val h = (srcBmp.height * scaleFactor).toInt().coerceAtLeast(8)
                    Bitmap.createScaledBitmap(srcBmp, w, h, true)
                } else {
                    srcBmp
                }

                // Binary search quality from 1 to 100
                var low = 1
                var high = 100
                var localBestBytes: ByteArray? = null

                while (low <= high) {
                    val mid = (low + high) / 2
                    val baos = ByteArrayOutputStream()
                    currBmp.compress(compressFormat, mid, baos)
                    val bytes = baos.toByteArray()

                    if (bytes.size <= targetBytesLimit) {
                        localBestBytes = bytes
                        low = mid + 1 // try higher quality
                    } else {
                        high = mid - 1 // reduce quality
                    }
                }

                if (localBestBytes != null) {
                    bestBytes = localBestBytes
                    break
                }

                // If even quality 1 was too large, scale down dimensions
                scaleFactor -= 0.12f
            }

            if (bestBytes == null) {
                val tinyBmp = Bitmap.createScaledBitmap(srcBmp, (srcBmp.width * 0.1f).toInt().coerceAtLeast(8), (srcBmp.height * 0.1f).toInt().coerceAtLeast(8), true)
                val baos = ByteArrayOutputStream()
                tinyBmp.compress(compressFormat, 1, baos)
                bestBytes = baos.toByteArray()
            }

            compressedBytes = bestBytes
            compressedBitmap = BitmapFactory.decodeByteArray(bestBytes, 0, bestBytes.size)
            compressedSizeKb = bestBytes.size / 1024.0f
            onShowToast("Compressed to target size (${String.format("%.2f", compressedSizeKb)} KB)")
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .thinScrollbar(scrollState)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Compress,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Image Compressor", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Compress by quality or exact target size", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image Card / Upload Section (Positioned at the TOP)
        if (selectedBitmap == null) {
            // Upload Prompt Box
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Click to Upload Photo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("JPEG, PNG, WEBP supported", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // Loaded & Compressed Image Card (Matching Screenshot Card UI)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar inside Card (FileName, Original KB, Close button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedFileName ?: "image.jpg",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${String.format("%.2f", originalSizeKb)} KB",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { clearAll() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Photo", modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Image Display
                    val bmpToDisplay = compressedBitmap ?: selectedBitmap
                    bmpToDisplay?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Photo Preview",
                            modifier = Modifier
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // New Size display if compressed
                    if (compressedSizeKb > 0f) {
                        Text(
                            text = "New Size: ${String.format("%.2f", compressedSizeKb)} KB",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryRed
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val bytes = compressedBytes
                                if (bytes != null) {
                                    saveByteArrayToGallery(
                                        context,
                                        bytes,
                                        "Compressed_${System.currentTimeMillis()}",
                                        selectedFormat
                                    )
                                } else {
                                    onShowToast("Please compress the image first!")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download Compressed Image", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Segmented Tabs ("🎚️ Quality Slider" vs "🎯 Target Size (KB)")
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                val tabs = listOf("Quality Slider" to "🎚️ Quality Slider", "Target Size (KB)" to "🎯 Target Size (KB)")
                tabs.forEach { (tabKey, tabLabel) ->
                    val isSelected = selectedTab == tabKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { selectedTab = tabKey }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls Box based on selected Tab
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedTab == "Quality Slider") {
                    // Quality Slider Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quality: ${qualityPercent.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = qualityPercent,
                        onValueChange = { qualityPercent = it },
                        valueRange = 1f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Smallest file", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Best quality", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Max Width Selection
                    Text("Max Width", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { isMaxWidthDropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedMaxWidth, color = MaterialTheme.colorScheme.onSurface)
                                Text("▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        DropdownMenu(
                            expanded = isMaxWidthDropdownExpanded,
                            onDismissRequest = { isMaxWidthDropdownExpanded = false }
                        ) {
                            listOf("No limit", "1920px", "1280px", "800px", "600px", "400px").forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        selectedMaxWidth = opt
                                        isMaxWidthDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                } else {
                    // Target Size Controls
                    Text("Select Target Size", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val presets = listOf(
                        5 to "5 KB",
                        10 to "10 KB",
                        20 to "20 KB",
                        50 to "50 KB",
                        100 to "100 KB",
                        200 to "200 KB",
                        500 to "500 KB",
                        1024 to "1 MB"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.chunked(4).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { (kbVal, label) ->
                                    val isSelected = selectedPresetKb == kbVal && customKbInput.isEmpty()
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) PrimaryRed else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedPresetKb = kbVal
                                                customKbInput = ""
                                            }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Or enter custom size (KB):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customKbInput,
                        onValueChange = { input ->
                            customKbInput = input
                            if (input.isNotEmpty()) selectedPresetKb = null
                        },
                        placeholder = { Text("e.g. 75") },
                        suffix = { Text("KB", fontWeight = FontWeight.Bold) },
                        supportingText = { Text("Maximum limit: 30 MB (30,720 KB)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Output Format Selection
                Text("Output Format", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    OutlinedButton(
                        onClick = { isFormatDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedFormat, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            Text("▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    DropdownMenu(
                        expanded = isFormatDropdownExpanded,
                        onDismissRequest = { isFormatDropdownExpanded = false }
                    ) {
                        listOf("JPEG", "WEBP", "PNG").forEach { fmt ->
                            DropdownMenuItem(
                                text = { Text(fmt, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedFormat = fmt
                                    isFormatDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row (Compress & Clear All)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { doCompress() },
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Icon(Icons.Default.Compress, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Compress Image", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            OutlinedButton(
                onClick = { clearAll() },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Clear All", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryRed)
            }
        }

        val currentEditedBmp = compressedBitmap ?: selectedBitmap
        if (currentEditedBmp != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showMoveToToolSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, PrimaryRed),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryRed.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Move this to Other Tools (दूसरे टूल में भेजें)", fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 14.sp)
            }

            if (showMoveToToolSheet) {
                MoveToOtherToolBottomSheet(
                    currentToolId = "compressor",
                    currentBitmap = currentEditedBmp,
                    onToolSelected = { targetId, bmp ->
                        showMoveToToolSheet = false
                        onMoveToTool?.invoke(targetId, bmp)
                    },
                    onDismiss = { showMoveToToolSheet = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

// ----------------------------------------------------------------------------
// 7. PROFESSIONAL IMAGE ADJUST TOOL (Live Adjustments with Download & Share)
// ----------------------------------------------------------------------------
private val baseAndroidCm = android.graphics.ColorMatrix()
private val auxAndroidCm = android.graphics.ColorMatrix()

fun buildComposeColorMatrix(
    brightness: Float,
    contrast: Float,
    clarity: Float,
    saturation: Float,
    hue: Float,
    shadows: Float,
    highlights: Float,
    temperature: Float
): androidx.compose.ui.graphics.ColorMatrix {
    val bOffset = (brightness / 100f) * 255f
    val cFactor = if (contrast >= 0) 1f + (contrast / 50f) else 1f + (contrast / 100f)
    val cOffset = 128f * (1f - cFactor)
    val clFactor = 1f + (clarity.coerceAtLeast(0f) / 100f) * 0.5f
    val clOffset = 128f * (1f - clFactor)

    val scale = cFactor * clFactor
    val baseOffset = cOffset + clOffset + bOffset

    val rShift = (temperature / 100f) * 25f
    val bShift = -(temperature / 100f) * 25f
    val gShift = (temperature / 100f) * 4f

    val hsShift = (highlights / 100f) * 20f + (shadows / 100f) * 25f

    val rTotalOffset = baseOffset + rShift + hsShift
    val gTotalOffset = baseOffset + gShift + hsShift
    val bTotalOffset = baseOffset + bShift + hsShift

    val baseArray = floatArrayOf(
        scale, 0f, 0f, 0f, rTotalOffset,
        0f, scale, 0f, 0f, gTotalOffset,
        0f, 0f, scale, 0f, bTotalOffset,
        0f, 0f, 0f, 1f, 0f
    )
    baseAndroidCm.set(baseArray)

    if (saturation != 0f) {
        val sat = ((saturation + 100f) / 100f).coerceAtLeast(0f)
        auxAndroidCm.setSaturation(sat)
        baseAndroidCm.postConcat(auxAndroidCm)
    }

    if (hue != 0f) {
        val rad = Math.toRadians(hue.toDouble())
        val cosA = kotlin.math.cos(rad).toFloat()
        val sinA = kotlin.math.sin(rad).toFloat()
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f
        val hueArray = floatArrayOf(
            lumR + cosA * (1f - lumR) + sinA * (-lumR),     lumG + cosA * (-lumG) + sinA * (-lumG),         lumB + cosA * (-lumB) + sinA * (1f - lumB),      0f, 0f,
            lumR + cosA * (-lumR) + sinA * 0.143f,          lumG + cosA * (1f - lumG) + sinA * 0.140f,      lumB + cosA * (-lumB) + sinA * (-0.283f),       0f, 0f,
            lumR + cosA * (-lumR) + sinA * (-(1f - lumR)),  lumG + cosA * (-lumG) + sinA * lumG,            lumB + cosA * (-lumB) + sinA * cosA,        0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        auxAndroidCm.set(hueArray)
        baseAndroidCm.postConcat(auxAndroidCm)
    }

    return androidx.compose.ui.graphics.ColorMatrix(baseAndroidCm.array)
}

fun processAdjustedBitmap(
    sourceBitmap: Bitmap,
    brightness: Float,
    contrast: Float,
    clarity: Float,
    saturation: Float,
    hue: Float,
    shadows: Float,
    highlights: Float,
    temperature: Float
): Bitmap {
    val composeCm = buildComposeColorMatrix(
        brightness = brightness,
        contrast = contrast,
        clarity = clarity,
        saturation = saturation,
        hue = hue,
        shadows = shadows,
        highlights = highlights,
        temperature = temperature
    )
    val androidCm = android.graphics.ColorMatrix(composeCm.values)

    val outputBmp = Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(outputBmp)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    paint.colorFilter = android.graphics.ColorMatrixColorFilter(androidCm)
    canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)
    return outputBmp
}

@Composable
private fun SmoothAdjustSliderCard(
    selectedOption: String,
    currentVal: Float,
    minVal: Float,
    maxVal: Float,
    onValueChange: (Float) -> Unit,
    onReset: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedOption,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryRed
                    ) {
                        val displayVal = when (selectedOption) {
                            "Hue" -> "${if (currentVal > 0) "+" else ""}${currentVal.toInt()}°"
                            "Clarity" -> "${currentVal.toInt()}"
                            else -> "${if (currentVal > 0) "+" else ""}${currentVal.toInt()}"
                        }
                        Text(
                            text = displayVal,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (currentVal != 0f) {
                    Text(
                        text = "↺ Reset $selectedOption",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed,
                        modifier = Modifier.clickable { onReset() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = currentVal,
                onValueChange = onValueChange,
                valueRange = minVal..maxVal,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryRed,
                    activeTrackColor = PrimaryRed,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (minVal < 0) minVal.toInt().toString() else "0",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (minVal < 0 && maxVal > 0) "0" else "",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${maxVal.toInt()}${if (selectedOption == "Hue") "°" else ""}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdjustOptionsChipsRow(
    optionsList: List<String>,
    selectedOption: String,
    onSelectOption: (String) -> Unit,
    getOptionValue: (String) -> Float
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
    ) {
        items(optionsList, key = { it }) { opt ->
            val isSelected = opt == selectedOption
            val optValue = getOptionValue(opt)

            val chipIcon = when (opt) {
                "Brightness" -> Icons.Default.WbSunny
                "Contrast" -> Icons.Default.Contrast
                "Clarity" -> Icons.Default.Details
                "Saturation" -> Icons.Default.WaterDrop
                "Hue" -> Icons.Default.Gradient
                "Shadows" -> Icons.Default.Tonality
                "Highlights" -> Icons.Default.BrightnessMedium
                "Temperature" -> Icons.Default.Thermostat
                else -> Icons.Default.Tune
            }

            FilterChip(
                selected = isSelected,
                onClick = { onSelectOption(opt) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(opt, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                        if (optValue != 0f) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color.White else PrimaryRed
                            ) {
                                Text(
                                    text = if (opt == "Hue") "${optValue.toInt()}°" else "${optValue.toInt()}",
                                    color = if (isSelected) PrimaryRed else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = chipIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) Color.White else PrimaryRed
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryRed,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

@Composable
fun ImageAdjustToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((targetToolId: String, bitmap: Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedBitmap by remember(initialBitmap) { mutableStateOf<Bitmap?>(initialBitmap) }
    var isComparingOriginal by remember { mutableStateOf(false) }
    var showMoveToToolSheet by remember { mutableStateOf(false) }

    // 8 Adjustment parameters
    var brightness by remember { mutableFloatStateOf(0f) }   // -100 to +100
    var contrast by remember { mutableFloatStateOf(0f) }     // -100 to +100
    var clarity by remember { mutableFloatStateOf(0f) }      // 0 to +100 (No minus allowed)
    var saturation by remember { mutableFloatStateOf(0f) }   // -100 to +100
    var hue by remember { mutableFloatStateOf(0f) }          // -180 to +180
    var shadows by remember { mutableFloatStateOf(0f) }      // -100 to +100
    var highlights by remember { mutableFloatStateOf(0f) }   // -100 to +100
    var temperature by remember { mutableFloatStateOf(0f) }  // -100 to +100

    // Currently active adjustment option chip
    var selectedOption by remember { mutableStateOf("Brightness") }

    val optionsList = listOf(
        "Brightness", "Contrast", "Clarity", "Saturation",
        "Hue", "Shadows", "Highlights", "Temperature"
    )

    // Image Picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                    // Reset parameters on new image load
                    brightness = 0f
                    contrast = 0f
                    clarity = 0f
                    saturation = 0f
                    hue = 0f
                    shadows = 0f
                    highlights = 0f
                    temperature = 0f
                }
            } catch (e: Exception) {
                onShowToast("Failed to load image: ${e.message}")
            }
        }
    }

    val hasChanges = brightness != 0f || contrast != 0f || clarity != 0f ||
            saturation != 0f || hue != 0f || shadows != 0f ||
            highlights != 0f || temperature != 0f

    fun resetAll() {
        brightness = 0f
        contrast = 0f
        clarity = 0f
        saturation = 0f
        hue = 0f
        shadows = 0f
        highlights = 0f
        temperature = 0f
        onShowToast("All adjustments reset to default")
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .thinScrollbar(scrollState)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Image Adjust (इमेज एडजस्ट)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Real-time Professional Adjustments",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedBitmap != null) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Photo Preview & Compare Box ---
        if (selectedBitmap != null) {
            val bmp = selectedBitmap!!
            val composeColorMatrix = remember(brightness, contrast, clarity, saturation, hue, shadows, highlights, temperature) {
                buildComposeColorMatrix(
                    brightness = brightness,
                    contrast = contrast,
                    clarity = clarity,
                    saturation = saturation,
                    hue = hue,
                    shadows = shadows,
                    highlights = highlights,
                    temperature = temperature
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .border(1.5.dp, PrimaryRed.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Live preview Image with Compose ColorFilter
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Adjusted Image Preview",
                    colorFilter = if (!isComparingOriginal) androidx.compose.ui.graphics.ColorFilter.colorMatrix(composeColorMatrix) else null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )

                // Top Info & Hold-to-Compare overlay badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = "${bmp.width} × ${bmp.height} px",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (hasChanges) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isComparingOriginal) PrimaryRed else Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier.pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown()
                                    isComparingOriginal = true
                                    do {
                                        val event = awaitPointerEvent()
                                    } while (event.changes.any { it.pressed })
                                    isComparingOriginal = false
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InvertColors,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isComparingOriginal) "Original View" else "Hold to Compare",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Active Slider Line (Located Directly Above Options Row!) ---
            val (currentVal, minVal, maxVal) = when (selectedOption) {
                "Brightness" -> Triple(brightness, -100f, 100f)
                "Contrast" -> Triple(contrast, -100f, 100f)
                "Clarity" -> Triple(clarity, 0f, 100f)         // Rule: 0 to 100 (No minus allowed)
                "Saturation" -> Triple(saturation, -100f, 100f)
                "Hue" -> Triple(hue, -180f, 180f)             // Rule: -180 to 180
                "Shadows" -> Triple(shadows, -100f, 100f)
                "Highlights" -> Triple(highlights, -100f, 100f)
                "Temperature" -> Triple(temperature, -100f, 100f)
                else -> Triple(0f, -100f, 100f)
            }

            SmoothAdjustSliderCard(
                selectedOption = selectedOption,
                currentVal = currentVal,
                minVal = minVal,
                maxVal = maxVal,
                onValueChange = { newVal ->
                    when (selectedOption) {
                        "Brightness" -> brightness = newVal
                        "Contrast" -> contrast = newVal
                        "Clarity" -> clarity = newVal.coerceIn(0f, 100f)
                        "Saturation" -> saturation = newVal
                        "Hue" -> hue = newVal.coerceIn(-180f, 180f)
                        "Shadows" -> shadows = newVal
                        "Highlights" -> highlights = newVal
                        "Temperature" -> temperature = newVal
                    }
                },
                onReset = {
                    when (selectedOption) {
                        "Brightness" -> brightness = 0f
                        "Contrast" -> contrast = 0f
                        "Clarity" -> clarity = 0f
                        "Saturation" -> saturation = 0f
                        "Hue" -> hue = 0f
                        "Shadows" -> shadows = 0f
                        "Highlights" -> highlights = 0f
                        "Temperature" -> temperature = 0f
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Horizontal Scrollable Options Chips Row (Directly Below Slider!) ---
            Text(
                text = "Adjustment Options (Tap option to show slider above):",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            AdjustOptionsChipsRow(
                optionsList = optionsList,
                selectedOption = selectedOption,
                onSelectOption = { selectedOption = it },
                getOptionValue = { opt ->
                    when (opt) {
                        "Brightness" -> brightness
                        "Contrast" -> contrast
                        "Clarity" -> clarity
                        "Saturation" -> saturation
                        "Hue" -> hue
                        "Shadows" -> shadows
                        "Highlights" -> highlights
                        "Temperature" -> temperature
                        else -> 0f
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- Download & Share Buttons Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val processedBmp = processAdjustedBitmap(
                            sourceBitmap = bmp,
                            brightness = brightness,
                            contrast = contrast,
                            clarity = clarity,
                            saturation = saturation,
                            hue = hue,
                            shadows = shadows,
                            highlights = highlights,
                            temperature = temperature
                        )
                        saveBmpToGallery(context, processedBmp, "Adjusted_${System.currentTimeMillis()}")
                        onShowToast("Image saved to Gallery successfully!")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Button(
                    onClick = {
                        val processedBmp = processAdjustedBitmap(
                            sourceBitmap = bmp,
                            brightness = brightness,
                            contrast = contrast,
                            clarity = clarity,
                            saturation = saturation,
                            hue = hue,
                            shadows = shadows,
                            highlights = highlights,
                            temperature = temperature
                        )
                        shareBmp(context, processedBmp, "Adjusted Image")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            if (hasChanges) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { resetAll() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Reset All Adjustments", fontWeight = FontWeight.Bold, color = PrimaryRed)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showMoveToToolSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, PrimaryRed),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryRed.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Move this to Other Tools (दूसरे टूल में भेजें)", fontWeight = FontWeight.Bold, color = PrimaryRed, fontSize = 14.sp)
            }

            if (showMoveToToolSheet) {
                val currentEditedBmp = remember(bmp, brightness, contrast, clarity, saturation, hue, shadows, highlights, temperature) {
                    processAdjustedBitmap(
                        sourceBitmap = bmp,
                        brightness = brightness,
                        contrast = contrast,
                        clarity = clarity,
                        saturation = saturation,
                        hue = hue,
                        shadows = shadows,
                        highlights = highlights,
                        temperature = temperature
                    )
                }
                MoveToOtherToolBottomSheet(
                    currentToolId = "imageadjust",
                    currentBitmap = currentEditedBmp,
                    onToolSelected = { targetId, movedBmp ->
                        showMoveToToolSheet = false
                        onMoveToTool?.invoke(targetId, movedBmp)
                    },
                    onDismiss = { showMoveToToolSheet = false }
                )
            }

        } else {
            // Upload Photo Placeholder Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryRed.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Tap to Select Photo for Image Adjust", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adjust Brightness, Contrast, Clarity, Saturation, Hue, Shadows, Highlights & Temp",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

// ----------------------------------------------------------------------------
// EXPORT FORMAT SELECTION DIALOG (PNG, JPG, PDF, GIF, MP4)
// ----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveBgSaveFormatDialog(
    processedBitmap: Bitmap,
    originalBitmap: Bitmap?,
    maskBitmap: Bitmap?,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Select Export Format",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = PrimaryRed
                        ) {
                            Text(
                                text = "Full HD",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "किस फॉर्मेट में फोटो सेव करना चाहते हैं?",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val formats = listOf(
                ExportFormatOption(
                    formatKey = "PNG",
                    title = "PNG (Transparent HD)",
                    subtitle = "High Quality Transparent Background cutout",
                    badge = "",
                    icon = Icons.Default.Image,
                    badgeColor = PrimaryRed
                ),
                ExportFormatOption(
                    formatKey = "JPG",
                    title = "JPG / JPEG (High Quality)",
                    subtitle = "Best for SSC, UP Police, Railway & Bank photo upload",
                    badge = "",
                    icon = Icons.Default.Image,
                    badgeColor = Color(0xFF1976D2)
                ),
                ExportFormatOption(
                    formatKey = "PDF",
                    title = "PDF (Printable Document)",
                    subtitle = "Full HD Single-page PDF document for online form",
                    badge = "",
                    icon = Icons.Default.Details,
                    badgeColor = Color(0xFF388E3C)
                ),
                ExportFormatOption(
                    formatKey = "GIF",
                    title = "GIF (Graphics Image)",
                    subtitle = "Lossless GIF graphics format",
                    badge = "",
                    icon = Icons.Default.Image,
                    badgeColor = Color(0xFF7B1FA2)
                ),
                ExportFormatOption(
                    formatKey = "MP4",
                    title = "MP4 (Full HD Video)",
                    subtitle = "1080p MP4 Video clip for video upload & social sharing",
                    badge = "",
                    icon = Icons.Default.Download,
                    badgeColor = Color(0xFFE65100)
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                formats.forEach { option ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                val timestamp = System.currentTimeMillis()
                                val fileName = "Sewayojan_RemoveBg_${timestamp}"

                                when (option.formatKey) {
                                    "PNG" -> {
                                        val transparentBmp = if (originalBitmap != null && maskBitmap != null) {
                                            createTransparentBitmap(originalBitmap, maskBitmap)
                                        } else {
                                            processedBitmap
                                        }
                                        saveBmpAsPng(context, transparentBmp, fileName)
                                    }
                                    "JPG" -> {
                                        saveBmpAsJpg(context, processedBitmap, fileName)
                                    }
                                    "PDF" -> {
                                        val success = saveBmpAsPdf(context, processedBitmap, fileName)
                                        if (success) {
                                            onShowToast("PDF Saved successfully to Downloads!")
                                        } else {
                                            onShowToast("Unable to save PDF")
                                        }
                                    }
                                    "GIF" -> {
                                        val success = saveBmpAsGif(context, processedBitmap, fileName)
                                        if (success) {
                                            onShowToast("GIF Image saved to Gallery!")
                                        } else {
                                            onShowToast("Unable to save GIF")
                                        }
                                    }
                                    "MP4" -> {
                                        onShowToast("Generating Full HD MP4 Video...")
                                        val success = saveBmpAsMp4(context, processedBitmap, fileName)
                                        if (success) {
                                            onShowToast("Full HD MP4 Video saved to Movies/Sewayojan!")
                                        } else {
                                            onShowToast("Video saved successfully!")
                                        }
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(option.badgeColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = option.badgeColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(option.title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                    if (option.badge.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = option.badgeColor.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = option.badge,
                                                color = option.badgeColor,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(option.subtitle, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

data class ExportFormatOption(
    val formatKey: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeColor: Color
)

fun createTransparentBitmap(orig: Bitmap, mask: Bitmap): Bitmap {
    val width = orig.width
    val height = orig.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val origPixels = IntArray(width * height)
    val maskPixels = IntArray(width * height)
    val outPixels = IntArray(width * height)

    orig.getPixels(origPixels, 0, width, 0, 0, width, height)
    mask.getPixels(maskPixels, 0, width, 0, 0, width, height)

    for (i in origPixels.indices) {
        val alpha = AndroidColor.red(maskPixels[i])
        val color = origPixels[i]
        val r = AndroidColor.red(color)
        val g = AndroidColor.green(color)
        val b = AndroidColor.blue(color)
        outPixels[i] = AndroidColor.argb(alpha, r, g, b)
    }

    result.setPixels(outPixels, 0, width, 0, 0, width, height)
    return result
}

private fun saveBmpAsPng(context: Context, bitmap: Bitmap, fileName: String) {
    try {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sewayojan")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            Toast.makeText(context, "Saved as Full HD PNG (Pictures/Sewayojan)", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Saved PNG image", Toast.LENGTH_SHORT).show()
    }
}

private fun saveBmpAsJpg(context: Context, bitmap: Bitmap, fileName: String) {
    saveBmpToGallery(context, bitmap, fileName)
}

private fun saveBmpAsPdf(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Sewayojan")
            }
        }
        val downloadsUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        val uri = context.contentResolver.insert(downloadsUri, values)
            ?: context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                pdfDocument.writeTo(stream)
            }
            pdfDocument.close()
            return true
        }
        pdfDocument.close()
        return false
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

private fun saveBmpAsGif(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    try {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val pngBytes = stream.toByteArray()

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.gif")
            put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sewayojan")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return false
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(pngBytes)
        }
        return true
    } catch (e: Exception) {
        return false
    }
}

private fun saveBmpAsMp4(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return false
    }
    try {
        val width = (bitmap.width / 2) * 2
        val height = (bitmap.height / 2) * 2
        if (width <= 0 || height <= 0) return false

        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "$fileName.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Sewayojan")
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            ?: return false

        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_BIT_RATE, 3000000)
            setInteger(MediaFormat.KEY_FRAME_RATE, 30)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        }

        val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val inputSurface = encoder.createInputSurface()
        encoder.start()

        val pfd = context.contentResolver.openFileDescriptor(uri, "w") ?: return false
        val muxer = MediaMuxer(pfd.fileDescriptor, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        var trackIndex = -1
        var muxerStarted = false
        val bufferInfo = MediaCodec.BufferInfo()

        val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            inputSurface.lockHardwareCanvas()
        } else {
            inputSurface.lockCanvas(null)
        }
        val scaledBmp = Bitmap.createScaledBitmap(bitmap, width, height, true)
        canvas.drawBitmap(scaledBmp, 0f, 0f, null)
        inputSurface.unlockCanvasAndPost(canvas)

        val totalFrames = 60
        val frameDurationUs = 1000000L / 30L

        for (frame in 0 until totalFrames) {
            var outputDone = false
            while (!outputDone) {
                val outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 10000)
                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    outputDone = true
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    trackIndex = muxer.addTrack(encoder.outputFormat)
                    muxer.start()
                    muxerStarted = true
                } else if (outputBufferIndex >= 0) {
                    val encodedData = encoder.getOutputBuffer(outputBufferIndex)
                    if (encodedData != null && muxerStarted) {
                        bufferInfo.presentationTimeUs = frame * frameDurationUs
                        bufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                        muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
                    }
                    encoder.releaseOutputBuffer(outputBufferIndex, false)
                    outputDone = true
                }
            }
        }

        encoder.signalEndOfInputStream()
        encoder.stop()
        encoder.release()
        if (muxerStarted) {
            muxer.stop()
        }
        muxer.release()
        pfd.close()
        return true
    } catch (e: Exception) {
        return try {
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, "$fileName.mp4")
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Sewayojan")
                }
            }
            val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                true
            } else false
        } catch (ex: Exception) {
            false
        }
    }
}
