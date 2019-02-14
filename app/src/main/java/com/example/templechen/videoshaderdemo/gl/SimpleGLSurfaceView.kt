package com.example.templechen.videoshaderdemo.gl

import android.app.Activity
import android.content.Context
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.gl.render.RenderThread
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLSurfaceView(context: Context, player: ExoPlayerTool, activityHandler: ActivityHandler?) :
    SurfaceView(context), SurfaceHolder.Callback,
    Choreographer.FrameCallback {

    private var mActivityHandler = activityHandler
    private var mPlayer = player
    private var renderThread: RenderThread? = null
    private var mSurface: Surface? = null
    var filterType = 0

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurface = holder?.surface
        renderThread =
            RenderThread(
                context,
                holder!!.surface,
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

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceChanged(format, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mSurface = null
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendShutDown()
        renderThread?.join()
        renderThread = null
        Choreographer.getInstance().removeFrameCallback(this)
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

    fun renderAnotherSurface(surface: Surface?) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.renderAnotherSurface(surface)
    }

    fun stopRenderAnotherSurface() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.stopRenderAnotherSurface()
    }

}