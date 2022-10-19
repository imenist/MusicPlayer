package com.example.musicplayer.main

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.musicplayer.R
import com.example.musicplayer.base.BaseFragment
import com.example.musicplayer.song.FavorManager
import com.example.musicplayer.song.SongAdapter
import com.example.musicplayer.song.SongBean
import com.example.musicplayer.utils.Store
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.musicplayer.databinding.FragmentFavoriteBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import java.lang.Exception

class FavorFragment : BaseFragment(),SwipeRefreshLayout.OnRefreshListener {
    private var favorList = arrayListOf<SongBean>()
    private lateinit var mAdapter: SongAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getFavorList()
        val view = inflater.inflate(R.layout.fragment_favorite,container,false)
        mAdapter = SongAdapter(favorList,FAVOR_LIST)
        val allSong = view.findViewById(R.id.rv_favor) as RecyclerView
        val refresh = view.findViewById(R.id.favor_refresh) as SwipeRefreshLayout
        refresh.setOnRefreshListener(this)
        allSong.layoutManager = LinearLayoutManager(activity)
        allSong.adapter = mAdapter
        return view
    }

    override fun onRefresh() {
        favor_refresh.isRefreshing = false
        updateFavorList()
    }

    private fun getFavorList(){
        val favorListString = Store.keyValue.decodeString(FavorManager.KEY_FAVOR_LIST)
        //Toast.makeText(context, "${favorListString}", Toast.LENGTH_SHORT).show()
        FavorManager.favorSongList.clear()
        if(!TextUtils.isEmpty(favorListString)){
            try{
                favorList= Gson().fromJson<ArrayList<SongBean>>(
                    favorListString,
                    object : TypeToken<ArrayList<SongBean>>(){}.type
                )
                FavorManager.favorSongList.addAll(favorList)
            }catch (e:Exception){
                Log.e("favor","1${e.message}")
            }
        }
    }

    //更新喜好列表
    private fun updateFavorList(){
        favorList.clear()
        favorList.addAll(FavorManager.favorSongList)
        mAdapter.notifyDataSetChanged()
    }



}