package com.example.musicplayer.player

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.musicplayer.R
import com.example.musicplayer.base.BaseActivity
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.player.PlayManager.playMode
import com.example.musicplayer.song.FavorManager
import com.example.musicplayer.song.SongBean
import com.example.musicplayer.utils.Store
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_player.*
import java.util.concurrent.TimeUnit

class PlayerActivity : BaseActivity(){


    companion object {
        const val UPDATE_PROGRESS = 0
    }

    private lateinit var binding: ActivityPlayerBinding
    private var isFavor = false
    private var favorList = arrayListOf<SongBean>()
    private var currentSong: SongBean? = null
    private var isChanging = false

    //观察由于通知栏操作而使UI发生改变
    override fun initBinding() {
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        updateUI()
        setContentView(binding.root)
        playMode.observe(this) {
            updateUI()
            //Toast.makeText(this, "${PlayManager.getPlayModeString()}", Toast.LENGTH_SHORT).show()
        }
        PlayManager.currentSong.observe(this) {
            updateUI()
            if (currentSong != it) {
                val bitmap = PlayManager.getCoverBitmap()
                if (bitmap != null) {
                    binding.includePlayerCover.ivPlayerCover.setImageBitmap(bitmap)
                } else {
                    binding.includePlayerCover.ivPlayerCover.setImageResource(R.drawable.default_cover)
                }
                objectAnimator.start()
            }
            currentSong = it
        }
        //1.定义写法
        var observer = object : Observer<Boolean>{

            override fun onChanged(t: Boolean?) {
                updateUI()
                if (PlayManager.isPlaying.value!!){
                    objectAnimator.resume()
                }else{
                    objectAnimator.pause()
                }
            }
        }
        PlayManager.isPlaying.observe(this,observer)
        //2.优化写法
        PlayManager.isPlaying.observe(this) {
            updateUI()
            if (it){
                objectAnimator.resume()
            }else{
                objectAnimator.pause()
            }
        }
    }



    //更新播放进度
    private val progressHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_PROGRESS -> {
                    binding.sbSong.progress = getCurrentProgress()
                    binding.tvCurrent.text =
                        getDurationString(PlayManager.mediaPlayer!!.currentPosition.toLong())
//                    var msgTemp = Message.obtain()
//                    msgTemp.what = UPDATE_PROGRESS
//                    sendMessage(msgTemp)
                    sendEmptyMessage(UPDATE_PROGRESS)
//                    var bitmap:Bitmap = Glide.with(this@PlayerActivity)
//                        .asBitmap()
//                        .load("http://cdnoffice.lizhi.fm/payment/2022/09/28/2965935044960488499_640x640.png")
//                        .into(500,500)
//                        .get()
                }
            }
            //拖控seekBar的时候防止改变progress进度
//            if(!isChanging){
//                binding.sbSong.progress = getCurrentProgress()
//                //Toast.makeText(this@PlayerActivity,"progress是${getCurrentProgress()}",Toast.LENGTH_SHORT).show()
//                binding.tvCurrent.text =
//                    getDurationString(PlayManager.mediaPlayer!!.currentPosition.toLong())
//                sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000L)
//            }
        }
    }

    //点击事件
    override fun initListener() {
        binding.apply {
            ivPlay.setOnClickListener {
                PlayManager.playOrPause()
            }
            ivNext.setOnClickListener {
                if(!PlayManager.next())
                    Toast.makeText(this@PlayerActivity, "已经到最后一首了", Toast.LENGTH_SHORT).show()
            }
            ivLast.setOnClickListener {
                PlayManager.last()
            }
            ivMode.setOnClickListener {
                onPlayModeSwitch()
            }
            ivFavor.setOnClickListener {
                onFavorSwitch()
            }
            sbSong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, isChanging: Boolean) {
                    if(isChanging){

                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    var progress = p0?.progress
                }

            })
        }

        //进度条更新
//        progressHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000L)
        var msg = Message.obtain()
        msg.what = UPDATE_PROGRESS
        progressHandler.handleMessage(msg)
        //progressHandler.sendEmptyMessage(UPDATE_PROGRESS)
    }


    //更新UI
    private fun updateUI() {
        isFavor = FavorManager.isCurrentSongFavor()
        binding.tvName.text = PlayManager.currentSong.value?.title
        binding.tvArtist.text = PlayManager.currentSong.value?.artist
        binding.ivPlay.setImageResource(
            if (PlayManager.isPlaying.value == true)
                R.drawable.ic_player_pause
            else R.drawable.ic_player_play
        )
        binding.sbSong.max = 100
        binding.tvDuration.text = getDurationString(PlayManager.currentSong.value?.duration ?: 0)
        binding.ivMode.setImageResource(PlayManager.getPlayModeIcon())
        binding.ivFavor.run {
            if (!isFavor) {
                this.setImageResource(R.drawable.ic_player_like)
            } else {
                this.setImageResource(R.drawable.ic_player_liked)
            }
        }
    }

    //CD旋转
    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(
            binding.includePlayerCover.root,
            "rotation", 0f, 360f
        ).apply {
            interpolator = LinearInterpolator()
            duration = 32_000L
            repeatCount = -1
        }
    }

    //获取播放进度
    private fun getCurrentProgress(): Int {
        if (PlayManager.mediaPlayer == null) {
            return 0
        }
        val duration = PlayManager.currentSong.value?.duration ?: return 0
        return ((PlayManager.mediaPlayer!!.currentPosition.toFloat() / duration) * 100).toInt()
    }

    //获取时长
    private fun getDurationString(duration: Long): String {
        val seconds = duration / 1000
        val m = TimeUnit.SECONDS.toMinutes(seconds)
        val s = seconds - m * 60
        return String.format("%02d:%02d", m, s)
    }

    //活动销毁
    override fun onDestroy() {
        //消除handler信息
        progressHandler.removeMessages(UPDATE_PROGRESS)
        super.onDestroy()
    }

    //切换歌曲播放模式
    private fun onPlayModeSwitch() {
        PlayManager.switchMode()
        updateUI()
        Toast.makeText(this, "${PlayManager.getPlayModeString()}", Toast.LENGTH_SHORT).show()
    }

    //监听喜好
    private fun onFavorSwitch() {
        isFavor = !isFavor
        FavorManager.updateFavorList(isFavor)
        binding.ivFavor.run {
            if (!isFavor) {
                this.setImageResource(R.drawable.ic_player_like)
            } else {
                this.setImageResource(R.drawable.ic_player_liked)
            }
        }
        val favorListString = Store.keyValue.decodeString(FavorManager.KEY_FAVOR_LIST)
        //Toast.makeText(context, "${favorListString}", Toast.LENGTH_SHORT).show()
        FavorManager.favorSongList.clear()
        if (!TextUtils.isEmpty(favorListString)) {
            try {
                favorList = Gson().fromJson<ArrayList<SongBean>>(
                    favorListString,
                    object : TypeToken<ArrayList<SongBean>>() {}.type
                )
                FavorManager.favorSongList.addAll(favorList)
            } catch (e: Exception) {
                Log.e("favor", "1${e.message}")
            }
        }
    }
}