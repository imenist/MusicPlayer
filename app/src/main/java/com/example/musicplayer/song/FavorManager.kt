package com.example.musicplayer.song

import android.widget.Toast
import com.example.musicplayer.player.PlayManager
import com.example.musicplayer.utils.MyApplication
import com.example.musicplayer.utils.Store
import com.google.gson.Gson
import java.util.concurrent.CopyOnWriteArraySet

object FavorManager {
    const val KEY_FAVOR_LIST = "key_star_list"

    //喜好列表
    var favorSongList = CopyOnWriteArraySet<SongBean>()

    //根据喜好来进行列表的更新
    fun updateFavorList(isCurrentFavor: Boolean) {
        val currentSongId = PlayManager.currentSong.value?.id ?: 0
        if (isCurrentFavor) {
            // 添加收藏
            favorSongList.add(PlayManager.currentSong.value)
            Toast.makeText(MyApplication.context, "添加喜欢成功", Toast.LENGTH_SHORT).show()
        } else {
            // 移除收藏
            for (song in favorSongList) {
                if (currentSongId == song.id) {
                    favorSongList.remove(song)
                    break
                }
            }
            Toast.makeText(MyApplication.context, "取消喜欢", Toast.LENGTH_SHORT).show()
        }
        Store.keyValue.encode(KEY_FAVOR_LIST, Gson().toJson(favorSongList))
    }

    fun isCurrentSongFavor(): Boolean {
        val currentSong = PlayManager.currentSong.value ?: return false
        for (song in favorSongList) {
            if (song.id == currentSong.id) {
                return true
            }
        }
        return false
    }

    fun isFavor(song: SongBean):Boolean{
        for(favorSong in favorSongList){
            if(favorSong.id == song.id){
                return true
            }
        }
        return false
    }

    fun insertFavor(song:SongBean){
        favorSongList.add(song)
        Toast.makeText(MyApplication.context, "添加喜欢成功", Toast.LENGTH_SHORT).show()
        Store.keyValue.encode(KEY_FAVOR_LIST, Gson().toJson(favorSongList))
    }

    fun deleteFavor(song:SongBean){
        for(ds in favorSongList){
            if(ds.id == song.id){
                favorSongList.remove(ds)
                break
            }
        }
        Toast.makeText(MyApplication.context, "取消喜欢", Toast.LENGTH_SHORT).show()
        Store.keyValue.encode(KEY_FAVOR_LIST, Gson().toJson(favorSongList))
    }
}