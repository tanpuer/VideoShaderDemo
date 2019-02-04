package com.example.templechen.videoshaderdemo.gl

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Choreographer
import android.view.Surface
import android.view.TextureView
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLTextureView(context: Context, player: ExoPlayerTool, activityHandler: ActivityHandler) :
    TextureView(context), TextureView.SurfaceTextureListener, Choreographer.FrameCallback {

    private var mActivityHandler = activityHandler
    private var mPlayer = player
    private var renderThread: RenderThread? = null

    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        renderThread =
                RenderThread(
                    context,
                    Surface(surface),
                    mActivityHandler,
                    GLUtils.getDisplayRefreshNsec(context as Activity),
                    mPlayer
                )
        renderThread?.start()
        renderThread?.waitUtilReady()
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceCreated()
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceChanged(0, width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
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


}