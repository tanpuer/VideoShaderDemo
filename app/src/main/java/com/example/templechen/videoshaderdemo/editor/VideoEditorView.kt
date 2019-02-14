package com.example.templechen.videoshaderdemo.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

class VideoEditorView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val PAINT_WIDTH = 20
    }

    private var mPaint: Paint = Paint()
    private var mRect = Rect()
    private var mVideoViewWidth = 0f

    init {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = PAINT_WIDTH.toFloat()
        mPaint.style = Paint.Style.STROKE
        mRect.set(0, 0, width, height)
    }

    fun setSize(videoViewWHeight: Float, videoViewWidth: Float) {
        mVideoViewWidth = videoViewWidth
        val lp = layoutParams as RelativeLayout.LayoutParams
        val width = (videoViewWHeight / 16f * 9f).toInt()
        lp.width = width
        lp.height = videoViewWHeight.toInt()
        val marginLeft = (videoViewWidth - width) / 2
        lp.leftMargin = marginLeft.toInt()
        layoutParams = lp
//        mRect.set((width * 0.05).toInt(), (height * 0.05f).toInt(), (width * 0.95).toInt(), (height * 0.95).toInt())
        mRect.set(0, 0, width, videoViewWHeight.toInt())

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawRect(mRect, mPaint)
    }

    private var startX = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                val lm = layoutParams as RelativeLayout.LayoutParams
                lm.leftMargin += (endX - startX).toInt()
                //left and right border
                if (lm.leftMargin < 0) {
                    lm.leftMargin = 0
                }
                if (lm.leftMargin > mVideoViewWidth - lm.width) {
                    lm.leftMargin = (mVideoViewWidth - lm.width).toInt()
                }
                layoutParams = lm
                startX = endX
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }
}