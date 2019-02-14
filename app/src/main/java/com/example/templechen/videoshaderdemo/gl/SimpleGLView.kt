package com.example.templechen.videoshaderdemo.gl

import android.view.Surface
import android.view.View
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

interface SimpleGLView {

    fun initViews(activityHandler: ActivityHandler, playerTool: ExoPlayerTool)

    fun startRecording()

    fun stopRecording()

    fun changeFilter(type: Int)

    fun renderAnotherSurface(surface: Surface?)

    fun stopRenderAnotherSurface()

    fun getView() : View

}