package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class VibranceFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uVibrance = "uVibrance"
        private const val vibrance = 0.5f
    }

    private var uVibranceLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES20.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.vibrance_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.vibrance_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uVibranceLocation = GLES20.glGetUniformLocation(program, uVibrance)
        GLES20.glUniform1f(uVibranceLocation, vibrance)
        super.drawFrame()
    }
}