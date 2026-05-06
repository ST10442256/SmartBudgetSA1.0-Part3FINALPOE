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
import androidx.compose.ui.unit.dp
import com.example.smartbudgetsa10.R

import androidx.compose.ui.platform.LocalContext
import com.example.smartbudgetsa10.util.BudgetManager

@Composable
fun CategoryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var categoryName by remember { mutableStateOf(value = "") }
    val categories = remember { 
        mutableStateListOf<String>().apply { 
            addAll(BudgetManager.getCategories(context)) 
        } 
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Manage Categories",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("New Category") },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        BudgetManager.saveCategory(context, categoryName)
                        if (!categories.contains(categoryName)) {
                            categories.add(categoryName)
                        }
                        categoryName = ""
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(name = category)
            }
        }
    }
}

@Composable
fun CategoryItem(name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
            // Edit/Delete buttons could go here
        }
    }
}

