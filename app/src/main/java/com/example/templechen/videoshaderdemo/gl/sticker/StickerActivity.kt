package com.example.templechen.videoshaderdemo.gl.sticker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.gl.SimpleGLSurfaceView
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class StickerActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener {

    companion object {
        private const val TAG = "StickerActivity"
    }

    private lateinit var simpleGLSurfaceView: SimpleGLSurfaceView
    private lateinit var mPlayer: ExoPlayerTool
    private lateinit var mStickerView: StickerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
        )
        simpleGLSurfaceView = findViewById(R.id.simple_gl_surface_view)
        simpleGLSurfaceView.initViews(null, mPlayer, 3)
        mPlayer.addVideoListener(this)

        //sticker
        mStickerView = findViewById(R.id.image)
        Glide.with(this).load(R.drawable.ic_screenroom_playlist_playing_gif).into(mStickerView)
        initSticker()
    }

    private fun initSticker() {
        mStickerView.setOnStickerViewClickListener(object : StickerView.OnStickerViewClickListener {
            override fun onStickerViewClicked(stickerView: StickerView) {
                simpleGLSurfaceView.setCustomStickerView(stickerView)
            }
        })
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = simpleGLSurfaceView.layoutParams
        val viewWidth = simpleGLSurfaceView.width
        val viewHeight = simpleGLSurfaceView.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
        } else {
            params.height = (viewWidth / videoRatio).toInt()
        }
        simpleGLSurfaceView.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

}