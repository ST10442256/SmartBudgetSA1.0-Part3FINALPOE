package com.example.smartbudgetsa10.util

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, username: String, password: String) {
        getPrefs(context).edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getUsername(context: Context): String? {
        return getPrefs(context).getString(KEY_USERNAME, null)
    }

    fun verifyPassword(context: Context, password: String): Boolean {
        val savedPassword = getPrefs(context).getString(KEY_PASSWORD, null)
        return savedPassword == password
    }

    fun isUserRegistered(context: Context): Boolean {
        return getUsername(context) != null
    }
}
