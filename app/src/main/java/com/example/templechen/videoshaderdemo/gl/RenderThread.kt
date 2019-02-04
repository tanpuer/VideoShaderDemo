package com.example.templechen.videoshaderdemo.gl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.filter.*
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class RenderThread(
    context: Context,
    surface: Surface,
    activityHandler: ActivityHandler,
    refreshPeriodsNs: Long,
    player: ExoPlayerTool
) :
    Thread() {

    companion object {
        private const val TAG = "RenderThread"
    }

    lateinit var mHandler: RenderHandler
    private lateinit var mEglCore: EglCore
    //use to wait for the thread to start
    private val mStartLock = Object()
    private var mReady = false

    private var mSurface = surface
    private var mPlayer = player
    private var mContext = context
    private var mActivityHandler = activityHandler
    private var mRefreshPeriod = refreshPeriodsNs
    private lateinit var mWindowSurface: WindowSurface
    private var mDisplayProjectionMatrix = FloatArray(16) { 0f }

    // Previous frame time.
    private var mPrevTimeNanos: Long = 0

    // FPS / drop counter.
    private var mRefreshPeriodNanos: Long = 0
    private var mFpsCountStartNanos: Long = 0
    private var mFpsCountFrame: Int = 0
    private var mDroppedFrames: Int = 0
    private var mPreviousWasDropped: Boolean = false

    //my custom program
    private lateinit var filter: BaseFilter
    private var mOESTextureId: Int = -1
    private lateinit var mSurfaceTexture: SurfaceTexture

    override fun run() {
        Looper.prepare()
        mHandler = RenderHandler(this)
        mEglCore = EglCore(null, EglCore.FLAG_RECORDABLE.or(EglCore.FLAG_TRY_GLES3))
        synchronized(mStartLock) {
            mReady = true
            mStartLock.notify()  // signal waitUntilReady()
        }
        Looper.loop()
        Log.d(TAG, "looper quit")
        releaseGL()
        mEglCore.release()
        synchronized(mStartLock) {
            mReady = false
        }
    }

    /**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    fun waitUtilReady() {
        synchronized(mStartLock) {
            while (!mReady) {
                mStartLock.wait()
            }
        }
    }

    fun surfaceCreated() {
        prepareGL(mSurface)
    }

    private fun prepareGL(surface: Surface) {
        Log.d(TAG, "prepareGl")
        mWindowSurface = WindowSurface(mEglCore, surface, false)
        mWindowSurface.makeCurrent()
        //custom program
        mOESTextureId = GLUtils.createOESTextureObject()
        mSurfaceTexture = SurfaceTexture(mOESTextureId)
        filter = SketchFilter(mContext, mOESTextureId)
        filter.initProgram()

        //todo main thread
        mPlayer.setVideoSurface(Surface(mSurfaceTexture))
        mPlayer.setPlayWhenReady(true)

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glDisable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        mActivityHandler.sendGLESVersion(mEglCore.glVersion)
    }

    fun surfaceChanged(width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged " + width + "x" + height)
        prepareFrameBuffer()
        GLES20.glViewport(0, 0, width, height)
        Matrix.orthoM(mDisplayProjectionMatrix, 0, 0f, width.toFloat(), 0f, height.toFloat(), -1f, 1f)

    }

    /**
     * Prepares the off-screen framebuffer.
     */
    private fun prepareFrameBuffer() {
        GLUtils.checkGlError("prepareFramebuffer start")
        //todo
    }

    /**
     * Advance state and draw frame in response to a vsync event.
     */
    fun doFrame(timestampsNanos: Long) {
        update(timestampsNanos)
        val diff = System.nanoTime() - timestampsNanos
        val max = mRefreshPeriod - 2000000  // if we're within 2ms, don't bother
        if (diff > max) {
            // too much, drop a frame
            Log.d(
                TAG, "diff is " + (diff / 1000000.0) + " ms, max " + (max / 1000000.0) +
                        ", skipping render"
            )
            mPreviousWasDropped = true
            mDroppedFrames++
            return
        }

        // Render the scene, swap back to front.
        draw()
        val swapResult: Boolean = mWindowSurface.swapBuffers()

        if (!swapResult) {
            Log.w(TAG, "swapBuffers failed, killing renderer thread")
            shutDown()
            return
        }

        //update fps
        val NUM_FRAMES = 120
        val ONE_TRILLION = 1000000000000L
        if (mFpsCountStartNanos == 0L) {
            mFpsCountStartNanos = timestampsNanos
            mFpsCountFrame = 0
        } else {
            mFpsCountFrame++
            if (mFpsCountFrame == NUM_FRAMES) {
                // compute thousands of frames per second
                val elapsed = timestampsNanos - mFpsCountStartNanos
                mActivityHandler.sendFpsUpdate((NUM_FRAMES * ONE_TRILLION / elapsed).toInt(), mDroppedFrames)
                // reset
                mFpsCountStartNanos = timestampsNanos
                mFpsCountFrame = 0
            }
        }


    }

    fun shutDown() {
        Log.d(TAG, "shutdown")
        Looper.myLooper()?.quit()
    }

    private fun releaseGL() {
        GLUtils.checkGlError("releaseGl start")
        mWindowSurface.release()
        mEglCore.makeNothingCurrent()
    }

    private fun update(timestampsNanos: Long) {

    }

    private fun draw() {
        GLUtils.checkGlError("draw start")
        // Clear to a non-black color to make the content easily differentiable from
        // the pillar-/letter-boxing.
        GLES20.glClearColor(0f, 0f, 0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        mSurfaceTexture.getTransformMatrix(filter.transformMatrix)
        mSurfaceTexture.updateTexImage()
        filter.drawFrame()
    }

}