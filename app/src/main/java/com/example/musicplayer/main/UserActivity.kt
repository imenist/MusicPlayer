package com.example.musicplayer.main

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.base.BaseActivity
import com.example.musicplayer.databinding.ActivityUserBinding
import com.example.musicplayer.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : BaseActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel : UserViewModel
    private val TAG = "user"

    override fun initBinding() {
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        viewModel = ViewModelProvider(this@UserActivity).get(UserViewModel::class.java)
    }

    override fun initListener() {
        super.initListener()
        binding.btnGet.setOnClickListener{
            Toast.makeText(this, "get", Toast.LENGTH_SHORT).show()
//            var sp  = getSharedPreferences("Data", MODE_PRIVATE)
//            var age = sp.getInt("age",0)
//            binding.tvSp.text = age.toString()
            var sp = getPreferences(MODE_PRIVATE)
            binding.tvSp.text = sp.getString("like","123")
        }
        binding.btnStore.setOnClickListener {
            Toast.makeText(this, "store", Toast.LENGTH_SHORT).show()
            var editor = getSharedPreferences("Data",MODE_PRIVATE).edit()
            editor.putString("name","Aniware")
            editor.apply()

            var activityEditor = getPreferences(MODE_PRIVATE).edit()
            activityEditor.putString("like","basketball")
            activityEditor.apply()
        }

        btn_plus.setOnClickListener {
            Toast.makeText(this, "plus", Toast.LENGTH_SHORT).show()
            viewModel.plus()
        }

        viewModel.counter.observe(this) {
            binding.tvSp.text = it.toString()
        }
    }
}