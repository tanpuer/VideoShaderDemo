package com.example.templechen.videoshaderdemo.gl

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener {

    private lateinit var simpleGLSurfaceView: SimpleGLSurfaceView
    private lateinit var parentView: RelativeLayout
    private lateinit var mPlayer: ExoPlayerTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentView = RelativeLayout(this)
        parentView.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(parentView)

        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
        )
        simpleGLSurfaceView = SimpleGLSurfaceView(this, mPlayer)
        val params =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        parentView.addView(simpleGLSurfaceView, params)
        mPlayer.addVideoListener(this)
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

    override fun onPause() {
        super.onPause()
        mPlayer.setPlayWhenReady(false)
    }

    override fun onResume() {
        super.onResume()
        mPlayer.setPlayWhenReady(true)
    }
}