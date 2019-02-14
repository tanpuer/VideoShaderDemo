package com.example.templechen.videoshaderdemo.editor

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class VideoEditorView(context: Context) : View(context) {

    private var mPaint: Paint = Paint()

    init {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 5.0f
    }
}