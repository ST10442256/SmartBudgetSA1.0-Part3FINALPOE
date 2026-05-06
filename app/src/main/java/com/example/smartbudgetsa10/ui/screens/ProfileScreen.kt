package com.example.smartbudgetsa10.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.smartbudgetsa10.util.UserManager

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var isProfileUnlocked by remember { mutableStateOf(value = false) }
    var passwordInput by remember { mutableStateOf(value = "") }
    var errorMessage by remember { mutableStateOf(value = "") }

    if (!isProfileUnlocked) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Enter Password to Access Profile",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(height = 24.dp))
            Button(
                onClick = {
                    if (UserManager.verifyPassword(context, passwordInput)) {
                        isProfileUnlocked = true
                        errorMessage = ""
                    } else {
                        errorMessage = "Incorrect password"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Verify")
            }
        }
    } else {
        ProfileContent()
    }
}

@Composable
fun ProfileContent() {
    val context = LocalContext.current
    val username = UserManager.getUsername(context) ?: "Guest"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "User Profile",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(height = 32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(all = 16.dp),
            ) {
                Text(text = "Username: $username", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(text = "Account Type: Premium", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(text = "Member Since: Feb 2026", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
