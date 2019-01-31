package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R

class GlassSphereFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        private const val uCenter = "uCenter"
        private const val uRadius = "uRadius"
        private const val uAspectRatio = "uAspectRatio"
        private const val uRefractiveIndex = "uRefractiveIndex"

        private val center = floatArrayOf(0.5f, 0.5f)
        private const val radius = 0.5f
        private const val aspectRatio = 1.0f
        private const val refractiveIndex = 0.7f
    }

    private var uCenterLocation = -1
    private var uRadiusLocation = -1
    private var uAspectRatioLocation = -1
    private var uRefractiveIndexLocation = -1

    override fun initProgram() {
        vertexShader = GLUtils.loadShader(
            GLES20.GL_VERTEX_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.glass_sphere_vertex_shader)
        )
        fragmentShader = GLUtils.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            GLUtils.readShaderFromResource(context, R.raw.glass_sphere_fragment_shader)
        )
        program = GLUtils.createProgram(vertexShader, fragmentShader)
    }

    override fun drawFrame() {
        uCenterLocation = GLES20.glGetUniformLocation(program, uCenter)
        uRadiusLocation = GLES20.glGetUniformLocation(program, uRadius)
        uAspectRatioLocation = GLES20.glGetUniformLocation(program, uAspectRatio)
        uRefractiveIndexLocation = GLES20.glGetUniformLocation(program, uRefractiveIndex)

        GLES20.glUniform2fv(uCenterLocation, 1, center, 0)
        GLES20.glUniform1f(uRadiusLocation, radius)
        GLES20.glUniform1f(uAspectRatioLocation, aspectRatio)
        GLES20.glUniform1f(uRefractiveIndexLocation, refractiveIndex)

        super.drawFrame()
    }
}