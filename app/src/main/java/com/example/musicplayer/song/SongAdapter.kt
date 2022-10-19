package com.example.musicplayer.song

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.player.PlayManager
import com.example.musicplayer.player.PlayerActivity
import com.example.musicplayer.utils.MyApplication

class SongAdapter(private val songList: ArrayList<SongBean>, private val listType: Int) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songImage: ImageView = view.findViewById(R.id.ivCover)
        val songTitle: TextView = view.findViewById(R.id.tvTitle)
        val songArtist: TextView = view.findViewById(R.id.tvSinger)
        val songMenu: ImageView = view.findViewById(R.id.ivMenu)
    }

    private var selectedIndex:Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_allsong, parent, false)
        val vh = ViewHolder(view)
        vh.itemView.setOnClickListener {
            val position = vh.adapterPosition
            val song = songList[position]
            val intent = Intent(vh.itemView.context, PlayerActivity::class.java)
            selectedIndex = position
            //判断列表，不同列表需要重新载入队列,即使同样的歌曲也重新播放
            //相同列表如果不是同一首歌才执行播放
            if (PlayManager.currentListType.value != listType) {
                PlayManager.updateList(songList, listType, song)
            }else{
                if(PlayManager.currentSong.value != song)
                    PlayManager.onMusicPlay(song)
            }
            notifyDataSetChanged()
            vh.itemView.context.startActivity(intent)
        }
        vh.songMenu.setOnClickListener {
            val position = vh.adapterPosition
            myPopupMenu(view,position)
        }
        return vh

    }

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        val song = songList[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist
        val pic = getCover(song)
        if(pic != null)
            holder.songImage.setImageBitmap(getCover(song))
        else
            holder.songImage.setImageResource(R.drawable.ic_song_cover)
        //设置颜色
        if(position == selectedIndex){
            holder.songTitle.setTextColor(ContextCompat.getColor(MyApplication.context,R.color.heart_red))
            holder.songArtist.setTextColor(ContextCompat.getColor(MyApplication.context,R.color.heart_red))
        }else{
            holder.songTitle.setTextColor(ContextCompat.getColor(MyApplication.context,R.color.black))
            holder.songArtist.setTextColor(ContextCompat.getColor(MyApplication.context,R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    private fun getCover(song:SongBean): Bitmap?{
        if(song == null) return null
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(song.data)
        val byteArray = mediaMetadataRetriever.embeddedPicture ?: return null
        return BitmapFactory.decodeByteArray(
            mediaMetadataRetriever.embeddedPicture,
            0,
            byteArray.size
        )
    }

    fun myPopupMenu(view:View,position: Int){
        var popupMenu = PopupMenu(MyApplication.context,view)
        popupMenu.gravity = Gravity.CENTER
        popupMenu.menuInflater.inflate(R.menu.popupmenu,popupMenu.menu)
        var song = songList[position]
        popupMenu.setOnMenuItemClickListener (PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.menu_favor -> {
                    if(FavorManager.isFavor(song)){
                        Toast.makeText(MyApplication.context, "当前歌曲已经被收藏", Toast.LENGTH_SHORT).show()
                    }else{
                        FavorManager.insertFavor(song)
                    }
                }
                R.id.menu_delete -> {
                    if(listType == 0){
                        Toast.makeText(MyApplication.context, "当前为本地歌曲列表，不能删除", Toast.LENGTH_SHORT).show()
                    }else{
                        FavorManager.deleteFavor(song)
                        songList.remove(song)
                        this.notifyItemRemoved(position)
                    }
                }
            }
            notifyDataSetChanged()
            true
        })
        popupMenu.show()
        }
}

