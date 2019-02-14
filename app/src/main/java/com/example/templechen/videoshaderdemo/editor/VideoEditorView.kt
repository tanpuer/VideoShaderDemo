package com.example.templechen.videoshaderdemo.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class VideoEditorView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val PAINT_WIDTH = 20
    }

    private var mPaint: Paint = Paint()
    private var mRect = Rect()

    init {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = PAINT_WIDTH.toFloat()
        mPaint.style = Paint.Style.STROKE
        mRect.set(0, 0, width, height)
    }

    fun setSize(height: Float) {
        val lp = layoutParams
        val width = (height / 16f * 9f).toInt()
        layoutParams.width = width
        layoutParams.height = height.toInt()
        this.layoutParams = lp
//        mRect.set((width * 0.05).toInt(), (height * 0.05f).toInt(), (width * 0.95).toInt(), (height * 0.95).toInt())
        mRect.set(0, 0, width, height.toInt())
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawRect(mRect, mPaint)
    }
}