package com.example.musicplayer.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.tencent.mmkv.MMKV

class MyApplication:Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = baseContext
        var SP = getSharedPreferences("Data", MODE_PRIVATE).edit()
        SP.putInt("age",18)
        SP.apply()
    }
}