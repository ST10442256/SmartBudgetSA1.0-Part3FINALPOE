package com.example.smartbudgetsa10.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.example.smartbudgetsa10.model.CategoryGoal
import com.example.smartbudgetsa10.util.BudgetManager
import java.util.*

/**
 * The ReportsScreen displays financial analytics to the user.
 * It includes spending trends, category distribution, and a visual comparison 
 * of actual spending against the user's min/max goals.
 */
@Composable
fun ReportsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val expenses = remember(context) { BudgetManager.getExpenses(context) }
    val goals = remember(context) { BudgetManager.getCategoryGoals(context) }
    val currency = BudgetManager.getCurrency(context)
    
    var selectedPeriod by remember { mutableStateOf("Month") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Financial Reports",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Section(title = "Select Period") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Week", "Month", "Year", "All").forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        label = { Text(period) }
                    )
                }
            }
        }

        Section(title = "Category vs Goals ($selectedPeriod)") {
            val filteredExpenses = filterExpensesByPeriod(expenses, selectedPeriod)
            CategoryGoalsComparison(filteredExpenses, goals, currency)
        }

        Section(title = "Category Spending Bar Chart") {
            val filteredExpenses = filterExpensesByPeriod(expenses, selectedPeriod)
            CategoryBarChart(filteredExpenses, goals, currency)
        }

        Section(title = "Spending Trends") {
            SpendingLineChart(filterExpensesByPeriod(expenses, selectedPeriod))
        }

        Section(title = "Category Distribution") {
            CategoryPieChart(filterExpensesByPeriod(expenses, selectedPeriod))
        }
    }
}

/**
 * Filters the list of expenses based on a selected time period.
 * Uses the Java Calendar API to calculate the cutoff date.
 */
fun filterExpensesByPeriod(expenses: List<com.example.smartbudgetsa10.model.Expense>, period: String): List<com.example.smartbudgetsa10.model.Expense> {
    val calendar = Calendar.getInstance()
    when (period) {
        "Week" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
        "Month" -> calendar.add(Calendar.MONTH, -1)
        "Year" -> calendar.add(Calendar.YEAR, -1)
        else -> return expenses
    }
    return expenses.filter { it.date >= calendar.timeInMillis }
}

/**
 * A visual representation showing how well the user is staying within their goals.
 * Status colors:
 * - Yellow: Below Minimum (Under-spending)
 * - Red: Over Maximum (Over-spending)
 * - Green: On Target (Perfectly between min and max)
 */
@Composable
fun CategoryGoalsComparison(
    expenses: List<com.example.smartbudgetsa10.model.Expense>,
    goals: List<CategoryGoal>,
    currency: String
) {
    val categorySpending = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { exp -> exp.amount } }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (goals.isEmpty()) {
            Text("Set category goals in the Categories screen to see comparison.", style = MaterialTheme.typography.bodyMedium)
        }
        
        goals.forEach { goal ->
            val spent = categorySpending[goal.category] ?: 0.0
            val statusColor = when {
                spent < goal.minAmount -> Color.Yellow
                spent > goal.maxAmount -> Color.Red
                else -> Color.Green
            }
            val statusText = when {
                spent < goal.minAmount -> "Below Minimum"
                spent > goal.maxAmount -> "Over Maximum"
                else -> "On Target"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = goal.category, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(Color.Gray.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        val maxScale = (maxOf(spent, goal.maxAmount) * 1.2).toFloat()
                        val spentWidth = (spent / maxScale).toFloat().coerceIn(0f, 1f)
                        val minGoalPos = (goal.minAmount / maxScale).toFloat().coerceIn(0f, 1f)
                        val maxGoalPos = (goal.maxAmount / maxScale).toFloat().coerceIn(0f, 1f)

                        // Spent bar
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(spentWidth)
                                .background(statusColor.copy(alpha = 0.6f), shape = CircleShape)
                        )
                        
                        // Goal markers
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val minX = size.width * minGoalPos
                            val maxX = size.width * maxGoalPos
                            
                            drawLine(
                                color = Color.White,
                                start = androidx.compose.ui.geometry.Offset(minX, 0f),
                                end = androidx.compose.ui.geometry.Offset(minX, size.height),
                                strokeWidth = 4f
                            )
                            drawLine(
                                color = Color.White,
                                start = androidx.compose.ui.geometry.Offset(maxX, 0f),
                                end = androidx.compose.ui.geometry.Offset(maxX, size.height),
                                strokeWidth = 4f
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Spent: $currency$spent", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Goal: $currency${goal.minAmount} - $currency${goal.maxAmount}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.CircleShape

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
fun CategoryBarChart(
    expenses: List<com.example.smartbudgetsa10.model.Expense>,
    goals: List<CategoryGoal>,
    currency: String
) {
    val categorySpending = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { exp -> exp.amount }.toFloat() }
    
    val maxSpent = categorySpending.values.maxOrNull() ?: 1f
    val maxGoal = goals.map { it.maxAmount.toFloat() }.maxOrNull() ?: 1f
    val maxValue = maxOf(maxSpent, maxGoal) * 1.2f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.DarkGray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            categorySpending.forEach { (category, spent) ->
                val goal = goals.find { it.category == category }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Spent Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(spent / maxValue)
                                .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall)
                        )
                        
                        // Goal Markers
                        if (goal != null) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val minY = size.height - (goal.minAmount.toFloat() / maxValue * size.height)
                                val maxY = size.height - (goal.maxAmount.toFloat() / maxValue * size.height)
                                
                                drawLine(
                                    color = Color.Green,
                                    start = androidx.compose.ui.geometry.Offset(0f, minY),
                                    end = androidx.compose.ui.geometry.Offset(size.width, minY),
                                    strokeWidth = 2f
                                )
                                drawLine(
                                    color = Color.Red,
                                    start = androidx.compose.ui.geometry.Offset(0f, maxY),
                                    end = androidx.compose.ui.geometry.Offset(size.width, maxY),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}
