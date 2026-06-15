package com.example.smartbudgetsa10.util

import android.content.Context
import android.util.Log
import com.example.smartbudgetsa10.model.Badge
import com.example.smartbudgetsa10.model.CategoryGoal
import com.example.smartbudgetsa10.model.Expense
import org.json.JSONArray
import org.json.JSONObject

/**
 * BudgetManager is a singleton utility that handles all data persistence for the app.
 * It uses SharedPreferences to store expenses, goals, and user preferences.
 * Data is serialized to JSON for complex objects like Expenses and CategoryGoals.
 */
object BudgetManager {
    private const val TAG = "BudgetManager"
    private const val PREFS_NAME = "budget_prefs"
    private const val KEY_EXPENSES = "expenses"
    private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    private const val KEY_CATEGORIES = "categories"
    private const val KEY_GOALS = "category_goals"
    private const val KEY_BADGES = "earned_badges"
    private const val KEY_CURRENCY = "currency_symbol"
    private const val DEFAULT_BUDGET = 2000.0
    private val DEFAULT_CATEGORIES = listOf("Groceries", "Transport", "Entertainment", "Utilities", "Savings")

    fun getCurrency(context: Context): String {
        return getPrefs(context).getString(KEY_CURRENCY, "R") ?: "R"
    }

    fun setCurrency(context: Context, currency: String) {
        Log.d(TAG, "Currency set to: $currency")
        getPrefs(context).edit().putString(KEY_CURRENCY, currency).apply()
    }

    private fun getPrefs(context: Context) = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCategory(context: Context, category: String) {
        Log.d(TAG, "Saving category: $category")
        val categories = getCategories(context).toMutableList()
        if (!categories.contains(category)) {
            categories.add(category)
            getPrefs(context).edit().putString(KEY_CATEGORIES, categories.joinToString(",")).apply()
        }
    }

    fun getCategories(context: Context): List<String> {
        val categoriesString = getPrefs(context).getString(KEY_CATEGORIES, null)
        return categoriesString?.split(",") ?: DEFAULT_CATEGORIES
    }

    fun saveCategoryGoal(context: Context, goal: CategoryGoal) {
        Log.d(TAG, "Saving goal for category ${goal.category}: Min=${goal.minAmount}, Max=${goal.maxAmount}")
        val goals = getCategoryGoals(context).toMutableList()
        goals.removeAll { it.category == goal.category }
        goals.add(goal)
        val jsonArray = JSONArray()
        goals.forEach {
            val jsonObject = JSONObject()
            jsonObject.put("category", it.category)
            jsonObject.put("minAmount", it.minAmount)
            jsonObject.put("maxAmount", it.maxAmount)
            jsonArray.put(jsonObject)
        }
        getPrefs(context).edit().putString(KEY_GOALS, jsonArray.toString()).apply()
    }

    fun getCategoryGoals(context: Context): List<CategoryGoal> {
        val jsonString = getPrefs(context).getString(KEY_GOALS, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val goals = mutableListOf<CategoryGoal>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            goals.add(
                CategoryGoal(
                    category = jsonObject.getString("category"),
                    minAmount = jsonObject.getDouble("minAmount"),
                    maxAmount = jsonObject.getDouble("maxAmount")
                )
            )
        }
        return goals
    }

    fun saveExpense(context: Context, expense: Expense) {
        Log.d(TAG, "Saving expense: ${expense.description}, Amount: ${expense.amount}")
        val expenses = getExpenses(context).toMutableList()
        expenses.add(expense)
        val jsonArray = JSONArray()
        expenses.forEach {
            val jsonObject = JSONObject()
            jsonObject.put("id", it.id)
            jsonObject.put("amount", it.amount)
            jsonObject.put("description", it.description)
            jsonObject.put("category", it.category)
            jsonObject.put("date", it.date)
            jsonArray.put(jsonObject)
        }
        getPrefs(context).edit().putString(KEY_EXPENSES, jsonArray.toString()).apply()
        checkForBadges(context)
    }

    fun getExpenses(context: Context): List<Expense> {
        val jsonString = getPrefs(context).getString(KEY_EXPENSES, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val expenses = mutableListOf<Expense>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            expenses.add(
                Expense(
                    id = jsonObject.getString("id"),
                    amount = jsonObject.getDouble("amount"),
                    description = jsonObject.getString("description"),
                    category = jsonObject.getString("category"),
                    date = jsonObject.getLong("date")
                )
            )
        }
        return expenses
    }

    fun getMonthlyBudget(context: Context): Double {
        return getPrefs(context).getFloat(KEY_MONTHLY_BUDGET, DEFAULT_BUDGET.toFloat()).toDouble()
    }

    fun setMonthlyBudget(context: Context, budget: Double) {
        getPrefs(context).edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
    }

    fun getTotalExpenses(context: Context): Double {
        return getExpenses(context).sumOf { it.amount }
    }

    fun getRemainingBudget(context: Context): Double {
        return getMonthlyBudget(context) - getTotalExpenses(context)
    }

    fun clearExpenses(context: Context) {
        getPrefs(context).edit().remove(KEY_EXPENSES).apply()
    }

    fun getEarnedBadges(context: Context): List<Badge> {
        val jsonString = getPrefs(context).getString(KEY_BADGES, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val badges = mutableListOf<Badge>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            badges.add(
                Badge(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    description = jsonObject.getString("description"),
                    iconResId = jsonObject.getInt("iconResId"),
                    dateEarned = jsonObject.getLong("dateEarned")
                )
            )
        }
        return badges
    }

    private fun saveBadge(context: Context, badge: Badge) {
        val badges = getEarnedBadges(context).toMutableList()
        if (badges.none { it.id == badge.id }) {
            badges.add(badge)
            val jsonArray = JSONArray()
            badges.forEach {
                val jsonObject = JSONObject()
                jsonObject.put("id", it.id)
                jsonObject.put("name", it.name)
                jsonObject.put("description", it.description)
                jsonObject.put("iconResId", it.iconResId)
                jsonObject.put("dateEarned", it.dateEarned)
                jsonArray.put(jsonObject)
            }
            getPrefs(context).edit().putString(KEY_BADGES, jsonArray.toString()).apply()
        }
    }

    private fun checkForBadges(context: Context) {
        val expenses = getExpenses(context)
        val totalAmount = expenses.sumOf { it.amount }
        val budget = getMonthlyBudget(context)

        // Achievement: First Expense
        if (expenses.size == 1) {
            saveBadge(context, Badge("first_expense", "Budget Beginner", "Logged your first expense!", com.example.smartbudgetsa10.R.drawable.ic_profile))
        }

        // Achievement: 5 Expenses
        if (expenses.size == 5) {
            saveBadge(context, Badge("five_expenses", "Consistent Logger", "Logged 5 expenses!", com.example.smartbudgetsa10.R.drawable.ic_dashboard))
        }

        // Achievement: Under Budget
        if (totalAmount > 0 && totalAmount <= budget * 0.5) {
            saveBadge(context, Badge("frugal", "Saving Master", "Spent less than 50% of your budget!", com.example.smartbudgetsa10.R.drawable.ic_reports))
        }
    }
}
