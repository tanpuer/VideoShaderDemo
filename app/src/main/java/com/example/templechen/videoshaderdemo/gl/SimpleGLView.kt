package com.example.templechen.videoshaderdemo.gl

import android.view.Surface
import android.view.View

interface SimpleGLView {

    fun startRecording()

    fun stopRecording()

    fun changeFilter(type: Int)

    fun renderAnotherSurface(surface: Surface?)

    fun stopRenderAnotherSurface()

    fun getView() : View

}