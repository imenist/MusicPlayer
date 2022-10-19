package com.example.musicplayer.utils

import android.app.Application
import android.content.SharedPreferences
import com.tencent.mmkv.MMKV

object Store {
    //本地数据存储
    val keyValue: MMKV by lazy { MMKV.defaultMMKV() }
}