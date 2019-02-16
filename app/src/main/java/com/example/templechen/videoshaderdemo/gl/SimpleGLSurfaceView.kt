package com.example.templechen.videoshaderdemo.gl

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.gl.render.RenderThread
import com.example.templechen.videoshaderdemo.gl.sticker.StickerUtil
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLSurfaceView : SurfaceView, SurfaceHolder.Callback, Choreographer.FrameCallback, SimpleGLView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var mActivityHandler: ActivityHandler? = null
    private lateinit var mPlayer: ExoPlayerTool
    private var renderThread: RenderThread? = null
    private var mSurface: Surface? = null
    private var mFilterType = 0
    private var mStickerView: View? = null

    override fun initViews(activityHandler: ActivityHandler?, playerTool: ExoPlayerTool, filterType: Int) {
        mActivityHandler = activityHandler
        mPlayer = playerTool
        mFilterType = filterType
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
        renderHandler?.sendSurfaceCreated(mFilterType)
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
        if (mStickerView != null && mFilterType == 3) {
            renderHandler?.setCustomWaterMark(StickerUtil.convertViewToBitmap(mStickerView!!))
        }
        renderHandler?.sendDoFrame(frameTimeNanos)
    }

    override fun startRecording() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendStartEncoder()
    }

    override fun stopRecording() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.sendStopEncoder()
    }

    override fun changeFilter(type: Int) {
        val renderHandler = renderThread?.mHandler
        mFilterType = type
        renderHandler?.changeFilter(type)
    }

    override fun renderAnotherSurface(surface: Surface?) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.renderAnotherSurface(surface)
    }

    override fun stopRenderAnotherSurface() {
        val renderHandler = renderThread?.mHandler
        renderHandler?.stopRenderAnotherSurface()
    }

    override fun getView(): View {
        return this
    }

    override fun setVideoEditorRect(rect: Rect) {
        val renderHandler = renderThread?.mHandler
        renderHandler?.setVideoEditorRect(rect)
    }

    override fun setCustomStickerView(view: View?) {
        mStickerView = view
    }

}