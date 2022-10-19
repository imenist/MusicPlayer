package com.example.musicplayer.player

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.example.musicplayer.R
import com.example.musicplayer.music.MusicPlayer
import com.example.musicplayer.song.FavorManager
import com.example.musicplayer.song.SongBean
import com.example.musicplayer.utils.MyApplication
import java.util.concurrent.CopyOnWriteArrayList

object PlayManager {

    val musicPlayer = MusicPlayer()

    var KEY_CURRENT_SONG_ID = -1
    private var KEY_PLAY_MODE = 0
    var KEY_CURRENT_LIST_TYPE = -1
    var mediaPlayer: MediaPlayer? = null

    //顺序播放
    const val ALL_ONCE = 0

    //列表循环
    const val ALL_CIRCLE = 1

    //单曲循环
    const val REPEAT_ONE = 2

    //随机播放
    const val RANDOM = 3

    //使用livedata可以方便观察
    var currentSong = MutableLiveData<SongBean>()

    var isPlaying = MutableLiveData<Boolean>()

    var currentListType = MutableLiveData<Int>()

    var playMode = MutableLiveData(ALL_ONCE)

    //当前播放列表
    var currentSongList = CopyOnWriteArrayList<SongBean>()


    fun switchMode() {
        var mode = (playMode.value ?: ALL_ONCE) + 1
        if (mode > RANDOM) {
            mode = ALL_ONCE
        }
        playMode.value = mode
    }

    fun playOrPause() {
        if (isPlaying.value == true) {
            isPlaying.value = false
            onMusicPause()
            //Toast.makeText(context,"状态${PlayManager.isPlaying}",Toast.LENGTH_SHORT).show()
        } else {
            isPlaying.value = true
            onMusicResume()
        }
    }

    fun onMusicPlay(songBean: SongBean) {
        isPlaying.value = true
        currentSong.value = songBean
        musicPlayer.onMusicPlay()
    }

    fun onMusicPause() {
        isPlaying.value = false
        musicPlayer.onMusicPause()
    }

    fun onMusicResume() {
        isPlaying.value = true
        musicPlayer.onMusicResume()
    }

    //下一首
    fun next(): Boolean {
        val tempSong = currentSong.value
        if (currentSongList.isEmpty() || tempSong == null) {
            return false
        }
        when (playMode.value) {
            //顺序播放
            ALL_ONCE -> {
                val index = currentSongList.indexOf(tempSong)
                if (index >= currentSongList.size - 1)
                    return false
                else onMusicPlay(currentSongList[index + 1])
            }
            //列表循环
            ALL_CIRCLE -> {
                val index = currentSongList.indexOf(tempSong)
                onMusicPlay(currentSongList[(index + 1) % currentSongList.size])
            }
            //单曲循环
            REPEAT_ONE -> {
                onMusicPlay(tempSong)
            }
            //随机播放
            RANDOM -> {
                onMusicPlay(currentSongList.random())
            }
        }
        return true
    }

    //上一首
    fun last(): Boolean {
        val tempSong = currentSong.value
        if (currentSongList.isEmpty() || tempSong == null) {
            return false
        }
        when (playMode.value) {
            ALL_ONCE -> {
                val index = currentSongList.indexOf(tempSong)
                if (index <= 0) return false
                else onMusicPlay(currentSongList[index - 1])
            }
            ALL_CIRCLE -> {
                val index = currentSongList.indexOf(tempSong)
                onMusicPlay(currentSongList[(index - 1 + currentSongList.size) % currentSongList.size])
            }
            REPEAT_ONE -> {
                onMusicPlay(tempSong)
            }
            RANDOM -> {
                onMusicPlay(currentSongList.random())
            }
        }
        return true
    }

    //更新当前播放列表
    fun updateList(list: List<SongBean>, listType: Int, play: SongBean?) {
        onMusicPause()
        currentSongList.clear()
        currentSongList.addAll(list)
        currentListType.value = listType
        if (play != null) {
            onMusicPlay(play)
        }
    }

    @DrawableRes
    fun getPlayModeIcon(): Int {
        return when (playMode.value) {
            ALL_CIRCLE -> R.drawable.ic_player_circle
            REPEAT_ONE -> R.drawable.ic_player_repeat_one
            RANDOM -> R.drawable.ic_player_random
            else -> R.drawable.ic_player_once
        }
    }


    fun getPlayModeString():String{
        return when(playMode.value){
            ALL_CIRCLE -> "列表循环"
            REPEAT_ONE -> "单曲循环"
            RANDOM -> "随机播放"
            else -> "顺序播放"
        }
    }

    fun getCoverBitmap(): Bitmap? {
        val song = currentSong.value ?: return null
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(song.data)
        val byteArray = mediaMetadataRetriever.embeddedPicture ?: return null
        return BitmapFactory.decodeByteArray(
            mediaMetadataRetriever.embeddedPicture,
            0,
            byteArray.size
        )
    }

    fun switchFavor(){
//        val song = currentSong.value
//        FavorManager.updateFavorList(FavorManager.isCurrentSongFavor())
        Toast.makeText(MyApplication.context, "需要到详情页设置", Toast.LENGTH_SHORT).show()
    }
}