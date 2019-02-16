package com.example.templechen.videoshaderdemo.gl.sticker

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

class StickerView : ImageView, View.OnTouchListener {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var mGestureDetector: GestureDetector = GestureDetector(context, SingleTapConfirm(this))
    private var onStickerViewClickListener: OnStickerViewClickListener? = null

    init {
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    private class SingleTapConfirm(stickerView: StickerView) : GestureDetector.SimpleOnGestureListener() {

        private var mStickerView = stickerView
        private var deltaX = 0f
        private var deltaY = 0f

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (e1 != null && e2 != null) {
                deltaX = e2.x - e1.x
                deltaY = e2.y - e1.y
            }
            mStickerView.layout(
                (mStickerView.left + deltaX).toInt(),
                (mStickerView.top + deltaY).toInt(),
                (mStickerView.right + deltaX).toInt(),
                (mStickerView.bottom + deltaY).toInt()
            )
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mStickerView.onStickerViewClickListener?.onStickerViewClicked(mStickerView)
            return true
        }
    }

    fun setOnStickerViewClickListener(onStickerViewClickListener: OnStickerViewClickListener) {
        this.onStickerViewClickListener = onStickerViewClickListener
    }

    interface OnStickerViewClickListener {
        fun onStickerViewClicked(stickerView: StickerView)
    }
}