package com.automexa.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.automexa.app.data.auth.AuthManager
import com.automexa.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authManager = remember { AuthManager(context) }

    var isSignInTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var showOtpDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSecurityLockoutDialog by remember { mutableStateOf(false) }

    // Check if account already locked from previous attempts
    LaunchedEffect(Unit) {
        if (authManager.isLocked) {
            showSecurityLockoutDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Branding Icon Box
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome to Automexa",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isSignInTab) "Sign in to activate your autonomous engine" else "Create an account to start automating jobs",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Tab Switcher (Linen / Card style matching code.html)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(CircleShape)
                    .background(PillInactive)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(if (isSignInTab) AccentDark else Color.Transparent)
                        .clickable {
                            isSignInTab = true
                            errorMessage = null
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSignInTab) Color.White else TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(if (!isSignInTab) AccentDark else Color.Transparent)
                        .clickable {
                            isSignInTab = false
                            errorMessage = null
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (!isSignInTab) Color.White else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Card Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x0A000000))
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardBackground)
                    .padding(22.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (!isSignInTab) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", fontSize = 13.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontSize = 13.sp) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!isSignInTab) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password", fontSize = 13.sp) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (isSignInTab) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.clickable {
                                    showResetDialog = true
                                }
                            )
                        }
                    }

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = err,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }

                    successMessage?.let { succ ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = succ,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Submit Button
                    Button(
                        onClick = {
                            errorMessage = null
                            successMessage = null

                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@Button
                            }

                            if (!isSignInTab && password != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@Button
                            }

                            isLoading = true
                            coroutineScope.launch {
                                if (isSignInTab) {
                                    val res = authManager.login(email, password)
                                    isLoading = false
                                    if (res.success) {
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = res.message
                                        if (res.isLocked) {
                                            showSecurityLockoutDialog = true
                                        }
                                    }
                                } else {
                                    val res = authManager.signup(fullName, email, password)
                                    isLoading = false
                                    if (res.success) {
                                        successMessage = res.message
                                        showOtpDialog = true
                                    } else {
                                        errorMessage = res.message
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (isSignInTab) "Sign In" else "Create Account",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // 3-Attempt Security Lockout Dialog
        if (showSecurityLockoutDialog) {
            Dialog(onDismissRequest = { /* Force user action */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(CardBackground)
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Security Shield Activated",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "More than 3 incorrect password attempts were detected. To protect your account from unauthorized access, password login is locked. Please verify via OTP to reset your password.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                showSecurityLockoutDialog = false
                                showResetDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentDark, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Verify with OTP & Reset", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // OTP Verification Dialog with 60s Resend Timer
        if (showOtpDialog) {
            OtpVerificationDialog(
                email = email,
                authManager = authManager,
                onDismiss = { showOtpDialog = false },
                onVerified = {
                    showOtpDialog = false
                    onAuthSuccess()
                }
            )
        }

        // Reset Password Dialog
        if (showResetDialog) {
            ResetPasswordDialog(
                defaultEmail = email,
                authManager = authManager,
                onDismiss = { showResetDialog = false },
                onSuccess = {
                    showResetDialog = false
                    successMessage = "Password successfully reset! Please sign in."
                }
            )
        }
    }
}

@Composable
fun OtpVerificationDialog(
    email: String,
    authManager: AuthManager,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var timerSeconds by remember { mutableIntStateOf(60) }
    var isVerifying by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // 60-Second Countdown Timer
    LaunchedEffect(key1 = timerSeconds) {
        if (timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardBackground)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Verify Email",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter the 6-digit verification code sent to $email",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    label = { Text("6-Digit OTP") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMsg?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, fontSize = 11.sp, color = Color(0xFFD32F2F))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Resend OTP with 60s Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timerSeconds > 0) {
                        Text(
                            text = "Resend in 00:${if (timerSeconds < 10) "0$timerSeconds" else timerSeconds}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = "Resend OTP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentDark,
                            modifier = Modifier.clickable {
                                isResending = true
                                coroutineScope.launch {
                                    authManager.sendOtp(email, "Verification")
                                    isResending = false
                                    timerSeconds = 60
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isVerifying = true
                        errorMsg = null
                        coroutineScope.launch {
                            val res = authManager.verifyOtp(email, otp)
                            isVerifying = false
                            if (res.success) {
                                onVerified()
                            } else {
                                errorMsg = res.message
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentDark, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otp.length == 6 && !isVerifying
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Verify & Continue", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ResetPasswordDialog(
    defaultEmail: String,
    authManager: AuthManager,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf(defaultEmail) }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var timerSeconds by remember { mutableIntStateOf(0) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isResetting by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = timerSeconds) {
        if (timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardBackground)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reset Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (timerSeconds > 0) {
                        Text("Resend code in ${timerSeconds}s", fontSize = 11.sp, color = TextSecondary)
                    } else {
                        Text(
                            text = "Send OTP to Email",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentDark,
                            modifier = Modifier.clickable {
                                if (email.isNotBlank()) {
                                    isSendingOtp = true
                                    coroutineScope.launch {
                                        authManager.sendOtp(email, "Password Reset")
                                        isSendingOtp = false
                                        timerSeconds = 60
                                        statusMsg = "OTP sent! Check your inbox."
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("6-Digit OTP") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                statusMsg?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = msg, fontSize = 11.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isResetting = true
                        coroutineScope.launch {
                            val res = authManager.resetPassword(email, newPassword, otp)
                            isResetting = false
                            if (res.success) {
                                onSuccess()
                            } else {
                                statusMsg = res.message
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentDark, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotBlank() && otp.isNotBlank() && newPassword.isNotBlank() && !isResetting
                ) {
                    if (isResetting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Save New Password & Unlock", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

