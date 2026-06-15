package com.example.smartbudgetsa10.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartbudgetsa10.util.BudgetManager
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
    val badges = remember(context) { BudgetManager.getEarnedBadges(context) }
    var selectedCurrency by remember { mutableStateOf(BudgetManager.getCurrency(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp)
            .verticalScroll(rememberScrollState()),
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
            }
        }
        
        Spacer(modifier = Modifier.height(height = 16.dp))

        // Own Feature 1: Currency Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "App Settings", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Currency Symbol: ")
                    val currencies = listOf("R", "$", "€", "£")
                    currencies.forEach { curr ->
                        FilterChip(
                            selected = selectedCurrency == curr,
                            onClick = {
                                selectedCurrency = curr
                                BudgetManager.setCurrency(context, curr)
                            },
                            label = { Text(curr) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(height = 32.dp))
        
        Text(
            text = "Achievements & Badges",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        
        if (badges.isEmpty()) {
            Text(text = "Start logging expenses to earn badges!", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(badges) { badge ->
                    BadgeItem(badge = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: com.example.smartbudgetsa10.model.Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                painter = painterResource(id = badge.iconResId),
                contentDescription = badge.name,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = badge.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
