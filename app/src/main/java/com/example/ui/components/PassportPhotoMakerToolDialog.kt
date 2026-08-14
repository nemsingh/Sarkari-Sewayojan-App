package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.theme.PrimaryRed
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PassportPhotoSizeOption(
    val name: String,
    val widthCm: Float,
    val heightCm: Float,
    val description: String
)

val PASSPORT_PHOTO_SIZES = listOf(
    PassportPhotoSizeOption("India Passport (3.5×4.5 cm)", 3.5f, 4.5f, "Standard Indian Passport / Visa / Govt Forms"),
    PassportPhotoSizeOption("US Passport (2×2 inch)", 5.08f, 5.08f, "US Visa / Passport Standard"),
    PassportPhotoSizeOption("UK Passport (3.5×4.5 cm)", 3.5f, 4.5f, "UK Visa / Passport"),
    PassportPhotoSizeOption("Visa Photo (5×5 cm)", 5.0f, 5.0f, "Global Schengen & Visa Photo"),
    PassportPhotoSizeOption("Aadhaar Card (3.5×4.5 cm)", 3.5f, 4.5f, "UIDAI Aadhaar Update / Verification"),
    PassportPhotoSizeOption("PAN Card (2.5×3.5 cm)", 2.5f, 3.5f, "NSDL / UTITSL PAN Card Application"),
    PassportPhotoSizeOption("SSC Exam (3.5×4.5 cm)", 3.5f, 4.5f, "SSC CGL, CHSL, GD, MTS Applications"),
    PassportPhotoSizeOption("UPSC Photo (3.5×4.5 cm)", 3.5f, 4.5f, "UPSC CSE / NDA / CDS Online Portal"),
    PassportPhotoSizeOption("Custom Size", 3.5f, 4.5f, "Enter custom Width & Height in CM")
)

