package com.example.templechen.videoshaderdemo.offscreen

import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.io.File
import java.io.FileNotFoundException
import java.lang.RuntimeException

class VideoDecoder(file: File) {

    companion object {
        private const val TAG = "VideoDecoder"
    }

    private var mFile = file
    var mOutputSurface: Surface? = null
    private var mMediaExtractor = MediaExtractor()
    private var mVideoTrack = -1
    private var mMediaFormat: MediaFormat
    var mVideoWidth = -1
    var mVideoHeight = -1
    private lateinit var mMediaCodec: MediaCodec
    var mFrameCallback: FrameCallback? = null

    init {
        mMediaExtractor.setDataSource(file.toString())
        val numTracks = mMediaExtractor.trackCount
        for (i in 0..numTracks) {
            val mediaFormat = mMediaExtractor.getTrackFormat(i)
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mime.startsWith("video/")) {
                mVideoTrack = i
                break
            }
        }
        if (mVideoTrack == -1) {
            throw RuntimeException("file contains no video track, please check")
        }
        mMediaExtractor.selectTrack(mVideoTrack)
        mMediaFormat = mMediaExtractor.getTrackFormat(mVideoTrack)
        mVideoWidth = mMediaFormat.getInteger(MediaFormat.KEY_WIDTH)
        mVideoHeight = mMediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
    }


    fun decode() {
        if (!mFile.canRead()) {
            throw FileNotFoundException("video file not exist")
        }
        val codecName = MediaCodecList(MediaCodecList.ALL_CODECS).findDecoderForFormat(mMediaFormat)
        if (codecName == null) {
            throw RuntimeException("video can not be decoded by GPU")
        }
        mMediaCodec = MediaCodec.createDecoderByType(mMediaFormat.getString(MediaFormat.KEY_MIME))
        mMediaCodec.configure(mMediaFormat, mOutputSurface, null, 0)
        mMediaCodec.start()

        //begin
        var mBufferInfo = MediaCodec.BufferInfo()
        var inputDone = false
        var outputDone = false
        while (!inputDone) {
            mFrameCallback?.decodeFrameBegin()
            //feed more data to the decoder
            if (!outputDone) {
                val inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1)
                if (inputBufferIndex > 0) {
                    val inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex)
                    val chunkSize = mMediaExtractor.readSampleData(inputBuffer!!, 0)
                    if (chunkSize < 0) {
                        //end of stream
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        inputDone = true
                        Log.d(TAG, "sent input EOS")
                    } else {
                        if (mMediaExtractor.sampleTrackIndex != mVideoTrack) {
                            Log.w(
                                TAG, "WEIRD: got sample from track " +
                                        mMediaExtractor.sampleTrackIndex + ", expected " + mVideoTrack
                            )
                        }
                        val pts = mMediaExtractor.sampleTime
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, chunkSize, pts, 0)
                        mMediaExtractor.advance()
                    }
                } else {
                    Log.d(TAG, "input buffer not available")
                }
            }
            if (!outputDone) {
                val status = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 0)
                when {
                    status == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                        Log.d(TAG, "no output from decoder available")
                    }
                    status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        Log.d(TAG, "decoder output buffers changed");
                    }
                    status < 0 -> {
                        throw RuntimeException(
                            "unexpected result from decoder.dequeueOutputBuffer: " +
                                    status
                        )
                    }
                    else -> {
                        if (mBufferInfo.flags.and(MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            outputDone = true
                        }
                        mMediaCodec.releaseOutputBuffer(status, mBufferInfo.size != 0)
                        if (mBufferInfo.size > 0) {
                            mFrameCallback?.decodeOneFrame(mBufferInfo.presentationTimeUs)
                        }
                    }
                }
            }
        }
        mFrameCallback?.decodeFrameEnd()
        mMediaCodec.stop()
        mMediaCodec.release()
        mMediaExtractor.release()
    }

    interface FrameCallback {
        fun decodeFrameBegin() {}
        fun decodeOneFrame(pts: Long) {}
        fun decodeFrameEnd() {}
    }

}