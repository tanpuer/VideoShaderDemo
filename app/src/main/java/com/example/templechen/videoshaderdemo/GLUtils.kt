package com.example.templechen.videoshaderdemo

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class GLUtils {

    companion object {

        fun createOESTextureObject(): Int {
            var tex = IntArray(1)
            GLES20.glGenTextures(1, tex, 0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            return tex[0]
        }

        fun readShaderFromResource(context: Context, resId: Int): String {
            var builder = StringBuilder()
            var inputStream: InputStream? = null
            var inputStreamReader: InputStreamReader? = null
            var bufferedReader: BufferedReader? = null

            try {
                inputStream = context.resources.openRawResource(resId)
                inputStreamReader = InputStreamReader(inputStream)
                bufferedReader = BufferedReader(inputStreamReader)
                var line: String? = bufferedReader.readLine()
                while (line != null && line.isNotEmpty()) {
                    builder.append(line).append("\n")
                    line = bufferedReader.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                inputStreamReader?.close()
                bufferedReader?.close()
            }
            return builder.toString()
        }

        fun createBuffer(vertexData: FloatArray): FloatBuffer {
            val floatBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            floatBuffer.put(vertexData, 0, vertexData.size).position(0)
            return floatBuffer
        }

        fun loadShader(type: Int, shaderSource: String): Int {
            val shader = GLES20.glCreateShader(type)
            if (shader == 0) {
                throw RuntimeException("create shader failed $type")
            }
            GLES20.glShaderSource(shader, shaderSource)
            GLES20.glCompileShader(shader)
            val compiled = intArrayOf(0)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == GLES20.GL_FALSE) {
                Log.e("Shader Compile Error: ", GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
            }
            return shader
        }

        fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
            val program = GLES20.glCreateProgram()
            if (program == 0) {
                throw RuntimeException("create gl program failed")
            }
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
            val compiled = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, compiled, 0)
            if (compiled[0] == GLES20.GL_FALSE) {
                Log.e("Program Link Error: ", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program)
            }
            return program
        }

        val vertexData = floatArrayOf(
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
        )

        fun loadTexture(context: Context, resId: Int) :Int{
            val textureObjectIds = IntArray(1)
            GLES20.glGenTextures(1, textureObjectIds, 0)
            if (textureObjectIds[0] ==0){
                return 0
            }
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
            if (bitmap == null){
                GLES20.glDeleteTextures(1, textureObjectIds, 0)
                return 0
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
            bitmap.recycle()
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            return textureObjectIds[0]
        }

        val waterMarkVertexData = floatArrayOf(
            1f, 1f, 1f, 1f,
            0.7f, 1f, 0f, 1f,
            0.7f, 0.7f, 0f, 0f,
            1f, 1f, 1f, 1f,
            1f, 0.7f, 0f, 0f,
            0.7f, 0.7f, 1f, 0f
        )

    }

}