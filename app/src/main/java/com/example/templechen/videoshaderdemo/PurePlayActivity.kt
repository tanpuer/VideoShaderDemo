package com.example.templechen.videoshaderdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import java.io.File

class PurePlayActivity : AppCompatActivity(), SurfaceHolder.Callback, ExoPlayerTool.IVideoListener{

    private lateinit var mParentView: RelativeLayout
    private lateinit var mSurfaceView: SurfaceView
    private var mPlayer: ExoPlayerTool? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mParentView = RelativeLayout(this)
        mParentView.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(mParentView)
        mSurfaceView = SurfaceView(this)
        val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        mParentView.addView(mSurfaceView, layoutParams)
        mSurfaceView.holder.addCallback(this)

        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer?.quickSetting(
            this,
            "file://${File(this.cacheDir, "gltest.mp4")}"
        )
        mPlayer?.addVideoListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mPlayer?.setVideoSurface(holder?.surface)
        mPlayer?.setPlayWhenReady(true)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mPlayer?.setPlayWhenReady(false)
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = mSurfaceView.layoutParams
        val viewWidth = mSurfaceView.width
        val viewHeight = mSurfaceView.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
        } else {
            params.height = (viewWidth / videoRatio).toInt()
        }
        mSurfaceView.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
    }
}