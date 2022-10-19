package com.example.musicplayer.music

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.example.musicplayer.player.PlayManager
import com.example.musicplayer.utils.MyApplication.Companion.context
import java.io.IOException

//播放控制
class MusicPlayer : MediaSessionCompat.Callback(),
    MediaPlayer.OnPreparedListener {

    var mediaPlayer: MediaPlayer = MediaPlayer()
    var mediaSession: MediaSessionCompat = MediaSessionCompat(context, MusicService.SESSION_TAG)
    var onErrorListener: MediaPlayer.OnErrorListener? = null

    init {
        //初始化MediaSessionCompat
        //设置播放控制回调
        mediaSession.setCallback(this)
        //设置可接受媒体控制
        mediaSession.isActive = true
        //设置音频流类型
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer.setOnPreparedListener(this)
        //播放错误
        mediaPlayer.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            return@OnErrorListener onErrorListener?.onError(mp, what, extra) ?: false
        })
        //播放完成，传递回控制器来判断播放模式
        mediaPlayer.setOnCompletionListener {
            PlayManager.next()
        }
        //将播放器传递给控制
        PlayManager.mediaPlayer = mediaPlayer
    }

    //重写Listener 准备完成后可以开始播放
    override fun onPrepared(mp: MediaPlayer?) {
        if (PlayManager.isPlaying.value == true) {
            mediaPlayer.start()
        }
    }

    //点击播放，根据歌曲data播放
    fun onMusicPlay() {
        val song = PlayManager.currentSong.value ?: return
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(song.data)
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            Log.e(e.message, "播放错误")
            onErrorListener?.onError(mediaPlayer, 0, 0)
        }
    }

    //点击暂停
    fun onMusicPause() {
        mediaPlayer.pause()
    }

    //暂停后播放
    fun onMusicResume() {
        mediaPlayer.start()
    }
}