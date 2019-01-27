package com.example.templechen.videoshaderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: VideoGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = VideoGLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(3)
        setContentView(glSurfaceView)
        glSurfaceView.init(this, false)
    }

}
