package com.example.smartbudgetsa10.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.smartbudgetsa10.R
import com.example.smartbudgetsa10.util.BudgetManager
import java.util.Locale

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showBudgetDialog by remember { mutableStateOf(false) }
    var expenses by remember { mutableStateOf(BudgetManager.getExpenses(context)) }
    var monthlyBudget by remember { mutableStateOf(BudgetManager.getMonthlyBudget(context)) }
    val currency = BudgetManager.getCurrency(context)
    
    // Own Feature 2: Search Expenses
    var searchQuery by remember { mutableStateOf("") }
    val filteredExpenses = remember(expenses, searchQuery) {
        if (searchQuery.isEmpty()) {
            expenses
        } else {
            expenses.filter { 
                it.description.contains(searchQuery, ignoreCase = true) || 
                it.category.contains(searchQuery, ignoreCase = true) 
            }
        }
    }

    val totalExpenses = expenses.sumOf { it.amount }
    val remainingBudget = monthlyBudget - totalExpenses
    val safeToSpend = if (remainingBudget > 0.0) remainingBudget else 0.0
    val progress = if (monthlyBudget > 0.0) (totalExpenses / monthlyBudget).toFloat() else 0f

    if (showBudgetDialog) {
        AdjustBudgetDialog(
            currentBudget = monthlyBudget,
            onDismiss = { showBudgetDialog = false },
            onSave = { newBudget, resetExpenses ->
                BudgetManager.setMonthlyBudget(context, newBudget)
                if (resetExpenses) {
                    BudgetManager.clearExpenses(context)
                    expenses = emptyList()
                }
                monthlyBudget = newBudget
                showBudgetDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            BalanceCard(safeToSpend, totalExpenses, monthlyBudget, currency, onAdjustBudget = { showBudgetDialog = true })
        }
        item {
            BudgetProgressSection(progress, remainingBudget, currency)
        }
        if (totalExpenses > monthlyBudget) {
            item {
                AlertSection()
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(id = R.string.search_expenses)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(painterResource(id = R.drawable.ic_dashboard), contentDescription = null) },
                singleLine = true
            )
        }

        item {
            Text(
                text = if (searchQuery.isEmpty()) stringResource(id = R.string.recent_expenses) else "Search Results",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        items(filteredExpenses.reversed().take(if (searchQuery.isEmpty()) 5 else 20)) { expense ->
            ExpenseItem(name = expense.description, amount = "$currency${String.format(Locale.getDefault(), "%.2f", expense.amount)}")
        }
    }
}

@Composable
fun AdjustBudgetDialog(
    currentBudget: Double,
    onDismiss: () -> Unit,
    onSave: (Double, Boolean) -> Unit
) {
    var budgetText by remember { mutableStateOf(currentBudget.toString()) }
    var resetExpenses by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.adjust_budget)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.enter_budget))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { resetExpenses = !resetExpenses }
                ) {
                    Checkbox(
                        checked = resetExpenses,
                        onCheckedChange = { resetExpenses = it }
                    )
                    Text(
                        text = stringResource(id = R.string.reset_expenses),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newBudget = budgetText.toDoubleOrNull() ?: 0.0
                    onSave(newBudget, resetExpenses)
                }
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

@Composable
fun BalanceCard(safeToSpend: Double, totalBalance: Double, monthlyBudget: Double, currency: String, onAdjustBudget: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.safe_to_spend),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "$currency${String.format(Locale.getDefault(), "%.2f", safeToSpend)}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(text = stringResource(id = R.string.total_balance), style = MaterialTheme.typography.labelSmall)
                    Text(text = "$currency${String.format(Locale.getDefault(), "%.2f", totalBalance)}", style = MaterialTheme.typography.bodyLarge)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(id = R.string.monthly_budget), style = MaterialTheme.typography.labelSmall)
                    Text(text = "$currency${String.format(Locale.getDefault(), "%.2f", monthlyBudget)}", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAdjustBudget,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(id = R.string.adjust_budget))
            }
        }
    }
}

@Composable
fun BudgetProgressSection(progress: Float, remaining: Double, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monthly Budget Progress",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (progress > 0.9f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.budget_remaining, "$currency${String.format(Locale.getDefault(), "%.2f", remaining)}"),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(alignment = Alignment.End),
            )
        }
    }
}

@Composable
fun AlertSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.overspending_alert, "Entertainment"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ExpenseItem(name: String, amount: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
            Text(text = amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}
