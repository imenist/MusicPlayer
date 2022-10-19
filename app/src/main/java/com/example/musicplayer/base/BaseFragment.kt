package com.example.musicplayer.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.example.musicplayer.R
import com.example.musicplayer.player.PlayManager

abstract class BaseFragment : Fragment() {
    val LOCAL_LIST:Int = 0
    val FAVOR_LIST:Int = 1
//    val lifecycleOwner:LifecycleOwner ?= null
//
//    override fun getLifecycle(): Lifecycle {
//        return lifecycleOwner!!.lifecycle
//    }

}

