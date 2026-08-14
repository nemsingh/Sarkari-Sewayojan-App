package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import com.example.ui.theme.SurfaceContainerHigh
import com.example.data.local.JobOpportunity
import com.example.data.local.AdmitCardItem
import com.example.data.local.ExamResultItem
import com.example.ui.screens.formatDisplayLastDate
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ServiceDialog(
    serviceType: String,
    allJobs: List<com.example.data.local.JobOpportunity> = emptyList(),
    admitCards: List<AdmitCardItem> = emptyList(),
    examResults: List<ExamResultItem> = emptyList(),
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit = {},
    onShowToast: (String) -> Unit,
    onToggleBookmark: ((com.example.data.local.JobOpportunity) -> Unit)? = null
) {
    BackHandler {
        onDismiss()
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = serviceType,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Official Sewayojan Directory",
                            style = MaterialTheme.typography.labelMedium,
                            color = SecondaryGray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (serviceType) {
                "Latest Jobs" -> LatestJobsDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Admit Card" -> AdmitCardDialogContent(allJobs, admitCards, onDismiss, onJobSelect, onShowToast)
                "Result", "Results" -> ResultDialogContent(allJobs, examResults, onDismiss, onJobSelect, onShowToast)
                "Answer Key" -> AnswerKeyDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Certificate Verification", "Verification" -> MoreServicesDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Admission" -> AdmissionDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Syllabus" -> SyllabusDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Important" -> ImportantDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Offline Job" -> OfflineJobDialogContent(allJobs, onDismiss, onJobSelect, onShowToast)
                "Recharge" -> RechargeDialogContent(onDismiss, onShowToast)
                "Pay Bills" -> PayBillsDialogContent(onDismiss, onShowToast)
                "Help & Support", "Help", "Support" -> HelpSupportDialogContent(onDismiss, onShowToast)
                "About Sewayojan", "About Us" -> AboutSewayojanDialogContent(onDismiss, onShowToast)
                "Privacy Policy & Disclaimer", "Privacy Policy", "Disclaimer" -> PrivacyPolicyDisclaimerDialogContent(onDismiss, onShowToast)
                "OTR Details", "OTR (One Time Registration)", "OTR" -> OtrDetailsDialogContent(onDismiss, onShowToast)
                "Saved Jobs", "Saved Posts", "Saved" -> SavedJobsDialogContent(allJobs, onDismiss, onJobSelect, onShowToast, onToggleBookmark)
                else -> GenericServiceContent(serviceType, allJobs, onDismiss, onJobSelect, onShowToast)
            }
        }
    }
}

@Composable
private fun RechargeDialogContent(
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var mobileNumber by remember { mutableStateOf("") }
    var selectedOperator by remember { mutableStateOf("Jio") }
    var selectedPlan by remember { mutableStateOf("₹299 - 1.5GB/day (28 Days)") }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Mobile Recharge",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSuccess) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recharge Successful!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "$selectedOperator • $mobileNumber\n$selectedPlan",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text("Done")
                }
            }
        } else {
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { if (it.length <= 10) mobileNumber = it },
                label = { Text("Mobile Number") },
                placeholder = { Text("Enter 10-digit mobile number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Operator",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Jio", "Airtel", "Vi", "BSNL").forEach { operator ->
                    val isSelected = selectedOperator == operator
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryRed else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedOperator = operator }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = operator,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Popular Plans",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val plans = listOf(
                "₹299 - 1.5GB/day (28 Days)",
                "₹666 - 1.5GB/day (84 Days)",
                "₹719 - 2.0GB/day (84 Days)"
            )

            plans.forEach { plan ->
                val isSelected = selectedPlan == plan
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedPlan = plan },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PrimaryRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryRed) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = plan,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (mobileNumber.length == 10) {
                        isSuccess = true
                        onShowToast("Recharge initiated for $mobileNumber")
                    } else {
                        onShowToast("Please enter a valid 10-digit mobile number")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text("Proceed to Pay", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            }
        }
    }
}

