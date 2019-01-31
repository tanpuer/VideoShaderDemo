package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class GaussianBlurFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(GLES20.GL_VERTEX_SHADER, GLUtils.readShaderFromResource(context, R.raw.base_vertex_shader))
        fragmentShader = GLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, GLUtils.readShaderFromResource(context, R.raw.gaussian_blur_fragment_shader))
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        super.drawFrame()

    }
}