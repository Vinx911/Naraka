package com.vinx911.naraka

import android.app.Application
import android.util.Log

class App : Application() {
    override fun onCreate() {
        super.onCreate()

//        Naraka.initialize(this, NarakaDefaultErrorActivity::class.java)
        Naraka.initialize(this, CrashActivity::class.java) {
            Log.e("TAG", "onCreate: $it")
        }
    }

}