package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class PixelationFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uImageWidthFactor = "uImageWidthFactor"
        private const val uImageHeightFactor = "uImageHeightFactor"
        private const val uPixel = "uPixel"

        private const val imageWidthFactor = 0.02f
        private const val imageHeightFactor = 0.02f
        private const val pixel = 1f
    }

    private var uImageWidthFactorLocation = -1
    private var uImageHeightFactorLocation = -1
    private var uPixelLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES20.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.pixelation_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.pixelation_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uImageWidthFactorLocation = GLES20.glGetUniformLocation(program, uImageWidthFactor)
        GLES20.glUniform1f(uImageWidthFactorLocation, imageWidthFactor)
        uImageHeightFactorLocation = GLES20.glGetUniformLocation(program, uImageHeightFactor)
        GLES20.glUniform1f(uImageHeightFactorLocation, imageHeightFactor)
        uPixelLocation = GLES20.glGetUniformLocation(program, uPixel)
        GLES20.glUniform1f(uPixelLocation, pixel)
        super.drawFrame()
    }
}