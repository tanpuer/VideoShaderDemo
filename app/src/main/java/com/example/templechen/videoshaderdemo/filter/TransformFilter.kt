package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.Matrix

class TransformFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    private var matrix = FloatArray(16) {0f}
    private var degree = 0

    override fun drawFrame() {
        initMatrix()
        Matrix.multiplyMM(transformMatrix, 0, matrix, 0, transformMatrix, 0)
        super.drawFrame()
    }

    private fun initMatrix() {
        if (degree >= 360) {
            degree = 0
        }
        Matrix.setIdentityM(matrix, 0)
        Matrix.rotateM(matrix, 0, degree.toFloat(), 0f, 0f, 1f)
        degree += 1
    }

}