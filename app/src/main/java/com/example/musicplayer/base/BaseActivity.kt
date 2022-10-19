package com.example.musicplayer.base

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.BottomPlayerBinding
import com.example.musicplayer.player.PlayManager
import com.example.musicplayer.player.PlayerActivity

abstract class BaseActivity : AppCompatActivity() {
    var bottomPlayer: BottomPlayerBinding? = null

    //继承了这个类后，需要把onCreate去掉，否则会不执行
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
        initListener()
        initMiniPlayer()
    }

    protected open fun initBinding() {}

    protected open fun initView() {}

    protected open fun initListener() {}



    private fun initMiniPlayer() {
        bottomPlayer?.let { BP ->
            BP.apply {
                root.setOnClickListener {
                    if (PlayManager.currentSong.value == null) {
                        Toast.makeText(this@BaseActivity, "当前没有正在播放的歌曲", Toast.LENGTH_SHORT).show()
                    } else {
                        startActivity(Intent(this@BaseActivity, PlayerActivity::class.java))
                    }
                }
                ivPlayOrPause.setOnClickListener {
                    PlayManager.playOrPause()
                }

                PlayManager.isPlaying.observe(this@BaseActivity){
                    if(it)
                        ivPlayOrPause.setImageResource(R.drawable.ic_player_pause)
                    else
                        ivPlayOrPause.setImageResource(R.drawable.ic_player_play)
                }
                PlayManager.currentSong.observe(this@BaseActivity){
                    if(it != null){
                        val bitmap = PlayManager.getCoverBitmap()
                        val songName = PlayManager.currentSong.value?.title
                        if(bitmap != null)
                            ivPicCover.setImageBitmap(bitmap)
                        if(songName != null)
                            tvTitle.text = songName
                    }
                }
            }
        }
    }
}