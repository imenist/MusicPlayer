package com.example.musicplayer.main

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.base.BaseFragment
import com.example.musicplayer.song.SongAdapter
import com.example.musicplayer.song.SongBean
import com.example.musicplayer.song.SongManager

class SongFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all, container, false)
        val resolver = requireContext().contentResolver
        val allSong = view.findViewById(R.id.rv_allSong) as RecyclerView
        MediaTask(allSong, requireContext()).execute(resolver)
        return view
    }


    class MediaTask(
        recyclerView: RecyclerView,
        @SuppressLint("StaticFieldLeak") val context: Context
    ) : AsyncTask<ContentResolver, Int, Cursor>() {
        @SuppressLint("StaticFieldLeak")
        val rvContain = recyclerView
        override fun doInBackground(vararg params: ContentResolver): Cursor {
            val resolver = params[0]
            val cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.ARTIST
                ), MediaStore.Audio.Media.DURATION + " > 10", null, null
            )
            return cursor!!
        }

        override fun onPostExecute(cursor: Cursor?) {
            super.onPostExecute(cursor)
            cursor!!.moveToPosition(-1)
            val musicList = ArrayList<SongBean>()
            //把查询完毕的数据放入集合
            while (cursor!!.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val data =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))//播放Uri
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val duration =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))//默认名称
                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))//大小
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))//歌手
                //把内容条目进行填充
                musicList.add(SongBean(id, data, title, duration, size, artist))
            }
            SongManager.allSong.run {
                clear()
                addAll(musicList)
            }

            rvContain.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            var musicAdapter = SongAdapter(musicList,0)
            rvContain.adapter = musicAdapter
        }
    }


}