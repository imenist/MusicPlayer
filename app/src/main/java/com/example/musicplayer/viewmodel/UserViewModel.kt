package com.example.musicplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author   :hejunxi
 * @data     :2022/10/10
 */
class UserViewModel : ViewModel() {
    val counter : LiveData<Int>
        get() = _counter
    private var _counter = MutableLiveData<Int>()

    init {
        _counter.value = 0
    }

    fun plus(){
        val value = _counter.value ?: 0
        _counter.value = value + 1
    }
}