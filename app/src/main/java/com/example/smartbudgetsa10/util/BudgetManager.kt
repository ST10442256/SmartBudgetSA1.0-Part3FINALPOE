package com.example.smartbudgetsa10.util

import android.content.Context
import com.example.smartbudgetsa10.model.Expense
import org.json.JSONArray
import org.json.JSONObject

object BudgetManager {
    private const val PREFS_NAME = "budget_prefs"
    private const val KEY_EXPENSES = "expenses"
    private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    private const val KEY_CATEGORIES = "categories"
    private const val DEFAULT_BUDGET = 2000.0
    private val DEFAULT_CATEGORIES = listOf("Groceries", "Transport", "Entertainment", "Utilities", "Savings")

    private fun getPrefs(context: Context) = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCategory(context: Context, category: String) {
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

    fun saveExpense(context: Context, expense: Expense) {
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
}
