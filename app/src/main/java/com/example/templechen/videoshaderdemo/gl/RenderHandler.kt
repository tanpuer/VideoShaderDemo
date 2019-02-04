package com.example.templechen.videoshaderdemo.gl

import android.os.Handler
import android.os.Message
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class RenderHandler(renderThread: RenderThread) : Handler() {

    companion object {
        const val MSG_SURFACE_CREATED = 0
        const val MSG_SURFACE_CHANGED = 1
        const val MSG_DO_FRAME = 2
        const val MSG_RECORDING_ENABLED = 3
        const val MSG_RECORD_METHOD = 4
        const val MSG_SHUTDOWN = 5
    }

    private var weakRenderThread: WeakReference<RenderThread> = WeakReference(renderThread)

    /**
     * Sends the "surface created" message.
     * <p>
     * Call from UI thread.
     */
    fun sendSurfaceCreated() {
        sendMessage(obtainMessage(MSG_SURFACE_CREATED))
    }

    /**
     * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
     * <p>
     * Call from UI thread.
     */
    fun sendSurfaceChanged(format: Int, width: Int, height: Int) {
        sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height))
    }

    /**
     * Sends the "do frame" message, forwarding the Choreographer event.
     * <p>
     * Call from UI thread.
     */
    fun sendDoFrame(frameTimeNanos: Long) {
        sendMessage(obtainMessage(MSG_DO_FRAME, frameTimeNanos.shr(32).toInt(), frameTimeNanos.toInt()))
    }

    /**
     * Enable or disable recording.
     * <p>
     * Call from non-UI thread.
     */
    fun setRecordingEnable(enable: Boolean) {
        sendMessage(obtainMessage(MSG_RECORDING_ENABLED, if (enable) 1 else 0, 1))
    }

    /**
     * Set the method used to render a frame for the encoder.
     * <p>
     * Call from non-UI thread.
     */
    fun setRecordMethod(method: Int) {
        sendMessage(obtainMessage(MSG_RECORD_METHOD, method, 0))
    }

    /**
     * Sends the "shutdown" message, which tells the render thread to halt.
     * <p>
     * Call from UI thread.
     */
    fun sendShutDown() {
        sendMessage(obtainMessage(MSG_SHUTDOWN))
    }

    override fun handleMessage(msg: Message?) {
        val what = msg?.what
        val renderThread = weakRenderThread.get() ?: return
        when (what) {
            MSG_SURFACE_CREATED -> {
                renderThread.surfaceCreated()
            }
            MSG_SURFACE_CHANGED -> {
                renderThread.surfaceChanged(msg.arg1, msg.arg2)
            }
            MSG_DO_FRAME -> {
                val timestamp: Long = msg.arg1.toLong().shl(32).or(msg.arg2.toLong().and(0xffffffffL))
                renderThread.doFrame(timestamp)
            }
            MSG_RECORDING_ENABLED -> {

            }
            MSG_RECORD_METHOD -> {

            }
            MSG_SHUTDOWN -> {
                renderThread.shutDown()
            }
            else -> throw IllegalArgumentException()
        }
    }

}