package com.example.easy_storage

import android.app.Application
import com.example.easy_storage.api.auth.TokenManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(applicationContext)
    }
}
