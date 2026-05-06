package com.example.smartbudgetsa10.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.remember
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.example.smartbudgetsa10.util.BudgetManager

@Composable
fun ReportsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val expenses = remember(context) { BudgetManager.getExpenses(context) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Financial Reports",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Section(title = "Spending Trends") {
            SpendingLineChart(expenses)
        }

        Section(title = "Category Distribution") {
            CategoryPieChart(expenses)
        }
    }
}

@Composable
fun SpendingLineChart(expenses: List<com.example.smartbudgetsa10.model.Expense>) {
    val spendingData = expenses.sortedBy { it.date }.takeLast(10).map { it.amount.toFloat() }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.DarkGray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (spendingData.isEmpty()) {
            Text("No data available", color = Color.White)
        } else {
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val width = size.width
                val height = size.height
                val maxAmount = spendingData.maxOrNull() ?: 1f
                val stepX = width / (spendingData.size - 1).coerceAtLeast(1)
                
                val path = Path().apply {
                    spendingData.forEachIndexed { index, amount ->
                        val x = index * stepX
                        val y = height - (amount / maxAmount * height)
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }
                drawPath(path, color = Color(0xFFFF9800), style = Stroke(width = 4f))
            }
        }
    }
}

@Composable
fun CategoryPieChart(expenses: List<com.example.smartbudgetsa10.model.Expense>) {
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { exp -> exp.amount }.toFloat() }
    
    val total = categoryTotals.values.sum()
    val colors = listOf(Color(0xFFFF9800), Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFF9C27B0))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.DarkGray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (categoryTotals.isEmpty()) {
            Text("No data available", color = Color.White)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    var startAngle = 0f
                    categoryTotals.values.forEachIndexed { index, amount ->
                        val sweepAngle = (amount / total) * 360f
                        drawArc(
                            color = colors[index % colors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                        startAngle += sweepAngle
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    categoryTotals.keys.forEachIndexed { index, category ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).background(colors[index % colors.size]))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = category, color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}
