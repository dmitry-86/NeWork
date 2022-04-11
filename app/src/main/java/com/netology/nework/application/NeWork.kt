package com.netology.nework.application

import android.app.Application
import com.netology.nework.auth.AppAuth

class NeWork : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}