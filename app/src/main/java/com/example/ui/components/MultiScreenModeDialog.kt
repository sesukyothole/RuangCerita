package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun MultiScreenModeDialog(
    currentMode: MultiScreenMode,
    onModeSelected: (MultiScreenMode) -> Unit,
    onDismiss: () -> Unit,
    isDark: Boolean = true
) {
    val goldAccent = if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
    val goldSoft = if (isDark) Color(0x33D4AF37) else Color(0x22B8860B)
    val bgColor = if (isDark) Color(0xFF13141F) else Color(0xFFFFFFFF)
    val cardBg = if (isDark) Color(0xFF1E202E) else Color(0xFFF3F5FA)
    val textColor = if (isDark) Color.White else Color(0xFF181920)
    val textSecondary = if (isDark) Color.LightGray else Color(0xFF555866)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = bgColor,
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(goldSoft, goldAccent, goldSoft))
            ),
            tonalElevation = 10.dp,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("multi_screen_mode_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = goldAccent.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, goldAccent),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = null,
                                    tint = goldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Mode Multi Layar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = goldAccent
                            )
                            Text(
                                text = "Pilih tata letak multi layar siaran",
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_multi_screen_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options list
                MultiScreenMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) goldAccent.copy(alpha = 0.18f) else cardBg
                        ),
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 0.8.dp,
                            if (isSelected) goldAccent else goldSoft.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                onModeSelected(mode)
                                onDismiss()
                            }
                            .testTag("multi_screen_option_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Visual Layout Preview Icon / Box
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF0F101A) else Color(0xFFE5E7EB))
                                    .border(1.dp, if (isSelected) goldAccent else goldSoft, RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when (mode) {
                                    MultiScreenMode.SINGLE -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(28.dp)
                                                .background(goldAccent.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                        )
                                    }
                                    MultiScreenMode.DUAL -> {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .background(goldAccent.copy(alpha = 0.8f), RoundedCornerShape(2.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("1", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .background(goldAccent.copy(alpha = 0.6f), RoundedCornerShape(2.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("2", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                        }
                                    }
                                    MultiScreenMode.QUAD -> {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .background(goldAccent.copy(alpha = 0.85f), RoundedCornerShape(2.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("1", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .background(goldAccent.copy(alpha = 0.75f), RoundedCornerShape(2.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("2", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                }
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .background(goldAccent.copy(alpha = 0.65f), RoundedCornerShape(2.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("3", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .background(goldAccent.copy(alpha = 0.55f), RoundedCornerShape(2.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("4", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) goldAccent else textColor
                                )
                                Text(
                                    text = mode.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onModeSelected(mode)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = goldAccent,
                                    unselectedColor = textSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
