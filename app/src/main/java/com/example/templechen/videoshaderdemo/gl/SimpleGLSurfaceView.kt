package com.example.templechen.videoshaderdemo.gl

import android.app.Activity
import android.content.Context
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLSurfaceView(context: Context, player: ExoPlayerTool) : SurfaceView(context), SurfaceHolder.Callback,
    Choreographer.FrameCallback {

    private var activityHandler: ActivityHandler = ActivityHandler(context as Activity)
    private var mPlayer = player
    private var renderThread: RenderThread? = null

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        renderThread =
                RenderThread(context, holder!!.surface, activityHandler, GLUtils.getDisplayRefreshNsec(context as Activity), mPlayer)
        renderThread?.start()
        renderThread?.waitUtilReady()
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceCreated()
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendSurfaceChanged(format, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
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

}