@Composable
private fun PayBillsDialogContent(
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var consumerId by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electricity") }
    var isPaid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Pay Utility Bills",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isPaid) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Bill Payment Successful!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "$selectedCategory Bill • Consumer ID: $consumerId\nPaid: ₹1,240.00",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text("Done")
                }
            }
        } else {
            Text(
                text = "Select Bill Category",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val categories = listOf("Electricity", "Water", "Gas", "FASTag")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryRed else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = consumerId,
                onValueChange = { consumerId = it },
                label = { Text("Consumer / Account Number") },
                placeholder = { Text("e.g. UPPCL 1029384756") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Billed Amount", style = MaterialTheme.typography.bodyMedium)
                        Text("₹1,240.00", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Due Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("10 Aug 2026", style = MaterialTheme.typography.bodySmall, color = PrimaryRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (consumerId.isNotBlank()) {
                        isPaid = true
                        onShowToast("Bill payment processed for $consumerId")
                    } else {
                        onShowToast("Please enter consumer/account number")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text("Pay ₹1,240.00 Now", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            }
        }
    }
}

@Composable
private fun AnswerKeyDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Official Answer Keys",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matched = allJobs.filter { it.category.contains("Answer", ignoreCase = true) || it.title.contains("Answer", ignoreCase = true) || it.title.contains("Key", ignoreCase = true) }

        if (matched.isNotEmpty()) {
            matched.forEach { job ->
                JobCardItemRow(job = job, buttonText = "View Key", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val answerKeys = listOf(
                "SSC CGL Tier-I Answer Key 2026",
                "UP Police Constable Final Key 2026",
                "RRB Technician Stage-I Answer Sheet",
                "IBPS PO Prelims Provisional Key"
            )
            answerKeys.forEach { title ->
                val job = com.example.data.local.JobOpportunity(title = title, category = "Answer Key")
                JobCardItemRow(job = job, buttonText = "View Key", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
        }
    }
}

@Composable
private fun SyllabusDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Exam Syllabus 2026",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matched = allJobs.filter { it.category.contains("Syllabus", ignoreCase = true) || it.title.contains("Syllabus", ignoreCase = true) }

        if (matched.isNotEmpty()) {
            matched.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Syllabus PDF", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val syllabusList = listOf(
                "SSC CGL 2026 Tier 1 & 2 Syllabus",
                "UP Police Constable Exam Pattern & Topics",
                "RRB NTPC & Technician Full Syllabus",
                "UPSC Civil Services Prelims + Mains Syllabus"
            )
            syllabusList.forEach { title ->
                val job = com.example.data.local.JobOpportunity(title = title, category = "Syllabus")
                JobCardItemRow(job = job, buttonText = "Syllabus PDF", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close Syllabus Portal")
        }
    }
}

@Composable
private fun AdmissionDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Admissions 2026",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matched = allJobs.filter { it.category.contains("Admission", ignoreCase = true) || it.title.contains("Admission", ignoreCase = true) }

        if (matched.isNotEmpty()) {
            matched.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Apply", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val admissions = listOf(
                "JEECUP UP Polytechnic Counseling 2026",
                "UP B.Ed Joint Entrance Exam Counseling",
                "DU CSAS Undergraduate Admission Portal",
                "UP ITI NCVT Trades Online Admission Form"
            )
            admissions.forEach { title ->
                val job = com.example.data.local.JobOpportunity(title = title, category = "Admission")
                JobCardItemRow(job = job, buttonText = "Apply", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun MoreServicesDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Government Certificates & Services",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matched = allJobs.filter { it.category.contains("Certificate", ignoreCase = true) || it.category.contains("Verification", ignoreCase = true) }

        if (matched.isNotEmpty()) {
            matched.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Open Service", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val services = listOf(
                "UP Income Certificate (आय प्रमाण पत्र)",
                "UP Caste Certificate (जाति प्रमाण पत्र)",
                "UP Domicile Certificate (निवास प्रमाण पत्र)",
                "NCS Job Seeker Registration Card",
                "DigiLocker Verification Portal",
                "UP Employment Exchange Card Renewal"
            )

            services.forEach { service ->
                val job = com.example.data.local.JobOpportunity(title = service, category = "Certificate Verification")
                JobCardItemRow(job = job, buttonText = "Open Service", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun LatestJobsDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Latest Govt Recruitment 2026",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val jobsList = if (allJobs.isNotEmpty()) {
            allJobs.filter { it.category.contains("Jobs", ignoreCase = true) || it.category.contains("Latest", ignoreCase = true) }
                .ifEmpty { allJobs }
        } else emptyList()

        if (jobsList.isEmpty()) {
            val fallbackJobs = listOf(
                "SSC CGL Recruitment 2026 (17,727 Posts)",
                "UP Police Constable Re-Exam 2026",
                "RRB Technician Grade 1 & 3 (9,144 Posts)",
                "IBPS PO XIV Online Form 2026"
            )
            fallbackJobs.forEach { title ->
                val job = com.example.data.local.JobOpportunity(title = title, category = "Latest Jobs")
                JobCardItemRow(job = job, buttonText = "Apply", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            jobsList.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Apply", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun AdmitCardDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    admitCards: List<AdmitCardItem>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Admit Cards 2026",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matchedJobs = allJobs.filter { it.category.contains("Admit", ignoreCase = true) || it.title.contains("Admit", ignoreCase = true) }

        if (matchedJobs.isNotEmpty()) {
            matchedJobs.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Download", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else if (admitCards.isNotEmpty()) {
            admitCards.forEach { card ->
                val job = com.example.data.local.JobOpportunity(
                    title = card.examTitle,
                    category = "Admit Card",
                    applyUrl = card.downloadUrl
                )
                JobCardItemRow(job = job, buttonText = "Download", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val fallbackAdmitCards = listOf(
                "SSC CGL Tier I Admit Card & Exam City",
                "UP Police Constable Exam City Slip",
                "RRB ALP Stage II Call Letter",
                "NTA NEET UG Counseling Call Letter"
            )
            fallbackAdmitCards.forEach { title ->
                val job = com.example.data.local.JobOpportunity(title = title, category = "Admit Card")
                JobCardItemRow(job = job, buttonText = "Download", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun ResultDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    examResults: List<ExamResultItem>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Declared Exam Results",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matchedJobs = allJobs.filter { it.category.contains("Result", ignoreCase = true) || it.title.contains("Result", ignoreCase = true) }

        if (matchedJobs.isNotEmpty()) {
            matchedJobs.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Check Result", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else if (examResults.isNotEmpty()) {
            examResults.forEach { result ->
                val job = com.example.data.local.JobOpportunity(
                    title = result.examTitle,
                    category = "Result",
                    applyUrl = result.resultUrl
                )
                JobCardItemRow(job = job, buttonText = "Check Result", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val fallbackResults = listOf(
                "SSC CHSL Final Result & Cutoff 2026",
                "UP Board 10th/12th Scrutiny Result",
                "IBPS RRB Officer Scale I Result",
                "UPSC IAS Civil Services Final Result"
            )
            fallbackResults.forEach { title ->
                val job = com.example.data.local.JobOpportunity(title = title, category = "Result")
                JobCardItemRow(job = job, buttonText = "Check Result", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun ImportantDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Important Public Services",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matched = allJobs.filter { it.category.contains("Important", ignoreCase = true) || it.title.contains("Scholarship", ignoreCase = true) }

        if (matched.isNotEmpty()) {
            matched.forEach { job ->
                JobCardItemRow(job = job, buttonText = "Open Link", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val services = listOf(
                "UP Scholarship 2026 Online Application Form",
                "NSP National Scholarship Portal 2026",
                "PAN Card Online Form / Update",
                "Aadhar Card Address / Mobile Update",
                "Voter ID Registration & Correction Form"
            )

            services.forEach { service ->
                val job = com.example.data.local.JobOpportunity(title = service, category = "Important")
                JobCardItemRow(job = job, buttonText = "Open Link", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun OfflineJobDialogContent(
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Offline Recruitment & District Jobs",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val matched = allJobs.filter { it.category.contains("Offline", ignoreCase = true) || it.title.contains("Offline", ignoreCase = true) }

        if (matched.isNotEmpty()) {
            matched.forEach { job ->
                JobCardItemRow(job = job, buttonText = "PDF / Apply", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        } else {
            val offlineJobs = listOf(
                "District Court Clerk Offline Postal Form 2026",
                "UP Health Department Contract Worker Form",
                "Indian Army Ordnance Depot Offline Form",
                "District Employment Exchange Direct Interview"
            )

            offlineJobs.forEach { jobTitle ->
                val job = com.example.data.local.JobOpportunity(title = jobTitle, category = "Offline Job")
                JobCardItemRow(job = job, buttonText = "PDF / Apply", onSelect = {
                    onJobSelect(job)
                    onDismiss()
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun GenericServiceContent(
    serviceType: String,
    allJobs: List<com.example.data.local.JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = PrimaryRed,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "$serviceType Portal",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Showing latest posts and updates for $serviceType.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        val matchedJobs = allJobs.filter {
            it.category.contains(serviceType, ignoreCase = true) || it.title.contains(serviceType, ignoreCase = true)
        }.ifEmpty { allJobs.take(5) }

        matchedJobs.forEach { job ->
            JobCardItemRow(job = job, buttonText = "View Details", onSelect = {
                onJobSelect(job)
                onDismiss()
            })
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun HelpSupportDialogContent(
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Founder & Content Administration Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryRed.copy(alpha = 0.1f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "FOUNDER & CONTENT ADMINISTRATION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryRed,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 3.dp,
                        color = Color.White,
                        modifier = Modifier
                            .size(52.dp)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_icon_1785140212852),
                                contentDescription = "Sarkari Sewayojan Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(1.26f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Vikas Kumar",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Founder & Content Administrator – Sarkari Sewayojan",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = SecondaryGray,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(12.dp))

                // Website Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sarkarisewayojan.com"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("Opening www.sarkarisewayojan.com")
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Website",
                        tint = PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "www.sarkarisewayojan.com",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0000EF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Email 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:Helpdesk@sarkarisewayojan.com"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("Mail: Helpdesk@sarkarisewayojan.com")
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Helpdesk@sarkarisewayojan.com",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Email 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vikaskumar12121999@gmail.com"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("Mail: vikaskumar12121999@gmail.com")
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = "Personal Email",
                        tint = PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "vikaskumar12121999@gmail.com",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Our Other Websites Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Our other Websites:-",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sewayojan-tools.vercel.app"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("Opening sewayojan-tools")
                            }
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "https://sewayojan-tools.vercel.app",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0000EF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.elvoraservices.com"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("Opening elvoraservices")
                            }
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "https://www.elvoraservices.com",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0000EF)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content Management & Supervision Description Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Content Supervision & Quality",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "He is actively involved in content management and publishing related to education, government recruitment updates, and digital information services. The quality, clarity, and usefulness of the content published on the website are maintained under his supervision.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AboutSewayojanDialogContent(
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val keyFeatures = remember {
        listOf(
            "Real-time Sarkari Result & Score Card updates",
            "Direct Official Links for Online Application",
            "Verified Admit Cards & Exam City Intimation",
            "Sewayojan UP & State Rojgar Mela updates",
            "Latest Answer Keys & Official Cut-Off Marks",
            "Complete Syllabus PDF Downloads"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Who We Are Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryRed.copy(alpha = 0.1f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "ABOUT US",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryRed,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Who We Are (हमारे बारे में)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Sarkari Sewayojan (SarkariSewayojan.com) is an independent educational news and career information platform founded to help millions of Indian youth find reliable career guidance. We track recruitment notices across Central Government departments, State Public Service Commissions (UPPSC, MPPSC, BPSC, UKPSC, etc.), Staff Selection Commission (SSC), Railway Recruitment Board (RRB), Defence, Banking (IBPS, SBI), Teaching (CTET, UPTET), and State Employment Exchanges like Sewayojan UP (Rozgar Sangam Uttar Pradesh).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "सरकारी सेवायोजन (SarkariSewayojan.com) भारत का एक अग्रणी और विश्वसनीय जॉब पोर्टल है। हमारा उद्देश्य देश भर के युवाओं को सरकारी नौकरियों (Sarkari Naukri), सरकारी रिजल्ट (Sarkari Result), प्रवेश पत्र (Admit Card), उत्तर कुंजी (Answer Key), पाठ्यक्रम (Syllabus) और उत्तर प्रदेश सेवायोजन (Sewayojan UP) की सटीक और सबसे तेज जानकारी प्रदान करना है।",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Key Features Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Key Features of Sarkari Sewayojan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                keyFeatures.forEachIndexed { index, feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (index < keyFeatures.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PrivacyPolicyDisclaimerDialogContent(
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val officialGovtSources = remember {
        listOf(
            "Staff Selection Commission (SSC)" to "https://ssc.gov.in",
            "Union Public Service Commission (UPSC)" to "https://upsc.gov.in",
            "UP Public Service Commission (UPPSC)" to "https://uppsc.up.nic.in",
            "UP Subordinate Services (UPSSSC)" to "https://upsssc.gov.in",
            "UP Rojgar Sangam / Sewayojan" to "https://sewayojan.up.nic.in",
            "Railway Recruitment Boards (RRB)" to "https://indianrailways.gov.in",
            "Institute of Banking Personnel (IBPS)" to "https://ibps.in",
            "National Testing Agency (NTA)" to "https://nta.ac.in"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // NON-AFFILIATION DISCLAIMER CARD (MANDATORY FOR GOOGLE PLAY POLICY)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryRed.copy(alpha = 0.12f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "IMPORTANT DISCLAIMER (अस्वीकरण)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryRed,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Non-Government Affiliation Notice",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "This application is strictly an independent educational and informational service. We are NOT an official government entity, agency, or department, and we are NOT affiliated with, sponsored by, or endorsed by any Government or government body in any manner.",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "यह ऐप किसी भी सरकारी संस्था, विभाग या निकाय से संबद्ध नहीं है। यह केवल परीक्षार्थियों एवं छात्रों की सुविधा हेतु सार्वजनिक रूप से उपलब्ध आधिकारिक सरकारी वेबसाइटों से भर्ती, प्रवेश पत्र व परीक्षा परिणाम की जानकारी संकलित कर प्रस्तुत करता है।",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, lineHeight = 21.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SOURCES OF GOVERNMENT INFORMATION CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Official Government Sources (सूचना के स्रोत)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "All recruitment information, dates, and exam notifications displayed in the app are gathered directly from the respective public official portals:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                officialGovtSources.forEachIndexed { index, (orgName, url) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    onShowToast("Unable to open $url")
                                }
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = orgName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = url,
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryRed
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Open Source",
                            tint = SecondaryGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (index < officialGovtSources.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PRIVACY POLICY & DATA SAFETY CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Privacy Policy & Data Safety",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "• Local Storage Only: Your saved bookmarks, OTR notes, and photo edits remain locally on your device in secure app storage.\n\n• No Sensitive Tracking: We do not collect or sell your personal credentials, contact lists, or financial data.\n\n• Permissions: Camera is strictly utilized for passport photo creation and QR code verification upon your active selection.\n\n• Push Notifications: Used solely for sending timely recruitment alerts and admit card release notifications.",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sarkarisewayojan.com/privacy-policy/"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("https://sarkarisewayojan.com/privacy-policy/")
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Web Policy", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:Helpdesk@sarkarisewayojan.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Privacy & Policy Inquiry - Sarkari Sewayojan")
                                }
                                context.startActivity(emailIntent)
                            } catch (e: Exception) {
                                onShowToast("Helpdesk@sarkarisewayojan.com")
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Contact Grievance", fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GlobalSearchDialog(
    initialQuery: String = "",
    allJobs: List<com.example.data.local.JobOpportunity> = emptyList(),
    admitCards: List<AdmitCardItem> = emptyList(),
    examResults: List<ExamResultItem> = emptyList(),
    onDismiss: () -> Unit,
    onJobSelect: (com.example.data.local.JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit
) {
    BackHandler {
        onDismiss()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    var draftText by remember { mutableStateOf(initialQuery) }
    var appliedText by remember { mutableStateOf(initialQuery) }

    val popularSuggestions = remember {
        listOf("Railway RRB", "UP Police", "SSC CGL", "Admit Card", "Bank PO", "10th Pass", "Result", "UPSC", "Syllabus")
    }

    val searchResults = remember(appliedText, allJobs, admitCards, examResults) {
        val q = appliedText.trim().lowercase()
        if (q.isBlank()) {
            emptyList()
        } else {
            val matchedJobs = allJobs.filter { job ->
                job.title.lowercase().contains(q) ||
                job.category.lowercase().contains(q) ||
                job.department.lowercase().contains(q) ||
                job.description.lowercase().contains(q) ||
                job.vacancies.lowercase().contains(q)
            }

            val matchedCardsAsJobs = admitCards.filter { card ->
                card.examTitle.lowercase().contains(q) || card.department.lowercase().contains(q)
            }.map { card ->
                com.example.data.local.JobOpportunity(
                    title = card.examTitle,
                    category = "Admit Card",
                    department = card.department,
                    vacancies = card.examDateOrCenter,
                    applyUrl = card.downloadUrl
                )
            }

            val matchedResultsAsJobs = examResults.filter { res ->
                res.examTitle.lowercase().contains(q) || res.department.lowercase().contains(q)
            }.map { res ->
                com.example.data.local.JobOpportunity(
                    title = res.examTitle,
                    category = "Result",
                    department = res.department,
                    vacancies = res.releasedDate,
                    applyUrl = res.resultUrl
                )
            }

            (matchedJobs + matchedCardsAsJobs + matchedResultsAsJobs).distinctBy { it.title.lowercase().trim() }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryRed
                        )
                    }
                    Text(
                        text = "Search Jobs & Posts",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Input Box with explicit Search Lens Button
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = draftText,
                    onValueChange = { draftText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            appliedText = draftText.trim()
                            keyboardController?.hide()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(start = 16.dp, end = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (draftText.isEmpty()) {
                                    Text(
                                        text = "Enter job title, post, or category...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                innerTextField()
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (draftText.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            draftText = ""
                                            appliedText = ""
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryRed,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clickable {
                                            appliedText = draftText.trim()
                                            keyboardController?.hide()
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search Lens",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (appliedText.isBlank()) {
                // Popular Search Suggestions
                Text(
                    text = "Popular Search Keywords:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        popularSuggestions.chunked(3).forEach { rowChips ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowChips.forEach { suggestion ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, PrimaryRed.copy(alpha = 0.3f)),
                                        modifier = Modifier.clickable {
                                            draftText = suggestion
                                            appliedText = suggestion
                                            keyboardController?.hide()
                                        }
                                    ) {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = PrimaryRed,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Type any keyword above and click the Search Lens icon 🔍 to see matching posts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                // Search Results
                Text(
                    text = "Found ${searchResults.size} results for '${appliedText}'",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No jobs or posts found matching '${appliedText}'",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    draftText = ""
                                    appliedText = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                            ) {
                                Text("Clear Search")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(
                            items = searchResults,
                            key = { index, job -> "${index}_${job.applyUrl.ifBlank { job.title }}" }
                        ) { _, job ->
                            JobCardItemRow(
                                job = job,
                                buttonText = "View Details",
                                onSelect = {
                                    onJobSelect(job)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobCardItemRow(
    job: com.example.data.local.JobOpportunity,
    buttonText: String = "View Details",
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PrimaryRed.copy(alpha = 0.12f),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = job.category.ifBlank { "Job" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (job.department.isNotBlank()) {
                        Text(
                            text = job.department,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                )
                if (job.vacancies.isNotBlank() || job.dateText.isNotBlank()) {
                    Text(
                        text = job.vacancies.ifEmpty { job.dateText },
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryRed,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text(buttonText, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

data class OtrFieldItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    var name: String,
    var value: String = "",
    val isImage: Boolean = false
)

private fun loadOtrFieldsFromStorage(context: Context): List<OtrFieldItem> {
    val prefs = context.getSharedPreferences("SARKARI_OTR_PREFS", Context.MODE_PRIVATE)
    val jsonStr = prefs.getString("otr_fields_json", null)
    if (jsonStr.isNullOrEmpty()) {
        return getDefaultOtrFields()
    }
    return try {
        val jsonArray = JSONArray(jsonStr)
        val list = mutableListOf<OtrFieldItem>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                OtrFieldItem(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    name = obj.optString("name", "Field"),
                    value = obj.optString("value", ""),
                    isImage = obj.optBoolean("isImage", false)
                )
            )
        }
        if (list.isEmpty()) getDefaultOtrFields() else list
    } catch (e: Exception) {
        getDefaultOtrFields()
    }
}

private fun saveOtrFieldsToStorage(context: Context, fields: List<OtrFieldItem>) {
    val prefs = context.getSharedPreferences("SARKARI_OTR_PREFS", Context.MODE_PRIVATE)
    val jsonArray = JSONArray()
    for (f in fields) {
        val obj = JSONObject()
        obj.put("id", f.id)
        obj.put("name", f.name)
        obj.put("value", f.value)
        obj.put("isImage", f.isImage)
        jsonArray.put(obj)
    }
    prefs.edit().putString("otr_fields_json", jsonArray.toString()).apply()
}

private fun getDefaultOtrFields(): List<OtrFieldItem> {
    return listOf(
        OtrFieldItem(name = "Address", value = "", isImage = false),
        OtrFieldItem(name = "10th Roll Number", value = "", isImage = false),
        OtrFieldItem(name = "10th Certificate Number", value = "", isImage = false),
        OtrFieldItem(name = "12th Roll Number", value = "", isImage = false),
        OtrFieldItem(name = "12th Certificate Number", value = "", isImage = false),
        OtrFieldItem(name = "Graduation Roll Number", value = "", isImage = false),
        OtrFieldItem(name = "Graduation Enrollment Number", value = "", isImage = false),
        OtrFieldItem(name = "School Name", value = "", isImage = false),
        OtrFieldItem(name = "College Name", value = "", isImage = false),
        OtrFieldItem(name = "Cast Certificate Number", value = "", isImage = false),
        OtrFieldItem(name = "Income Certificate Number", value = "", isImage = false),
        OtrFieldItem(name = "Photo", value = "", isImage = true),
        OtrFieldItem(name = "Signature", value = "", isImage = true)
    )
}

@Composable
private fun OtrDetailsDialogContent(
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val fieldsList = remember { mutableStateListOf<OtrFieldItem>().apply { addAll(loadOtrFieldsFromStorage(context)) } }
    var showAddFieldDialog by remember { mutableStateOf(false) }

    var activePickerFieldId by remember { mutableStateOf<String?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val targetId = activePickerFieldId
            if (targetId != null) {
                val index = fieldsList.indexOfFirst { it.id == targetId }
                if (index != -1) {
                    val old = fieldsList[index]
                    fieldsList[index] = old.copy(value = it.toString())
                    onShowToast("Image selected for ${old.name}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.6f)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ध्यान दें: OTR Details आपकी सुविधा के लिए Sarkari Sewayojan ऐप में केवल offline सुरक्षित की गई है। ऐप delete होने की स्थिति में अथवा uninstall करने पर यह डेटा डिलीट हो जाएगा।",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryRed,
                        lineHeight = 17.sp
                    )
                }
            }

            fieldsList.forEachIndexed { index, item ->
                OtrFieldCardRow(
                    item = item,
                    onValueChange = { newValue ->
                        fieldsList[index] = item.copy(value = newValue)
                    },
                    onDelete = {
                        val removedName = item.name
                        fieldsList.removeAt(index)
                        onShowToast("Field '$removedName' removed")
                    },
                    onCopy = {
                        if (item.value.isNotBlank()) {
                            clipboardManager.setText(AnnotatedString(item.value))
                            onShowToast("Copied ${item.name} to clipboard")
                        } else {
                            onShowToast("Field is empty")
                        }
                    },
                    onPickImage = {
                        activePickerFieldId = item.id
                        galleryLauncher.launch("image/*")
                    },
                    onDownloadImage = {
                        if (item.value.isNotBlank()) {
                            onShowToast("${item.name} image attached")
                        } else {
                            onShowToast("No image attached yet")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showAddFieldDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E88E5),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Fields", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }

                Button(
                    onClick = {
                        saveOtrFieldsToStorage(context, fieldsList)
                        onShowToast("Details saved offline on your phone memory!")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(110.dp))
        }

        if (showAddFieldDialog) {
            AddNewFieldDialog(
                onDismiss = { showAddFieldDialog = false },
                onAddField = { name, isImage ->
                    if (name.isNotBlank()) {
                        val newItem = OtrFieldItem(name = name, value = "", isImage = isImage)
                        fieldsList.add(newItem)
                        saveOtrFieldsToStorage(context, fieldsList)
                        onShowToast("New field '$name' added!")
                        showAddFieldDialog = false
                    } else {
                        onShowToast("Please enter a field name")
                    }
                }
            )
        }
    }
}

@Composable
private fun OtrFieldCardRow(
    item: OtrFieldItem,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onPickImage: () -> Unit,
    onDownloadImage: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!item.isImage) {
                    BasicTextField(
                        value = item.value,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (item.value.isEmpty()) {
                                Text(
                                    text = item.name,
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (item.value.isBlank()) item.name else "${item.name} (Attached)",
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        color = if (item.value.isBlank()) Color.Gray else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (item.value.isBlank()) FontWeight.Normal else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onPickImage) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Select Image",
                            tint = PrimaryRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = Color.DarkGray,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = if (!item.isImage) onCopy else onDownloadImage,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (!item.isImage) Icons.Default.ContentCopy else Icons.Default.Download,
                contentDescription = if (!item.isImage) "Copy" else "Download",
                tint = Color.DarkGray,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun AddNewFieldDialog(
    onDismiss: () -> Unit,
    onAddField: (fieldName: String, isImage: Boolean) -> Unit
) {
    var fieldName by remember { mutableStateOf("") }
    var selectedTypeIsImage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Field",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Select field type:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedTypeIsImage = false }
                        ) {
                            RadioButton(
                                selected = !selectedTypeIsImage,
                                onClick = { selectedTypeIsImage = false },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryRed)
                            )
                            Text("Text", fontSize = 15.sp, maxLines = 1)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedTypeIsImage = true }
                        ) {
                            RadioButton(
                                selected = selectedTypeIsImage,
                                onClick = { selectedTypeIsImage = true },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryRed)
                            )
                            Text("Image", fontSize = 15.sp, maxLines = 1)
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.LightGray.copy(alpha = 0.6f)
                )

                OutlinedTextField(
                    value = fieldName,
                    onValueChange = { fieldName = it },
                    placeholder = { Text("Field Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onAddField(fieldName.trim(), selectedTypeIsImage) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text(
                        text = "ADD",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SavedJobsDialogContent(
    allJobs: List<JobOpportunity>,
    onDismiss: () -> Unit,
    onJobSelect: (JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit,
    onToggleBookmark: ((JobOpportunity) -> Unit)? = null
) {
    val savedJobs = remember(allJobs) { allJobs.filter { it.isSaved } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saved Jobs & Posts (${savedJobs.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (savedJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Saved Jobs Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "When you click 'Save' or 'Bookmark' on any job post, it will be saved here on your device for quick offline reference.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = savedJobs,
                    key = { idx, item -> "${idx}_${item.applyUrl.ifBlank { item.title }}" }
                ) { _, job ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        tonalElevation = 2.dp,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onJobSelect(job) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = job.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        onToggleBookmark?.invoke(job)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Remove Bookmark",
                                        tint = PrimaryRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val displayDate = formatDisplayLastDate(job.vacancies, job.dateText)
                                Text(
                                    text = "Last Date: $displayDate",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimaryRed
                                )

                                Button(
                                    onClick = { onJobSelect(job) },
                                    shape = CircleShape,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                                ) {
                                    Text(
                                        text = "View Details",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
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

