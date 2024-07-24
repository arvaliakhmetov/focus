package ru.aval.focus

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.ktor.http.ContentType


class FocusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object ctx {
        lateinit var context: Context
    }
}