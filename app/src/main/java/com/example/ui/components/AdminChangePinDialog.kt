package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SecurityPinManager

@Composable
fun AdminChangePinDialog(
    isDark: Boolean = isSystemInDarkTheme(),
    onDismiss: () -> Unit,
    onPinChanged: () -> Unit
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("") }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val goldColor = if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
    val dialogBgColor = if (isDark) Color(0xFF161724) else Color(0xFFFFFFFF)
    val textColor = if (isDark) Color.White else Color(0xFF1E2130)

    fun handleSave() {
        errorMessage = null

        // 1. Validasi Akun Admin (admin:admin1234)
        if (!SecurityPinManager.verifyAdminCredentials(username, password)) {
            errorMessage = "Autentikasi gagal! Username atau Password Admin salah."
            return
        }

        // 2. Validasi PIN Lama
        if (!SecurityPinManager.verifyPin(context, oldPin.trim())) {
            errorMessage = "PIN Lama yang dimasukkan tidak sesuai!"
            return
        }

        // 3. Validasi PIN Baru
        val cleanNewPin = newPin.trim()
        if (cleanNewPin.length != 4 || !cleanNewPin.all { it.isDigit() }) {
            errorMessage = "PIN Baru harus tepat 4 digit angka!"
            return
        }

        // 4. Validasi Konfirmasi PIN
        if (cleanNewPin != confirmNewPin.trim()) {
            errorMessage = "Konfirmasi PIN Baru tidak cocok!"
            return
        }

        // 5. Simpan PIN Baru
        val saved = SecurityPinManager.setPin(context, cleanNewPin)
        if (saved) {
            Toast.makeText(context, "Kode PIN berhasil diubah!", Toast.LENGTH_SHORT).show()
            onPinChanged()
            onDismiss()
        } else {
            errorMessage = "Terjadi kesalahan saat menyimpan PIN."
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
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
                .widthIn(min = 320.dp, max = 400.dp)
                .padding(20.dp)
                .testTag("admin_change_pin_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = goldColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, goldColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = goldColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "KHUSUS ADMIN",
                                color = goldColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("admin_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = if (isDark) Color.LightGray else Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
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
                        imageVector = Icons.Default.Key,
                        contentDescription = "Ubah PIN",
                        tint = goldColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Ubah Kode PIN Aplikasi",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Login sebagai admin untuk mengganti kode PIN filter usia.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = if (isDark) Color(0xFFB0B3C7) else Color(0xFF5F6368),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Error Banner
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFD32F2F).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Section 1: Kredensial Admin
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username Admin", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = goldColor, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("admin_username_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldColor,
                        unfocusedBorderColor = if (isDark) Color(0xFF373950) else Color(0xFFD0D4E0)
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Admin", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = goldColor, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = if (isDark) Color.LightGray else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .testTag("admin_password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldColor,
                        unfocusedBorderColor = if (isDark) Color(0xFF373950) else Color(0xFFD0D4E0)
                    )
                )

                // Divider Label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isDark) Color(0xFF2E3146) else Color(0xFFE0E3EC))
                    )
                    Text(
                        text = " PIN Keamanan Baru ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = goldColor
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isDark) Color(0xFF2E3146) else Color(0xFFE0E3EC))
                    )
                }

                // PIN Lama
                OutlinedTextField(
                    value = oldPin,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) oldPin = it },
                    label = { Text("PIN Lama (4 Angka)", fontSize = 12.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("admin_old_pin_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldColor,
                        unfocusedBorderColor = if (isDark) Color(0xFF373950) else Color(0xFFD0D4E0)
                    )
                )

                // PIN Baru
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) newPin = it },
                    label = { Text("PIN Baru (4 Angka)", fontSize = 12.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("admin_new_pin_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldColor,
                        unfocusedBorderColor = if (isDark) Color(0xFF373950) else Color(0xFFD0D4E0)
                    )
                )

                // Konfirmasi PIN Baru
                OutlinedTextField(
                    value = confirmNewPin,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) confirmNewPin = it },
                    label = { Text("Konfirmasi PIN Baru (4 Angka)", fontSize = 12.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("admin_confirm_pin_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldColor,
                        unfocusedBorderColor = if (isDark) Color(0xFF373950) else Color(0xFFD0D4E0)
                    )
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF3F4259) else Color(0xFFCCD0DC)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("admin_cancel_button")
                    ) {
                        Text(
                            text = "Batal",
                            color = if (isDark) Color.LightGray else Color(0xFF555968),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = { handleSave() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = goldColor,
                            contentColor = if (isDark) Color.Black else Color.White
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("admin_save_pin_button")
                    ) {
                        Text(
                            text = "Simpan PIN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
