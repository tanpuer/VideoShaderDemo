package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class GrayFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(GLES20.GL_VERTEX_SHADER, GLUtils.readShaderFromResource(context, R.raw.gray_vertex_shader))
        fragmentShader = GLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, GLUtils.readShaderFromResource(context, R.raw.gray_fragment_shader))
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }


}