package com.example.musicplayer.main

import android.os.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicplayer.base.BaseActivity
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.musicplayer.music.MusicService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahm.library.EasyProtectorLib


class MainActivity : BaseActivity() {
    private val PERMISSION_REQUEST_READ = 1
    private lateinit var binding: ActivityMainBinding




    override fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        bottomPlayer = binding.bottomPlayer
        setContentView(binding.root)
        if (EasyProtectorLib.checkIsRoot()) {
            Toast.makeText(this, "root了", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "没有root", Toast.LENGTH_SHORT).show()
        }
    }

    //创建UI前检查是否有读取权限
    override fun initView() {
        //有读取权限则可以进行渲染
        if (getPermissionEnabled()) {
            init()
        } else {
            requestPermission()
        }
        val notificationManager:NotificationManagerCompat = NotificationManagerCompat.from(this)
        //判定有无通知权限
        if(!notificationManager.areNotificationsEnabled()){
            val notificationDialog:MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
                .setTitle("提示")
                .setMessage("若需要在通知栏控制播放，请开启本应用的通知权限")
                .setPositiveButton("现在开启",DialogInterface.OnClickListener(){dialogInterface, i ->
                    jumpToSetting()
                    dialogInterface.cancel()
                })
            notificationDialog.create().show()
        }
    }

    override fun initListener() {
        super.initListener()
        binding.userBtn.setOnClickListener {
            var intent = Intent(this@MainActivity, UserActivity::class.java)
            startActivity(intent)
        }
    }
    fun init(){
        //开启服务
        startService(Intent(this,MusicService::class.java))
        binding.pager.offscreenPageLimit = 2
        binding.pager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position){
                    0 -> SongFragment()
                    else -> FavorFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout,binding.pager){tab,position->
            tab.text =when (position){
                0 -> "本地音乐"
                else -> "喜欢"
            }
        }.attach()
        binding.pager.setCurrentItem(0,false)
    }

    //获取权限状态
    private fun getPermissionEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    }

    //申请读取权限
    private fun requestPermission(){
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ),
            PERMISSION_REQUEST_READ
        )
    }

    //申请后判断是否得到
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST_READ -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                init()
            else Toast.makeText(this, "请授予权限", Toast.LENGTH_SHORT).show()
        }
    }

    //跳转到设置页面
    private fun jumpToSetting(){
        val intent = Intent()
        try{
            //直接进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("package",packageName)
            val uri = Uri.fromParts("package",packageName,null)
            intent.data = uri
//            直接设置通知权限 但不能应用
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE,packageName)
//            intent.putExtra(Settings.EXTRA_CHANNEL_ID,applicationInfo.uid)
            startActivity(intent)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}