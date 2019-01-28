package com.example.templechen.videoshaderdemo

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.example.templechen.videoshaderdemo.filter.BaseFilter
import com.example.templechen.videoshaderdemo.filter.FourPartFilter
import com.example.templechen.videoshaderdemo.filter.GrayFilter
import com.example.templechen.videoshaderdemo.filter.WaterMarkFilter
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VideoGLRenderer : GLSurfaceView.Renderer {

    private var isPreviewStarted = false
    private var mOESTextureId = -1
    private lateinit var mSurfaceTexture: SurfaceTexture
    private lateinit var context: Context
    private lateinit var videoGLSurfaceView: VideoGLSurfaceView
    private lateinit var filter: BaseFilter
    private var transformMatrix = FloatArray(16) { 0f }
    private lateinit var handler: android.os.Handler
    private lateinit var mPlayer: ExoPlayerTool

    fun init(context: Context, videoGLSurfaceView: VideoGLSurfaceView, isPreviewStarted: Boolean) {
        this.isPreviewStarted = isPreviewStarted
        this.context = context
        this.videoGLSurfaceView = videoGLSurfaceView
        handler = Handler(Looper.getMainLooper())
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        mOESTextureId = GLUtils.createOESTextureObject()
//        filter = BaseFilter(context, mOESTextureId)
//        filter = GrayFilter(context, mOESTextureId)
//        filter = FourPartFilter(context, mOESTextureId)
        filter = WaterMarkFilter(context, mOESTextureId)
        filter.initProgram()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onDrawFrame(gl: GL10?) {
        if (!isPreviewStarted) {
            isPreviewStarted = initSurfaceTexture()
            return
        }
        mSurfaceTexture.updateTexImage()
        mSurfaceTexture.getTransformMatrix(transformMatrix)
        filter.transformMatrix = transformMatrix
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        filter.drawFrame()
    }

    private fun initSurfaceTexture(): Boolean {
        mSurfaceTexture = SurfaceTexture(mOESTextureId)
        mSurfaceTexture.setOnFrameAvailableListener {
            videoGLSurfaceView.requestRender()
        }
        handler.post {
            mPlayer = ExoPlayerTool.getInstance(context)
            mPlayer.setVideoSurface(Surface(mSurfaceTexture))
            mPlayer.setPlayWhenReady(true)
        }
        return true
    }

    fun destroy() {
        mSurfaceTexture.release()
        mOESTextureId = -1
        isPreviewStarted = false
    }
}