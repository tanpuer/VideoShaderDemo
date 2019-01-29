package com.example.templechen.videoshaderdemo.filter

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.templechen.videoshaderdemo.GLUtils
import com.example.templechen.videoshaderdemo.R
import java.nio.FloatBuffer

class WaterMarkFilter(context: Context, oesTextureId: Int) : BaseFilter(context, oesTextureId) {

    companion object {
        const val aWaterMarkPosition = "aWaterMarkPosition"
        const val uWaterMarkMatrix = "uWaterMarkMatrix"
        const val aWaterMarkTextureCoord = "aWaterMarkTextureCoord"
        const val uWaterMarkTextureSampler = "uWaterMarkTextureSampler"
    }

    private var waterMarkTextureId : Int = -1
    private lateinit var waterMarkFloatBuffer : FloatBuffer
    private var waterMarkVertexShader = -1
    private var waterMarkFragmentShader = -1
    private var waterMarkProgram = -1

    private var aWaterMarkPositionLocation = -1
    private var uWaterMarkMatrixLocation = -1
    private var aWaterMarkTextureCoordLocation = -1
    private var uWaterMarkTextureSamplerLocation = -1


    override fun initProgram() {
        super.initProgram()
        waterMarkTextureId = GLUtils.loadTexture(context, R.drawable.drawer_amino_logo)
        waterMarkFloatBuffer = GLUtils.createBuffer(GLUtils.waterMarkVertexData)
        waterMarkVertexShader = GLUtils.loadShader(GLES20.GL_VERTEX_SHADER, GLUtils.readShaderFromResource(context, R.raw.water_mark_vertex_shader))
        waterMarkFragmentShader = GLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, GLUtils.readShaderFromResource(context, R.raw.water_mark_fragment_shader))
        waterMarkProgram = GLUtils.createProgram(waterMarkVertexShader, waterMarkFragmentShader)
    }

    override fun drawFrame() {
        super.drawFrame()

        GLES20.glUseProgram(waterMarkProgram)
        //water mark
        aWaterMarkPositionLocation = GLES20.glGetAttribLocation(waterMarkProgram, aWaterMarkPosition)
        uWaterMarkMatrixLocation = GLES20.glGetUniformLocation(waterMarkProgram, uWaterMarkMatrix)
        aWaterMarkTextureCoordLocation = GLES20.glGetAttribLocation(waterMarkProgram, aWaterMarkTextureCoord)
        uWaterMarkTextureSamplerLocation = GLES20.glGetUniformLocation(waterMarkProgram, uWaterMarkTextureSampler)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterMarkTextureId)

        GLES20.glUniform1i(uWaterMarkTextureSamplerLocation, 0)
        GLES20.glUniformMatrix4fv(uWaterMarkMatrixLocation, 1, false, transformMatrix, 0)

        waterMarkFloatBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aWaterMarkPositionLocation)
        GLES20.glVertexAttribPointer(aWaterMarkPositionLocation, 2, GLES20.GL_FLOAT, false, 16, waterMarkFloatBuffer)
        waterMarkFloatBuffer.position(2)
        GLES20.glEnableVertexAttribArray(aWaterMarkTextureCoordLocation)
        GLES20.glVertexAttribPointer(aWaterMarkTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 16, waterMarkFloatBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        GLES20.glDisableVertexAttribArray(aWaterMarkPositionLocation)
        GLES20.glDisableVertexAttribArray(aWaterMarkTextureCoordLocation)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    }
}