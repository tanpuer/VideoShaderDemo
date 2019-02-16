package com.example.templechen.videoshaderdemo.gl.sticker

import android.graphics.Bitmap
import android.view.View

class StickerUtil {

    companion object {

        var bitmap: Bitmap? = null

        fun convertViewToBitmap(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            bitmap = Bitmap.createBitmap(view.getDrawingCache())
            view.isDrawingCacheEnabled = false
            return bitmap!!
        }
    }
}