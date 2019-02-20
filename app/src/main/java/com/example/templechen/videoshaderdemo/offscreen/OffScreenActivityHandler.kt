package com.example.templechen.videoshaderdemo.offscreen

import android.os.Handler
import android.os.Message

class OffScreenActivityHandler(offScreenActivity: OffScreenActivity) : Handler() {

    private val offScreenActivity = offScreenActivity

    companion object {
        const val MSG_OFF_SCREEN_END = 0
    }

    fun sendOffscreenEnd() {
        obtainMessage(MSG_OFF_SCREEN_END)
    }

    override fun handleMessage(msg: Message?) {
        if (msg == null) {
            return
        }
        when (msg.what) {
            MSG_OFF_SCREEN_END -> {
                offScreenActivity.setDuration()
            }
        }
    }
}