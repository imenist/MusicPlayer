package com.example.musicplayer.music

import android.text.TextUtils
import android.widget.Toast
import com.example.musicplayer.player.PlayManager
import com.example.musicplayer.song.FavorManager
import com.example.musicplayer.utils.MyApplication

class ActionHandler() {

    fun execute(action: String?) {
        if (PlayManager.currentSong.value == null && !TextUtils.isEmpty(action))
            return
        when (action) {
            MusicService.KEY_ACTION_PREV -> {
                PlayManager.last()
            }
            MusicService.KEY_ACTION_NEXT -> {
                PlayManager.next()
            }
            MusicService.KEY_SWITCH_MODE -> {
                PlayManager.switchMode()
            }
            MusicService.KEY_ACTION_PLAY_OR_PAUSE -> {
                PlayManager.playOrPause()
            }
            MusicService.KEY_FAVOR -> {
                PlayManager.switchFavor()
            }
        }
    }
}