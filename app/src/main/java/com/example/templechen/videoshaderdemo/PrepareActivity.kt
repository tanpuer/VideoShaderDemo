package com.example.templechen.videoshaderdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.example.templechen.videoshaderdemo.gl.SimpleGLActivity
import com.example.templechen.videoshaderdemo.gl.sticker.StickerActivity
import com.example.templechen.videoshaderdemo.offscreen.OffScreenActivity

class PrepareActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recordBtn: Button
    private lateinit var playBtn: Button
    private lateinit var stickerBtn: Button
    private lateinit var offScreenBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare)
        recordBtn = findViewById(R.id.RecordBtn)
        playBtn = findViewById(R.id.PlayingBtn)
        stickerBtn = findViewById(R.id.stickerBtn)
        recordBtn.setOnClickListener(this)
        playBtn.setOnClickListener(this)
        stickerBtn.setOnClickListener(this)
        offScreenBtn = findViewById(R.id.offScreenBtn)
        offScreenBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.RecordBtn -> {
                val intent = Intent(this, SimpleGLActivity::class.java)
                startActivity(intent)
            }
            R.id.PlayingBtn -> {
                val intent = Intent(this, PurePlayActivity::class.java)
                startActivity(intent)
            }
            R.id.stickerBtn -> {
                val intent = Intent(this, StickerActivity::class.java)
                startActivity(intent)
            }
            R.id.offScreenBtn -> {
                val intent = Intent(this, OffScreenActivity::class.java)
                startActivity(intent)
            }
        }
    }

}