package com.example.easy_storage.api.auth

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREF_NAME = "easy_storage_prefs"
    private const val TOKEN_KEY = "auth_token"
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs?.getString(TOKEN_KEY, null)
        set(value) {
            prefs?.edit()?.putString(TOKEN_KEY, value)?.apply()
        }

    fun clearToken() {
        prefs?.edit()?.remove(TOKEN_KEY)?.apply()
    }
}
