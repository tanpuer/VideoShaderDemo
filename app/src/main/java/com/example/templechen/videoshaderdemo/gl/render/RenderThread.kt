package com.example.templechen.videoshaderdemo.gl.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.Matrix
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.filter.*
import com.example.templechen.videoshaderdemo.gl.ActivityHandler
import com.example.templechen.videoshaderdemo.gl.egl.EglCore
import com.example.templechen.videoshaderdemo.gl.egl.WindowSurface
import com.example.templechen.videoshaderdemo.gl.encoder.VideoEncoder
import com.example.templechen.videoshaderdemo.gl.encoder.VideoEncoderThread
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import java.io.File

class RenderThread(
    context: Context,
    surface: Surface,
    activityHandler: ActivityHandler,
    refreshPeriodsNs: Long,
    player: ExoPlayerTool,
    type: String
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
    private var mType = type

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

    //recording
    private var mOffscreenTexture = -1
    private var mFramebuffer = -1
    private var mDepthBuffer = -1
    private var recordingEnable = false
    private lateinit var mInputWindowSurface: WindowSurface
    private lateinit var mVideoEncoderThread: VideoEncoderThread

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

    fun surfaceCreated(type: Int) {
        prepareGL(mSurface, type)
    }

    private fun prepareGL(surface: Surface, type: Int) {
        Log.d(TAG, "prepareGl")
        mWindowSurface = WindowSurface(mEglCore, surface, false)
        mWindowSurface.makeCurrent()
        //custom program
        mOESTextureId = GLUtils.createOESTextureObject()
        mSurfaceTexture = SurfaceTexture(mOESTextureId)

        mType = FilterListUtil.LIST[type]
        setFilter(mType, mOESTextureId)

        //todo main thread
        mPlayer.setVideoSurface(Surface(mSurfaceTexture))
        mPlayer.setPlayWhenReady(true)

        GLES30.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        mActivityHandler.sendGLESVersion(mEglCore.glVersion)
    }

    fun surfaceChanged(width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged " + width + "x" + height)
//        prepareFrameBuffer(width, height)
        GLES30.glViewport(0, 0, width, height)
        Matrix.orthoM(mDisplayProjectionMatrix, 0, 0f, width.toFloat(), 0f, height.toFloat(), -1f, 1f)
    }

    /**
     * Prepares the off-screen framebuffer.
     */
    private fun prepareFrameBuffer(width: Int, height: Int) {
        GLUtils.checkGlError("prepareFramebuffer start")
        val values = intArrayOf(0)
        //create a texture object and bind it. This will be the color buffer
        GLES30.glGenTextures(1, values, 0)
        GLUtils.checkGlError("glGenTextures")
        mOffscreenTexture = values[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mOffscreenTexture)
        GLUtils.checkGlError("glBindTexture $mOffscreenTexture")
        //create texture storage
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_RGBA,
            width,
            height,
            0,
            GLES30.GL_RGBA,
            GLES30.GL_UNSIGNED_BYTE,
            null
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE
        )

        //create framebuffer and bind it
        GLES30.glGenFramebuffers(1, values, 0)
        mFramebuffer = values[0]
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFramebuffer)
        GLUtils.checkGlError("glBindFramebuffer $mFramebuffer")

        //create a depth buffer and bind it
        GLES30.glGenRenderbuffers(1, values, 0)
        mDepthBuffer = values[0]
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, mDepthBuffer)
        //allocate storage for the depth buffer
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, width, height)

        //attach the depth buffer and the texture(color buffer) to the framebuffer object
        GLES30.glFramebufferRenderbuffer(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_DEPTH_ATTACHMENT,
            GLES30.GL_RENDERBUFFER,
            mDepthBuffer
        )
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER,
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D,
            mOffscreenTexture,
            0
        )

        //see if GLES is happy with all this
        val status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER)
        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Framebuffer not complete, status=$status")
        }

        //switch back to the default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLUtils.checkGlError("prepareFramebuffer done")
    }

    private var filterNeedReset = false
    private var filterType = -1
    fun resetFilter(type: Int) {
        filterNeedReset = true
        filterType = type
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

        //reset
        if (filterNeedReset && filterType != -1) {
            filter.release()
            setFilter(FilterListUtil.LIST[filterType], mOESTextureId)
            filterNeedReset = false
            filterType = -1
        }

        // Render
        draw()

        //recording
        if (recordingEnable) {
            mVideoEncoderThread.frameAvailableSoon()
            mInputWindowSurface.makeCurrentReadFrom(mWindowSurface)
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            GLES30.glBlitFramebuffer(
                0,
                0,
                mWindowSurface.width,
                mWindowSurface.height,
                0,
                0,
                mWindowSurface.width,
                mWindowSurface.height,
                GLES30.GL_COLOR_BUFFER_BIT,
                GLES30.GL_NEAREST
            )
            val err = GLES30.glGetError()
            if (err != GLES30.GL_NO_ERROR) {
                Log.w(TAG, "ERROR: glBlitFramebuffer failed: 0x" + Integer.toHexString(err))
            }
            mInputWindowSurface.setPresentationTime(timestampsNanos)
            mInputWindowSurface.swapBuffers()

            mWindowSurface.makeCurrent()
        }

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

    fun startEncoder() {
        val BIT_RATE = 4000000
        val WIDTH = mWindowSurface.width
        val HEIGHT = mWindowSurface.height
        var outputFile = File(mContext.cacheDir, "gltest.mp4")
        if (outputFile.exists()) {
            outputFile.delete()
            outputFile = File(mContext.cacheDir, "gltest.mp4")
        }
        val encoder = VideoEncoder(WIDTH, HEIGHT, BIT_RATE, outputFile)
        mInputWindowSurface = WindowSurface(mEglCore, encoder.mInputSurface, true)
        mVideoEncoderThread = VideoEncoderThread(encoder)
        mVideoEncoderThread.start()
        mVideoEncoderThread.waitUntilReady()
        recordingEnable = true
    }

    fun stopEncoder() {
        recordingEnable = false
        mVideoEncoderThread.stopRecording()
        mInputWindowSurface.release()
//        mVideoEncoderThread.join()
    }

    private fun releaseGL() {
        GLUtils.checkGlError("releaseGl start")
        mWindowSurface.release()
        val values = intArrayOf(0)
        if (mOffscreenTexture > 0) {
            values[0] = mOffscreenTexture
            GLES30.glDeleteTextures(1, values, 0)
            mOffscreenTexture = -1
        }
        if (mFramebuffer > 0) {
            values[0] = mFramebuffer
            GLES30.glDeleteFramebuffers(1, values, 0)
            mFramebuffer = -1
        }
        if (mDepthBuffer > 0) {
            values[0] = mDepthBuffer
            GLES30.glDeleteRenderbuffers(1, values, 0)
            mDepthBuffer = -1
        }

        mEglCore.makeNothingCurrent()
    }

    private fun update(timestampsNanos: Long) {

    }

    private fun draw() {
        GLUtils.checkGlError("draw start")
        // Clear to a non-black color to make the content easily differentiable from
        // the pillar-/letter-boxing.
        GLES30.glClearColor(0f, 0f, 0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        mSurfaceTexture.getTransformMatrix(filter.transformMatrix)
        mSurfaceTexture.updateTexImage()
        filter.drawFrame()
    }

    private fun setFilter(type: String, mOESTextureId: Int) {
        filter = FilterListUtil.setFilter(type, mOESTextureId, mContext)
        filter.initProgram()
    }

}