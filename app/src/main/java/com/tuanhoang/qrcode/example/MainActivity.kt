package com.tuanhoang.qrcode.example

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuanhoang.qrcode.QrCode
import com.tuanhoang.qrcode.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        QrCode.generateQRCodeWithLogo(text = "https://github.com/hoanganhtuan95ptit", logo = BitmapFactory.decodeResource(resources, R.drawable.img_logo)).let {

            binding.ivQrCode.setImageBitmap(it)
        }
    }
}