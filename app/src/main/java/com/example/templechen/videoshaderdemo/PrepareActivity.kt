package com.example.templechen.videoshaderdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.example.templechen.videoshaderdemo.gl.SimpleGLActivity
import com.example.templechen.videoshaderdemo.gl.editor.VideoEditorActivity
import com.example.templechen.videoshaderdemo.gl.sticker.StickerActivity

class PrepareActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recordBtn: Button
    private lateinit var playBtn: Button
    private lateinit var stickerBtn: Button
    private lateinit var editorBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare)
        recordBtn = findViewById(R.id.RecordBtn)
        playBtn = findViewById(R.id.PlayingBtn)
        stickerBtn = findViewById(R.id.stickerBtn)
        recordBtn.setOnClickListener(this)
        playBtn.setOnClickListener(this)
        stickerBtn.setOnClickListener(this)

        editorBtn = findViewById(R.id.EditorBtn)
        editorBtn.setOnClickListener(this)
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
            R.id.EditorBtn -> {
                val intent = Intent(this, VideoEditorActivity::class.java)
                startActivity(intent)
            }
        }
    }

}