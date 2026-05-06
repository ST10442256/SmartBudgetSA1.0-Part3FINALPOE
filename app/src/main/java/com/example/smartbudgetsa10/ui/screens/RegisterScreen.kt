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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var confirmPassword by remember { mutableStateOf(value = "") }
    var errorMessage by remember { mutableStateOf(value = "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(height = 32.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(height = 16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(height = 32.dp))
        
        Button(
            onClick = {
                if ((password == confirmPassword) && username.isNotEmpty() && password.isNotEmpty()) {
                    UserManager.saveUser(context, username, password)
                    onRegisterSuccess()
                } else {
                    errorMessage = "Passwords do not match or fields are empty"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(all = 16.dp),
        ) {
            Text(text = "Register")
        }
        
        TextButton(onClick = onNavigateToLogin) {
            Text(text = "Already have an account? Login")
        }
    }
}