val SHEET_COPIES_OPTIONS = listOf(1, 2, 4, 6, 8, 10, 12, 16, 20, 30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportPhotoMakerToolDialog(
    initialBitmap: Bitmap? = null,
    onDismiss: () -> Unit,
    onMoveToTool: ((String, Bitmap) -> Unit)? = null,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current

    // Currently loaded base photo
    var selectedPhotoBitmap by remember { mutableStateOf<Bitmap?>(initialBitmap ?: createDemoPassportPhotoBitmap(context)) }
    var isUserPhotoSelected by remember { mutableStateOf(initialBitmap != null) }

    // Controls State
    var selectedSizeOption by remember { mutableStateOf(PASSPORT_PHOTO_SIZES[0]) }
    var customWidthCmText by remember { mutableStateOf("3.5") }
    var customHeightCmText by remember { mutableStateOf("4.5") }

    var isSizeDropdownExpanded by remember { mutableStateOf(false) }

    // Background Color
    var sheetBackgroundColor by remember { mutableStateOf(Color.White) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    // Copies on sheet
    var copiesCount by remember { mutableStateOf(8) }
    var isCopiesDropdownExpanded by remember { mutableStateOf(false) }

    // Generated Photo Sheet Bitmap & Details
    var generatedSheetBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetDetailsText by remember { mutableStateOf("") }

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    selectedPhotoBitmap = bitmap
                    isUserPhotoSelected = true
                    onShowToast("Photo selected successfully!")
                } else {
                    onShowToast("Unable to load image file.")
                }
            } catch (e: Exception) {
                onShowToast("Failed to pick photo: ${e.localizedMessage}")
            }
        }
    }

    // Generator function
    fun generateSheet() {
        val baseBitmap = selectedPhotoBitmap ?: createDemoPassportPhotoBitmap(context)

        val widthCm = if (selectedSizeOption.name == "Custom Size") {
            customWidthCmText.toFloatOrNull() ?: 3.5f
        } else {
            selectedSizeOption.widthCm
        }

        val heightCm = if (selectedSizeOption.name == "Custom Size") {
            customHeightCmText.toFloatOrNull() ?: 4.5f
        } else {
            selectedSizeOption.heightCm
        }

        val result = generatePassportPhotoSheetBitmap(
            sourcePhoto = baseBitmap,
            photoWidthCm = widthCm,
            photoHeightCm = heightCm,
            sheetBgColor = sheetBackgroundColor,
            copies = copiesCount
        )

        generatedSheetBitmap = result.first
        sheetDetailsText = result.second
    }

    // Auto generate on initial load or whenever inputs change
    LaunchedEffect(selectedPhotoBitmap, selectedSizeOption, customWidthCmText, customHeightCmText, sheetBackgroundColor, copiesCount) {
        generateSheet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Toolbar
        Surface(
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Passport Photo Maker",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Sarkari & Passport Photo Sheet Generator",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryRed.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Pro Tool",
                        color = PrimaryRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Photo Selection Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preview box
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F0))
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        selectedPhotoBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Selected Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } ?: Image(
                            painter = painterResource(id = R.drawable.ic_passport_placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Selected Photo",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isUserPhotoSelected && selectedPhotoBitmap != null) "${selectedPhotoBitmap?.width} × ${selectedPhotoBitmap?.height} px" else "Please select a photo",
                            fontSize = 12.sp,
                            color = if (isUserPhotoSelected) MaterialTheme.colorScheme.onSurfaceVariant else PrimaryRed
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Choose Photo", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Photo Size Dropdown Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Photo Size",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dropdown Trigger Box (matching screenshot 1)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF6F8FA),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSizeDropdownExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedSizeOption.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                                Icon(
                                    imageVector = if (isSizeDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }

                        // Dropdown Menu matching screenshot 1 style
                        DropdownMenu(
                            expanded = isSizeDropdownExpanded,
                            onDismissRequest = { isSizeDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        ) {
                            PASSPORT_PHOTO_SIZES.forEach { option ->
                                val isSelected = option.name == selectedSizeOption.name
                                val optionBg = if (isSelected) Color(0xFF00A86B) else Color.Transparent
                                val optionTextColor = if (isSelected) Color.White else Color(0xFF1E293B)

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                            }
                                            Text(
                                                text = option.name,
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = optionTextColor
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedSizeOption = option
                                        isSizeDropdownExpanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(optionBg)
                                )
                            }
                        }
                    }

                    // Custom Size Inputs if Custom Size selected
                    AnimatedVisibility(visible = selectedSizeOption.name == "Custom Size") {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = customWidthCmText,
                                    onValueChange = { customWidthCmText = it },
                                    label = { Text("Width (cm)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = customHeightCmText,
                                    onValueChange = { customHeightCmText = it },
                                    label = { Text("Height (cm)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Background Color Section (Sheet background) matching screenshot 2
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Background Color",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Color Circles & Custom Square Box (matching screenshot 2)
                    val presetColors = listOf(
                        Color.White,
                        Color(0xFFE0E0E0),
                        Color(0xFFE3F2FD),
                        Color(0xFFFFFDE7)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presetColors.forEach { color ->
                            val isSelected = sheetBackgroundColor == color
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF1976D2) else Color(0xFFD0D0D0),
                                        shape = CircleShape
                                    )
                                    .clickable { sheetBackgroundColor = color },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (color == Color.White || color == Color(0xFFFFFDE7)) Color(0xFF1976D2) else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Custom Color Selector Square Box (matching screenshot 2)
                        val isCustomSelected = !presetColors.contains(sheetBackgroundColor)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(sheetBackgroundColor)
                                .border(
                                    width = if (isCustomSelected) 2.5.dp else 1.dp,
                                    color = if (isCustomSelected) Color(0xFF1976D2) else Color(0xFF888888),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { showColorPickerDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCustomSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Custom",
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Copies on Sheet Section matching screenshot 4
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Copies on Sheet",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dropdown Trigger Box
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF6F8FA),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCopiesDropdownExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$copiesCount copies",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                                Icon(
                                    imageVector = if (isCopiesDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }

                        // Dropdown Menu matching screenshot 4
                        DropdownMenu(
                            expanded = isCopiesDropdownExpanded,
                            onDismissRequest = { isCopiesDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        ) {
                            SHEET_COPIES_OPTIONS.forEach { count ->
                                val isSelected = count == copiesCount
                                val optionBg = if (isSelected) Color(0xFF00A86B) else Color.Transparent
                                val optionTextColor = if (isSelected) Color.White else Color(0xFF1E293B)

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                            }
                                            Text(
                                                text = "$count copies",
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = optionTextColor
                                            )
                                        }
                                    },
                                    onClick = {
                                        copiesCount = count
                                        isCopiesDropdownExpanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(optionBg)
                                )
                            }
                        }
                    }
                }
            }



            // 6. Live Sheet Preview Section
            generatedSheetBitmap?.let { sheetBmp ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GridOn,
                                    contentDescription = null,
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live Photo Sheet Preview",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "300 DPI Ready",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sheet Image Display Container with Shadow & Border
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = sheetBmp.asImageBitmap(),
                                contentDescription = "Generated Photo Sheet Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sheet Details Box matching user request
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Sheet Details",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                    Text(
                                        text = sheetDetailsText,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!isUserPhotoSelected) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Please select a photo to download or share sheet",
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Download & Share Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Download Button
                            Button(
                                onClick = {
                                    if (!isUserPhotoSelected) {
                                        onShowToast("Please select a photo first!")
                                    } else {
                                        saveBitmapToGallery(context, sheetBmp) { success, msg ->
                                            onShowToast(msg)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isUserPhotoSelected) PrimaryRed else Color(0xFF94A3B8)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                            }

                            // Share Button
                            OutlinedButton(
                                onClick = {
                                    if (!isUserPhotoSelected) {
                                        onShowToast("Please select a photo first!")
                                    } else {
                                        shareBitmap(context, sheetBmp, onShowToast)
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, if (isUserPhotoSelected) PrimaryRed else Color(0xFF94A3B8)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isUserPhotoSelected) PrimaryRed else Color(0xFF94A3B8)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    tint = if (isUserPhotoSelected) PrimaryRed else Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Share",
                                    color = if (isUserPhotoSelected) PrimaryRed else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Custom Color Picker Dialog matching screenshot 3
    if (showColorPickerDialog) {
        CustomColorPickerDialog(
            initialColor = sheetBackgroundColor,
            onDismiss = { showColorPickerDialog = false },
            onColorSet = { color ->
                sheetBackgroundColor = color
                showColorPickerDialog = false
            }
        )
    }
}

// Custom Color Picker Dialog matching screenshot with Hue, Saturation, and Value sliders
@Composable
private fun CustomColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSet: (Color) -> Unit
) {
    // Convert initial color to HSV
    val initialHsv = remember(initialColor) {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(initialColor.toArgb(), hsv)
        hsv
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val chosenColor = remember(hue, saturation, value) {
        Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, value)))
    }

    val suggestionPalette = listOf(
        Color(0xFFFF0000), // Red
        Color(0xFF00FFFF), // Cyan
        Color(0xFF0000FF), // Blue
        Color(0xFF00FF00), // Green
        Color(0xFFFF00FF), // Magenta
        Color(0xFFFFFF00), // Yellow
        Color(0xFF000000), // Black
        Color(0xFFFFFFFF)  // White
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF8FAFC),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select colour",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Hue Slider
                HueSlider(hue = hue, onHueChange = { hue = it })

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Saturation Slider
                SaturationSlider(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onSaturationChange = { saturation = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Value Slider
                ValueSlider(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onValueChange = { value = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Suggestions palette & Chosen colour box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Suggestions",
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            suggestionPalette.take(5).forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                        .clickable {
                                            val hsv = FloatArray(3)
                                            AndroidColor.colorToHSV(color.toArgb(), hsv)
                                            hue = hsv[0]
                                            saturation = hsv[1]
                                            value = hsv[2]
                                        }
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Chosen colour",
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.5.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(chosenColor)
                                .border(1.5.dp, Color(0xFF94A3B8), RoundedCornerShape(8.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action Buttons: Cancel and Set
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(onClick = { onColorSet(chosenColor) }) {
                        Text(
                            text = "Set",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit
) {
    val rainbowBrush = remember {
        Brush.horizontalGradient(
            listOf(
                Color.Red,
                Color.Yellow,
                Color.Green,
                Color.Cyan,
                Color.Blue,
                Color.Magenta,
                Color.Red
            )
        )
    }

    Column {
        Text("Hue", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(rainbowBrush)
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
        ) {
            Slider(
                value = hue,
                onValueChange = onHueChange,
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF1D4ED8),
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SaturationSlider(
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationChange: (Float) -> Unit
) {
    val startColor = Color(AndroidColor.HSVToColor(floatArrayOf(hue, 0f, value)))
    val endColor = Color(AndroidColor.HSVToColor(floatArrayOf(hue, 1f, value)))
    val satBrush = Brush.horizontalGradient(listOf(startColor, endColor))

    Column {
        Text("Saturation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(satBrush)
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
        ) {
            Slider(
                value = saturation,
                onValueChange = onSaturationChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF1D4ED8),
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ValueSlider(
    hue: Float,
    saturation: Float,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val startColor = Color.Black
    val endColor = Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, 1f)))
    val valBrush = Brush.horizontalGradient(listOf(startColor, endColor))

    Column {
        Text("Value", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(valBrush)
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF1D4ED8),
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// Generate Passport Photo Sheet Bitmap at 300 DPI on standard A4 size
private fun generatePassportPhotoSheetBitmap(
    sourcePhoto: Bitmap,
    photoWidthCm: Float,
    photoHeightCm: Float,
    sheetBgColor: Color,
    copies: Int
): Pair<Bitmap, String> {
    // 300 DPI standard: 1 cm = 118.11 pixels
    val dpi = 300f
    val cmToPx = dpi / 2.54f // ~118.11 px/cm

    val photoWidthPx = (photoWidthCm * cmToPx).toInt().coerceAtLeast(100)
    val photoHeightPx = (photoHeightCm * cmToPx).toInt().coerceAtLeast(100)

    // Standard A4 sheet size at 300 DPI (210mm x 297mm = 2480 x 3508 px)
    val sheetWidthPx = 2480
    val sheetHeightPx = 3508

    val sheetBitmap = Bitmap.createBitmap(sheetWidthPx, sheetHeightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(sheetBitmap)

    // Fill Sheet Background
    canvas.drawColor(sheetBgColor.toArgb())

    // Scaled source photo bitmap to exact passport dimensions
    val scaledPhoto = Bitmap.createScaledBitmap(sourcePhoto, photoWidthPx, photoHeightPx, true)

    // Gap between photos for easy cutting (3 mm = ~35 px at 300 DPI)
    val gapPx = (0.3f * cmToPx).toInt().coerceAtLeast(20)

    // Margins from top & sides
    val minSideMarginPx = (1.0f * cmToPx).toInt() // ~118 px side margin
    val topMarginPx = (1.0f * cmToPx).toInt() // ~118 px top margin

    // Maximum photos that fit horizontally in one row on A4 width
    val maxColsFit = maxOf(1, (sheetWidthPx - 2 * minSideMarginPx + gapPx) / (photoWidthPx + gapPx))

    // Grid columns layout based on copies requested
    val cols = when {
        copies <= maxColsFit -> copies
        copies == 8 && maxColsFit >= 4 -> 4
        copies == 10 && maxColsFit >= 5 -> 5
        copies == 12 && maxColsFit >= 4 -> 4
        else -> maxColsFit
    }
    val rows = (copies + cols - 1) / cols

    // Center the row grid horizontally on A4
    val totalRowWidth = cols * photoWidthPx + (cols - 1) * gapPx
    val startX = (sheetWidthPx - totalRowWidth) / 2

    // Thin grey border line around each photo for easy scissor/cutter trimming
    val borderPaint = Paint().apply {
        color = AndroidColor.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    var drawnCount = 0
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (drawnCount >= copies) break

            val left = startX + c * (photoWidthPx + gapPx)
            val top = topMarginPx + r * (photoHeightPx + gapPx)

            // Draw photo
            canvas.drawBitmap(scaledPhoto, left.toFloat(), top.toFloat(), null)

            // Draw cutting border line around photo
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                (left + photoWidthPx).toFloat(),
                (top + photoHeightPx).toFloat(),
                borderPaint
            )

            drawnCount++
        }
    }

    val detailsStr = "$sheetWidthPx × $sheetHeightPx px at 300 DPI | $copies Copies (Standard A4 Sheet) | Photo: ${photoWidthCm}×${photoHeightCm} cm"

    return Pair(sheetBitmap, detailsStr)
}

// Helper to save bitmap to gallery
private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, onResult: (Boolean, String) -> Unit) {
    try {
        val fileName = "PassportSheet_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/SarkariPassportPhotos")
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                onResult(true, "✅ Photo sheet saved to Gallery!")
            } else {
                onResult(false, "Failed to save photo sheet.")
            }
        } else {
            val dir = File(context.getExternalFilesDir(null), "PassportPhotos")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            onResult(true, "✅ Saved: ${file.absolutePath}")
        }
    } catch (e: Exception) {
        onResult(false, "Error saving: ${e.localizedMessage}")
    }
}

// Helper to share bitmap via intent
private fun shareBitmap(context: Context, bitmap: Bitmap, onShowToast: (String) -> Unit) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "passport_sheet_share.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, "Generated using Sarkari Sewayojan Passport Photo Maker Tool")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Passport Photo Sheet"))
    } catch (e: Exception) {
        onShowToast("Failed to share image: ${e.localizedMessage}")
    }
}

// Programmatic Demo Passport Photo Bitmap if no photo chosen initially
private fun createDemoPassportPhotoBitmap(context: Context): Bitmap {
    val width = 413
    val height = 531
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)

    // Background - Light Studio Blue
    canvas.drawColor(AndroidColor.rgb(220, 235, 252))

    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_passport_placeholder)
    if (drawable != null) {
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
    }

    return bmp
}
