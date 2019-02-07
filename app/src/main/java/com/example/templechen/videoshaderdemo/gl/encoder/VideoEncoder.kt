package com.example.templechen.videoshaderdemo.gl.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.view.Surface
import java.io.File
import java.lang.RuntimeException

class VideoEncoder(val width: Int, val height: Int, val bitRate: Int, val outputFile: File) {

    companion object {
        const val MIME_TYPE = "video/avc"
        const val FRAME_RATE = 30
        const val I_FRAME_INTERVAL = 10
    }

    var mInputSurface: Surface?
    private var mMuxer: MediaMuxer
    private var mEncoder: MediaCodec
    private var mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
    private var format: MediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height)
    private var mTrackIndex = -1
    private var mMuxerStarted = false

    init {
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE)
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mInputSurface = mEncoder.createInputSurface()
        mEncoder.start()
        mMuxer = MediaMuxer(outputFile.toString(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        mTrackIndex = -1
        mMuxerStarted = false
    }

    fun release() {
        mEncoder.stop()
        mEncoder.release()
        mMuxer.stop()
        mMuxer.release()
    }

    fun drainEncoder(endOfStream: Boolean) {
        val TIMEOUT_USEC = 10000
        if (endOfStream) {
            mEncoder.signalEndOfInputStream()
        }
        var encoderOutputBuffers = mEncoder.outputBuffers
        while (true) {
            val encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC.toLong())
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = mEncoder.outputBuffers
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val mediaFormat = mEncoder.outputFormat
                mTrackIndex = mMuxer.addTrack(mediaFormat)
                mMuxer.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                //ignore
            } else {
                val encodedData = encoderOutputBuffers[encoderStatus]
                if (encodedData == null) {
                    throw RuntimeException("encoderOutputBuffer $encoderStatus is null")
                }
                if ((mBufferInfo.flags.and(MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)) {
                    mBufferInfo.size = 0
                }
                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset)
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size)
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo)

                }
                mEncoder.releaseOutputBuffer(encoderStatus, false)
                if ((mBufferInfo.flags.and(MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)) {
                    break     // out of while
                }
            }
        }
    }
}