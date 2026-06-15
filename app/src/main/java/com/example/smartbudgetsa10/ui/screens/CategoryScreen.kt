package com.example.smartbudgetsa10.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartbudgetsa10.R

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.example.smartbudgetsa10.model.CategoryGoal
import com.example.smartbudgetsa10.util.BudgetManager

@Composable
fun CategoryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var categoryName by remember { mutableStateOf(value = "") }
    var minGoal by remember { mutableStateOf(value = "") }
    var maxGoal by remember { mutableStateOf(value = "") }
    
    val categories = remember { 
        mutableStateListOf<String>().apply { 
            addAll(BudgetManager.getCategories(context)) 
        } 
    }
    val goals = remember {
        mutableStateMapOf<String, CategoryGoal>().apply {
            BudgetManager.getCategoryGoals(context).forEach { put(it.category, it) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.manage_goals),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minGoal,
                        onValueChange = { minGoal = it },
                        label = { Text(stringResource(id = R.string.min_goal)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = maxGoal,
                        onValueChange = { maxGoal = it },
                        label = { Text(stringResource(id = R.string.max_goal)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Button(
                    onClick = {
                        if (categoryName.isNotBlank()) {
                            BudgetManager.saveCategory(context, categoryName)
                            if (!categories.contains(categoryName)) {
                                categories.add(categoryName)
                            }
                            
                            val minVal = minGoal.toDoubleOrNull() ?: 0.0
                            val maxVal = maxGoal.toDoubleOrNull() ?: 0.0
                            val goal = CategoryGoal(categoryName, minVal, maxVal)
                            BudgetManager.saveCategoryGoal(context, goal)
                            goals[categoryName] = goal
                            
                            categoryName = ""
                            minGoal = ""
                            maxGoal = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(id = R.drawable.ic_add), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Category & Goals")
                }
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(name = category, goal = goals[category])
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, goal: CategoryGoal?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, style = MaterialTheme.typography.titleLarge)
            }
            if (goal != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Goal: R${goal.minAmount} - R${goal.maxAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = "No goals set",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                )
            }
        }
    }
}

