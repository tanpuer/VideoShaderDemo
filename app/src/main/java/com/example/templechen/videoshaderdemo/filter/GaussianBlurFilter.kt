package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class GaussianBlurFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uTexelOffset = "uTexelOffset"

        private val texelOffset = floatArrayOf(0.008f, 0.008f)
    }

    private var uTexelOffsetLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES20.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.gaussian_blur_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.gaussian_blur_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uTexelOffsetLocation = GLES20.glGetUniformLocation(program, uTexelOffset)
        GLES20.glUniform2fv(uTexelOffsetLocation, 1, texelOffset, 0)
        super.drawFrame()
    }
}