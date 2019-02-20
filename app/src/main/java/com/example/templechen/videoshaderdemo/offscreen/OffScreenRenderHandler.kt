package com.example.templechen.videoshaderdemo.offscreen

import android.os.Handler
import android.os.Message
import android.view.Surface
import java.lang.ref.WeakReference

class OffScreenRenderHandler(offScreenRenderThread: OffScreenRenderThread) : Handler() {

    companion object {
        const val MSG_START_OFFSCREEN_RENDER = 0
        const val MSG_PREPARE_OFFSCREEN_RENDER = 1
        const val MSG_SURFACE_CREATED = 2
    }

    private var weakOffScreenRenderThread = WeakReference<OffScreenRenderThread>(offScreenRenderThread)

    fun startOffscreenRender() {
        sendMessage(obtainMessage(MSG_START_OFFSCREEN_RENDER))
    }

    fun prepareOffscreenRender() {
        sendMessage(obtainMessage(MSG_PREPARE_OFFSCREEN_RENDER))
    }

    fun sendSurfaceCreate(surface: Surface) {
        sendMessage(obtainMessage(MSG_SURFACE_CREATED, surface))
    }

    override fun dispatchMessage(msg: Message?) {
        val offScreenRenderThread = weakOffScreenRenderThread.get() ?: return
        when (msg?.what) {
            MSG_START_OFFSCREEN_RENDER -> {
                offScreenRenderThread.renderFrame()
            }
            MSG_PREPARE_OFFSCREEN_RENDER -> {
                offScreenRenderThread.prepareGL()
            }
            MSG_SURFACE_CREATED -> {
                offScreenRenderThread.surfaceCreated(msg.obj as Surface)
            }
        }
    }

}