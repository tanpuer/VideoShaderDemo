package com.example.templechen.videoshaderdemo.gl

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Choreographer
import android.view.Surface
import android.view.TextureView
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.gl.render.RenderThread
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLTextureView(context: Context, player: ExoPlayerTool, activityHandler: ActivityHandler) :
    TextureView(context), TextureView.SurfaceTextureListener, Choreographer.FrameCallback {

    private var mActivityHandler = activityHandler
    private var mPlayer = player
    private var renderThread: RenderThread? = null
    private var mSurface: Surface? = null
    var filterType = 0

    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mSurface = Surface(surface)
        renderThread =
            RenderThread(
                context,
                mSurface!!,
                mActivityHandler,
                GLUtils.getDisplayRefreshNsec(context as Activity),
                mPlayer,
                "BaseFilter"
            )
        renderThread?.start()
        renderThread?.waitUtilReady()
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceCreated(filterType)
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceChanged(0, width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        mSurface = null
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendShutDown()
        renderThread?.join()
        renderThread = null
        Choreographer.getInstance().removeFrameCallback(this)
        return true
    }

    override fun doFrame(frameTimeNanos: Long) {
        val renderHandler = renderThread?.mHandler
        Choreographer.getInstance().postFrameCallback(this)
        renderHandler?.sendDoFrame(frameTimeNanos)
    }

    fun startRecording() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendStartEncoder()
    }

    fun stopRecording() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendStopEncoder()
    }

    fun changeFilter(type: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.changeFilter(type)
    }

}