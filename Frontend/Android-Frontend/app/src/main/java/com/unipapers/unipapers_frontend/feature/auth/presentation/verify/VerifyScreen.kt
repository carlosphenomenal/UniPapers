package com.unipapers.unipapers_frontend.feature.auth.presentation.verify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.core.ui.theme.UniPapersTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VerifyScreen(
    onVerifySuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: VerifyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is VerifyViewModel.UiEvent.Success -> {
                    onVerifySuccess()
                }
                is VerifyViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        VerifyContent(
            modifier = Modifier.padding(padding),
            state = state,
            onCodeChange = viewModel::onCodeChange,
            onResendCode = viewModel::onResendCode,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun VerifyContent(
    modifier: Modifier = Modifier,
    state: VerifyState,
    onCodeChange: (String) -> Unit,
    onResendCode: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = SimpleBlue,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Verify your email",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        
        Text(
            text = buildAnnotatedString {
                append("We've sent a 6-digit verification code to\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append(state.email.ifEmpty { "your email" })
                }
            },
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 6 Digit Input Field
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // BasicTextField to handle input and pasting
            BasicTextField(
                value = state.code,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        onCodeChange(it)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .matchParentSize()
                    .alpha(0f), 
                decorationBox = { it() }
            )

            // Visual representation of the 6 digits
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    val char = when {
                        index >= state.code.length -> ""
                        else -> state.code[index].toString()
                    }
                    val isFocused = index == state.code.length

                    DigitBox(
                        char = char,
                        isFocused = isFocused
                    )
                }
            }
        }

        if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = SimpleBlue)
        } else {
            // Resend Code Section
            if (state.canResend) {
                TextButton(onClick = onResendCode) {
                    Text(
                        text = "Resend code",
                        color = SimpleBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "Resend code in ${state.resendTimer}s",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateBack) {
            Text(
                text = "Back to registration",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DigitBox(
    char: String,
    isFocused: Boolean
) {
    Box(
        modifier = Modifier
            .size(48.dp, 56.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) SimpleBlue else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyScreenPreview() {
    UniPapersTheme {
        VerifyContent(
            state = VerifyState(email = "carlos@student.mak.ac.ug"),
            onCodeChange = {},
            onResendCode = {},
            onNavigateBack = {}
        )
    }
}
