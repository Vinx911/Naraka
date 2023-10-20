package com.vinx911.naraka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vinx911.naraka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setOnClickListeners()
        setContentView(binding.root)
    }

    private fun setOnClickListeners() {
        binding.bThrowError.setOnClickListener {
            throw Error("Hello, I'm the crash!")
        }
    }

}