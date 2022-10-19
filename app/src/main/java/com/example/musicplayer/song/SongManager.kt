package com.example.musicplayer.song

import java.util.concurrent.CopyOnWriteArrayList

object SongManager {
    val allSong = CopyOnWriteArrayList<SongBean>()
    val favorSong = CopyOnWriteArrayList<SongBean>()
}