package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SecurityPinManager

@Composable
fun DashboardPinDialog(
    isDark: Boolean = isSystemInDarkTheme(),
    onDismiss: () -> Unit = {},
    onPinCorrect: () -> Unit,
    onOpenAdminChangePin: () -> Unit = {}
) {
    val context = LocalContext.current
    var enteredPin by remember { mutableStateOf("") }

    val goldColor = if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
    val dialogBgColor = if (isDark) Color(0xFF161724) else Color(0xFFFFFFFF)
    val keypadBgColor = if (isDark) Color(0xFF222436) else Color(0xFFF3F5FA)
    val keyTextColor = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E2130)

    fun handleIncorrectPinAndExit() {
        Toast.makeText(context, "PIN Salah! Akses ditolak, aplikasi otomatis ditutup.", Toast.LENGTH_LONG).show()
        (context as? Activity)?.finishAffinity()
    }

    fun handleDigit(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin

            if (newPin.length == 4) {
                if (SecurityPinManager.verifyPin(context, newPin)) {
                    onPinCorrect()
                } else {
                    handleIncorrectPinAndExit()
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
        }
    }

    fun handleClear() {
        enteredPin = ""
    }

    Dialog(
        onDismissRequest = {
            (context as? Activity)?.finish()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = dialogBgColor,
            tonalElevation = 12.dp,
            shadowElevation = 16.dp,
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    if (isDark) listOf(Color(0xFFFFD700), Color(0x33D4AF37))
                    else listOf(Color(0xFFB8860B), Color(0x33B8860B))
                )
            ),
            modifier = Modifier
                .widthIn(min = 310.dp, max = 370.dp)
                .padding(20.dp)
                .testTag("dashboard_pin_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row: Age 17+ Badge & Exit App Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD32F2F).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "17+ DEWASA",
                                color = if (isDark) Color(0xFFFF5252) else Color(0xFFD32F2F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            (context as? Activity)?.finish()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("dashboard_pin_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Aplikasi",
                            tint = if (isDark) Color.LightGray else Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lock Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (isDark) listOf(Color(0xFF3B2E0A), Color(0xFF1E1705))
                                else listOf(Color(0xFFFFF7DF), Color(0xFFFFE8B2))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Kunci PIN Usia",
                        tint = goldColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title - Clean, no secret code shown
                Text(
                    text = "Verifikasi Usia Pengguna",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1E2130),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description - No mention of 1234
                Text(
                    text = "Akses dashboard dibatasi untuk usia 17 tahun ke atas. Masukkan kode PIN keamanan untuk melanjutkan.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                    color = if (isDark) Color(0xFFB0B3C7) else Color(0xFF5F6368),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4-Digit PIN Boxes / Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        val isCurrentTarget = i == enteredPin.length

                        val boxBorderColor = when {
                            isCurrentTarget -> goldColor
                            isFilled -> goldColor.copy(alpha = 0.8f)
                            else -> if (isDark) Color(0xFF373950) else Color(0xFFD0D4E0)
                        }

                        val boxBgColor = when {
                            isFilled -> goldColor.copy(alpha = 0.15f)
                            else -> if (isDark) Color(0xFF1C1E2D) else Color(0xFFF7F8FC)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = boxBgColor,
                            border = BorderStroke(1.5.dp, boxBorderColor),
                            modifier = Modifier
                                .size(46.dp)
                                .testTag("dashboard_pin_box_$i")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFilled) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(goldColor)
                                    )
                                } else if (isCurrentTarget) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(goldColor.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Numeric Keypad Grid (3 columns: 1-9, C, 0, DEL)
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "DEL")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    keypadRows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { keyLabel ->
                                Surface(
                                    onClick = {
                                        when (keyLabel) {
                                            "C" -> handleClear()
                                            "DEL" -> handleBackspace()
                                            else -> handleDigit(keyLabel)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (keyLabel) {
                                        "C", "DEL" -> if (isDark) Color(0xFF1E2030) else Color(0xFFE8EAF0)
                                        else -> keypadBgColor
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (isDark) Color(0xFF2E3146) else Color(0xFFDDE1EB)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("dashboard_pin_key_$keyLabel")
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        when (keyLabel) {
                                            "DEL" -> {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                    contentDescription = "Hapus",
                                                    tint = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            "C" -> {
                                                Text(
                                                    text = "C",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFFFA726) else Color(0xFFE65100)
                                                )
                                            }
                                            else -> {
                                                Text(
                                                    text = keyLabel,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = keyTextColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Keluar Aplikasi & Masuk Dashboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            (context as? Activity)?.finish()
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF3F4259) else Color(0xFFCCD0DC)),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("dashboard_pin_exit_button")
                    ) {
                        Text(
                            text = "Keluar",
                            color = if (isDark) Color.LightGray else Color(0xFF555968),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            if (enteredPin.length == 4) {
                                if (SecurityPinManager.verifyPin(context, enteredPin)) {
                                    onPinCorrect()
                                } else {
                                    handleIncorrectPinAndExit()
                                }
                            } else {
                                Toast.makeText(context, "Masukkan 4 digit PIN!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = goldColor,
                            contentColor = if (isDark) Color.Black else Color.White
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(42.dp)
                            .testTag("dashboard_pin_submit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Masuk",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Special Button for Admin to Change PIN
                Surface(
                    onClick = onOpenAdminChangePin,
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent,
                    modifier = Modifier.testTag("btn_open_admin_change_pin")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = goldColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Admin: Ubah Kode PIN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = goldColor
                        )
                    }
                }
            }
        }
    }
}
