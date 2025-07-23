package com.example.recipttracker.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "user_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_LOGIN_TIME = "login_time"
    private const val EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L  // 7 days in milliseconds

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            if (isLoggedIn) {
                putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            } else {
                remove(KEY_LOGIN_TIME)
            }
        }.apply()
    }

    fun isLoggedIn(): Boolean {
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0L)
        val currentTime = System.currentTimeMillis()

        return if (loggedIn && (currentTime - loginTime) <= EXPIRY_MS) {
            true
        } else {
            logout() // Expired
            false
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}