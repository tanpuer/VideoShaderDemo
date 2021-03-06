package com.example.templechen.videoshaderdemo.offscreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.templechen.videoshaderdemo.R
import java.io.File

class OffScreenActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val PERMISSION_CODE = 1001
    }

    private lateinit var mStartBtn: Button
    private lateinit var mOffScreenRenderThread: OffScreenRenderThread
    private lateinit var mDurationText: TextView
    private lateinit var mOffscreenActivityHandler: OffScreenActivityHandler
    private var startTime = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offscreen)
        mStartBtn = findViewById(R.id.start_btn)
        mStartBtn.setOnClickListener(this)
        mDurationText = findViewById(R.id.duration_txt)

        if (Build.VERSION.SDK_INT >= 21 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
//            initRenderThread()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initRenderThread()
        } else {
            Toast.makeText(this, "must need write external storage permission!", Toast.LENGTH_LONG).show()
        }
    }

    fun setDuration() {
        mDurationText.text = "Duration : ${(System.currentTimeMillis() - startTime) / 1000f}"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start_btn -> {
                initRenderThread()
                mOffScreenRenderThread.mRenderHandler.startOffscreenRender()
                startTime = System.currentTimeMillis()
            }
        }
    }

    private fun initRenderThread() {
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/trailer.mp4")
        if (!file.exists()) {
            Toast.makeText(this, "no mp4 in sdcard, please check", Toast.LENGTH_LONG).show()
            return
        }
        mOffscreenActivityHandler = OffScreenActivityHandler(this)
        mOffScreenRenderThread = OffScreenRenderThread(
            this,
            file,
            mOffscreenActivityHandler
        )
        mOffScreenRenderThread.start()
        mOffScreenRenderThread.waitUntilReady()
        mOffScreenRenderThread.mRenderHandler.prepareOffscreenRender()
    }
}