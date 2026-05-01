package com.unipapers.unipapers_frontend.feature.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.core.ui.theme.UniPapersTheme

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    RegisterContent(
        state = state,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onEmailChange = viewModel::onEmailChange,
        onStudentNumberChange = viewModel::onStudentNumberChange,
        onProgrammeChange = viewModel::onProgrammeChange,
        onYearOfStudyChange = viewModel::onYearOfStudyChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSignUp = viewModel::onSignUp,
        onNavigateToLogin = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    state: RegisterState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onStudentNumberChange: (String) -> Unit,
    onProgrammeChange: (String) -> Unit,
    onYearOfStudyChange: (Int) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSignUp: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
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
            text = "Create your account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        Text(
            text = "Join thousands of students on UniPapers",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // First Name and Last Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("First name", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                RegisterTextField(
                    value = state.firstName,
                    onValueChange = onFirstNameChange,
                    placeholder = "First name",
                    leadingIcon = Icons.Default.Person
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Last name", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                RegisterTextField(
                    value = state.lastName,
                    onValueChange = onLastNameChange,
                    placeholder = "Last name"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("University email", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            RegisterTextField(
                value = state.email,
                onValueChange = onEmailChange,
                placeholder = "firstname.lastname@mak.ac.ug",
                leadingIcon = Icons.Default.Email
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Number
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Student number", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            RegisterTextField(
                value = state.studentNumber,
                onValueChange = onStudentNumberChange,
                placeholder = "2100703316",
                leadingIcon = Icons.Default.Tag
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Programme
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Programme", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            RegisterTextField(
                value = state.programme,
                onValueChange = onProgrammeChange,
                placeholder = "Select your programme",
                leadingIcon = Icons.Default.School,
                trailingIcon = Icons.Default.KeyboardArrowDown,
                readOnly = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Year of Study
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Year of study", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { year ->
                    YearButton(
                        year = year,
                        isSelected = state.yearOfStudy == year,
                        onClick = { onYearOfStudyChange(year) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Password", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            RegisterTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                placeholder = "At least 6 characters",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Create Account Button
        Button(
            onClick = onSignUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BA2C1)
            )
        ) {
            Text(
                text = "Create account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Already have an account? Sign in
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("Already have an account? ")
            }
            withStyle(style = SpanStyle(color = SimpleBlue, fontWeight = FontWeight.Bold)) {
                append("Sign in")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text(text = annotatedString)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(imageVector = it, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        },
        trailingIcon = {
            if (isPassword && onTogglePasswordVisibility != null) {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (trailingIcon != null) {
                Icon(imageVector = trailingIcon, contentDescription = null, tint = Color.Gray)
            }
        },
        shape = RoundedCornerShape(25.dp),
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SimpleBlue,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        readOnly = readOnly
    )
}

@Composable
fun YearButton(
    year: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = if (isSelected) SimpleBlue else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
        contentColor = if (isSelected) Color.White else Color.Black
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = year.toString(), fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    UniPapersTheme {
        RegisterContent(
            state = RegisterState(),
            onFirstNameChange = {},
            onLastNameChange = {},
            onEmailChange = {},
            onStudentNumberChange = {},
            onProgrammeChange = {},
            onYearOfStudyChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSignUp = {},
            onNavigateToLogin = {}
        )
    }
}
