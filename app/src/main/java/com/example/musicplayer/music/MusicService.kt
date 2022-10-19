package com.example.musicplayer.music

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.example.musicplayer.utils.MyApplication


class MusicService : Service() {

    companion object {
        // 整型 ID 不得为 0
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "Player_Music_ID"
        const val SESSION_TAG = "Music_Session"
        const val KEY_ACTION_PREV = "1"
        const val KEY_ACTION_NEXT = "2"
        const val KEY_ACTION_PLAY_OR_PAUSE = "3"
        const val KEY_SWITCH_MODE = "4"
        const val KEY_FAVOR = "5"
    }

    private lateinit var musicNotice: MusicNotice

    override fun onCreate() {
        musicNotice = MusicNotice(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            ActionHandler().execute(intent.action)
        }
        return START_STICKY
    }


}