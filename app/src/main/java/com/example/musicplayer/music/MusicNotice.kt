package com.example.musicplayer.music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.music.MusicService.Companion.NOTIFICATION_ID
import com.example.musicplayer.player.PlayManager
import com.example.musicplayer.player.PlayerActivity
import com.example.musicplayer.song.FavorManager

class MusicNotice(val service: MusicService) {

    //remoteView仅支持线性，相对，帧布局
    private val remoteViews: RemoteViews = RemoteViews(service.packageName, R.layout.notification)

    init {
        remoteViews.setImageViewResource(R.id.ivMode, R.drawable.ic_player_circle)
        remoteViews.setImageViewResource(R.id.ivLast, R.drawable.ic_player_previous)
        remoteViews.setImageViewResource(R.id.ivNext, R.drawable.ic_player_next)
        remoteViews.setImageViewResource(R.id.ivPlay, R.drawable.ic_player_play)

        remoteViews.setOnClickPendingIntent(
            R.id.ivLast,
            getActionIntent(MusicService.KEY_ACTION_PREV)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.ivNext,
            getActionIntent(MusicService.KEY_ACTION_NEXT)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.ivPlay,
            getActionIntent(MusicService.KEY_ACTION_PLAY_OR_PAUSE)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.ivMode,
            getActionIntent(MusicService.KEY_SWITCH_MODE)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.ivLike,
            getActionIntent(MusicService.KEY_FAVOR)
        )

        //进入活动
        remoteViews.setOnClickPendingIntent(
            R.id.root, getDetailIntent()
        )
        PlayManager.playMode.observeForever {
            updateUI()
        }
        PlayManager.currentSong.observeForever {
            updateUI()
        }
        PlayManager.isPlaying.observeForever {
            updateUI()
        }
    }

    //更新通知栏UI
    private fun updateUI() {
        val currentSong = PlayManager.currentSong.value
        remoteViews.setTextViewText(
            R.id.songName,
            if (currentSong == null)
                "没有正在播放的歌曲"
            else
                currentSong.title
        )
        remoteViews.setTextViewText(
            R.id.songArtist,
            if (currentSong == null)
                " "
            else
                currentSong.artist
        )
        remoteViews.setImageViewResource(
            R.id.ivPlay,
            if (PlayManager.isPlaying.value == true) R.drawable.ic_player_pause else R.drawable.ic_player_play
        )
        remoteViews.setImageViewResource(R.id.ivMode, PlayManager.getPlayModeIcon())
        remoteViews.setImageViewResource(
            R.id.ivLike,
            if(FavorManager.isCurrentSongFavor())
                R.drawable.ic_player_liked
            else
                R.drawable.ic_player_like
        )
        //获得专辑封面
        val cover = PlayManager.getCoverBitmap()
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.musicBackground, PlayManager.getCoverBitmap())
        } else {
            remoteViews.setImageViewResource(R.id.musicBackground, R.drawable.default_cover)
        }

        createMusicNotification()
    }

    //获取点击通知栏图标后的意图
    private fun getActionIntent(action: String): PendingIntent {
        val intent = Intent(service, MusicService::class.java)
        intent.action = action
        return intent.let {
            PendingIntent.getService(service, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    //点击通知栏打开活动播放详情页
    private fun getDetailIntent(): PendingIntent {
        return PendingIntent.getActivity(
            service,
            0,
            Intent(service, PlayerActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    //创建通知栏
    fun createMusicNotification() {
        val notificationManager =
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //安卓8.0才有 API最低26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MusicService.CHANNEL_ID,
                "Music_Player",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(service, MusicService.CHANNEL_ID)
            .setContent(remoteViews)
            .setSmallIcon(R.mipmap.ic_small)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        notification.apply {
            flags = flags or Notification.FLAG_ONGOING_EVENT
            flags = flags or Notification.FLAG_NO_CLEAR
        }
        service.startForeground(NOTIFICATION_ID, notification)
    }

}