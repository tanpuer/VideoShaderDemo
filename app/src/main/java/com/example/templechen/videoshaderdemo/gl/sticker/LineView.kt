package com.example.templechen.videoshaderdemo.gl.sticker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView : View {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = Color.RED
        setBackgroundColor(Color.RED)
    }

    var lineType: Int = NO_TYPE
        set(value) {
            field = value
            invalidate()
        }
    private var paint: Paint = Paint()


    companion object {
        const val NO_TYPE = 0
        const val LINE_TOP_LEFT_TYPE = 1
        const val LINE_TOP_RIGHT_TYPE = 2
        const val LINE_BOTTOM_LEFT_TYPE = 3
        const val LINE_BOTTOM_RIGHT_TYPE = 4
        const val LINE_CENTER_TYPE = 5
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
//        when (lineType) {
//            LINE_TOP_LEFT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), bottom.toFloat(),
//                        left.toFloat(), top.toFloat(),
//                        right.toFloat(), top.toFloat()
//                    ), paint
//                )
//            }
//            LINE_TOP_RIGHT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), top.toFloat(),
//                        right.toFloat(), top.toFloat(),
//                        right.toFloat(), bottom.toFloat()
//                    ), paint
//                )
//            }
//            LINE_BOTTOM_LEFT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), top.toFloat(),
//                        left.toFloat(), bottom.toFloat(),
//                        right.toFloat(), bottom.toFloat()
//                    ), paint
//                )
//            }
//            LINE_BOTTOM_RIGHT_TYPE -> {
//                canvas?.drawLines(
//                    floatArrayOf(
//                        left.toFloat(), bottom.toFloat(),
//                        right.toFloat(), bottom.toFloat(),
//                        right.toFloat(), top.toFloat()
//                    ), paint
//                )
//            }
//            LINE_CENTER_TYPE -> {
//                canvas?.drawLine(
//                    left.toFloat(),
//                    (top + bottom) / 2f,
//                    right.toFloat(),
//                    (top + bottom) / 2f,
//                    paint
//                )
//                canvas?.drawLine(
//                    (left + right) / 2f,
//                    top.toFloat(),
//                    (left + right) / 2f,
//                    bottom.toFloat(),
//                    paint
//                )
//                canvas?.drawRect(
//                    left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint
//                )
//            }
//        }
    }
}