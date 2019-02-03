package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class SobelEdgeDetectionFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uTexelWidth = "uTexelWidth"
        private const val uTexelHeight = "uTexelHeight"

        private const val texelWidth = 0.001f
        private const val texelHeight = 0.001f
    }

    private var uTexelWidthLocation = -1
    private var uTexelHeightLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES20.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.three_x_three_texture_sampling_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.sobel_edge_detection_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uTexelWidthLocation = GLES20.glGetUniformLocation(program, uTexelWidth)
        GLES20.glUniform1f(uTexelHeightLocation, texelWidth)
        uTexelHeightLocation = GLES20.glGetUniformLocation(program, uTexelHeight)
        GLES20.glUniform1f(uTexelHeightLocation, texelHeight)
        super.drawFrame()
    }
}