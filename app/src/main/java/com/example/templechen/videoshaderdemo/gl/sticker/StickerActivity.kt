package com.example.templechen.videoshaderdemo.gl.sticker

import android.graphics.RectF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.gl.ActivityHandler
import com.example.templechen.videoshaderdemo.gl.IGLInfoCallback
import com.example.templechen.videoshaderdemo.gl.SimpleGLSurfaceView
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class StickerActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener, IGLInfoCallback {

    companion object {
        private const val TAG = "StickerActivity"
    }

    private lateinit var simpleGLSurfaceView: SimpleGLSurfaceView
    private lateinit var fpsView: TextView
    private lateinit var glVersionView: TextView
    private lateinit var mPlayer: ExoPlayerTool
    private lateinit var mStickerView: StickerView
    private var mStickerViewRectF = RectF()
    private lateinit var mActivityHandler: ActivityHandler
    private var mVideoWidthZoomScale = 1f
    private var mVideoHeightZoomScale = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
        )
        simpleGLSurfaceView = findViewById(R.id.simple_gl_surface_view)
        mActivityHandler = ActivityHandler(this)
        simpleGLSurfaceView.initViews(mActivityHandler, mPlayer, 3)
        mPlayer.addVideoListener(this)

        //sticker
        mStickerView = findViewById(R.id.image)
        Glide.with(this).load(R.drawable.drawer_amino_logo).into(mStickerView)
        initSticker()

        //fps and version
        fpsView = findViewById(R.id.fps)
        glVersionView = findViewById(R.id.gl_version)
    }

    private fun initSticker() {
        mStickerView.setOnStickerViewClickListener(object : StickerView.OnStickerViewClickListener {
            override fun onStickerViewClicked(stickerView: StickerView) {
                simpleGLSurfaceView.setCustomStickerView(stickerView)
            }
        })
        mStickerView.setOnStickerViewScrollListener(object : StickerView.OnStickerViewScroll {
            override fun stickerViewScroll(stickerView: StickerView) {
                //calculate StickerView's rect relative to SimpleGLSurfaceView
                if (stickerView.left < simpleGLSurfaceView.right
                    && stickerView.top < simpleGLSurfaceView.bottom
                    && stickerView.right > simpleGLSurfaceView.left
                    && stickerView.bottom > simpleGLSurfaceView.top
                ) {
                    val centerX = (simpleGLSurfaceView.left + simpleGLSurfaceView.right) / 2
                    val centerY = ((simpleGLSurfaceView.top) + simpleGLSurfaceView.bottom) / 2
                    mStickerViewRectF.set(
                        (stickerView.left - centerX) * 2.0f / simpleGLSurfaceView.width,
                        (centerY - stickerView.top) * 2.0f / simpleGLSurfaceView.height,
                        (stickerView.right - centerX) * 2.0f / simpleGLSurfaceView.width,
                        (centerY - stickerView.bottom) * 2.0f / simpleGLSurfaceView.height
                    )
                    simpleGLSurfaceView.setCustomWaterMarkRectF(mStickerViewRectF)
                }
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

        mVideoWidthZoomScale = params.width * 1.0f / width
        mVideoHeightZoomScale = params.height * 1.0f / height
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

    override fun updateFps(tfps: Int, dropped: Int) {
        fpsView.text = "Frame rate: ${tfps / 1000.0f}fps (${dropped} dropped)"
    }

    override fun updateGLVersion(version: Int) {
        glVersionView.text = "GLES Version: ${version}"
    }
}