package com.example.musicplayer.song

data class SongBean(
    //ID
    val id:Int = 0,
    //资源地址
    val data:String? = "null",
    // 歌名
    var title: String? = "未知",
    // 总时长，单位：秒
    var duration: Long = 0,
    // 大小，单位：字节
    var Size: Long = 0,
    // 歌手
    var artist: String? = "未知",
)