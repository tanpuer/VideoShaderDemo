package com.example.templechen.videoshaderdemo.gl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.templechen.videoshaderdemo.R
import com.example.templechen.videoshaderdemo.filter.FilterListUtil
import com.example.templechen.videoshaderdemo.player.ExoPlayerTool

class SimpleGLActivity : AppCompatActivity(), ExoPlayerTool.IVideoListener, SurfaceHolder.Callback {

    companion object {
        private const val TAG = "SimpleGLActivity"
        private val LIST = FilterListUtil.LIST
    }

    private lateinit var simpleGLSurfaceView: SimpleGLSurfaceView
    private lateinit var parentView: RelativeLayout
    private lateinit var mPlayer: ExoPlayerTool
    private lateinit var mActivityHandler: ActivityHandler
    private lateinit var fpsView: TextView
    private lateinit var glVersionView: TextView
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private var isRecording = false
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var anotherSurfaceView: SurfaceView
    private lateinit var anotherSurface: Surface
    private var renderAnotherSurfaceEnable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentView = RelativeLayout(this)
        parentView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(parentView)

        mActivityHandler = ActivityHandler(this)
        mPlayer = ExoPlayerTool.getInstance(applicationContext)
        mPlayer.quickSetting(
            this,
            "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
        )
        simpleGLSurfaceView = SimpleGLSurfaceView(this, mPlayer, mActivityHandler)
        val params =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        parentView.addView(simpleGLSurfaceView, params)
        mPlayer.addVideoListener(this)

        //fps view
        fpsView = TextView(this)
        val fpsViewParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        fpsView.textSize = 16f
        fpsView.setTextColor(Color.BLACK)
        fpsViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        parentView.addView(fpsView, fpsViewParams)

        //glVersion view
        glVersionView = TextView(this)
        val glVersionViewParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        glVersionView.textSize = 16f
        glVersionView.setTextColor(Color.BLACK)
        //貌似不生效啊
//        glVersionViewParams.addRule(RelativeLayout.BELOW, fpsView.id)
        glVersionViewParams.topMargin = 100
        parentView.addView(glVersionView, glVersionViewParams)

        //start stop btn
        startBtn = Button(this)
        val startBtnLayoutParams = RelativeLayout.LayoutParams(300, 150)
        startBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        startBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        startBtn.text = "start"
        parentView.addView(startBtn, startBtnLayoutParams)
        startBtn.setOnClickListener {
            isRecording = true
            simpleGLSurfaceView.startRecording()
        }

        stopBtn = Button(this)
        val stopBtnLayoutParams = RelativeLayout.LayoutParams(300, 150)
        stopBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        stopBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        stopBtn.text = "stop"
        parentView.addView(stopBtn, stopBtnLayoutParams)
        stopBtn.setOnClickListener {
            isRecording = false
            simpleGLSurfaceView.stopRecording()
        }

        //filter RecyclerView
        filterRecyclerView = RecyclerView(this)
        val filterLayoutParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        filterLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        filterLayoutParams.bottomMargin = 200
        filterRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filterRecyclerView.adapter = FilterAdapter(this, LIST)
        filterRecyclerView.setBackgroundColor(Color.GRAY)
        parentView.addView(filterRecyclerView, filterLayoutParams)

        //another surface
        anotherSurfaceView = SurfaceView(this)
        val anotherLayoutParams = RelativeLayout.LayoutParams(360, 640)
        anotherLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        parentView.addView(anotherSurfaceView, anotherLayoutParams)
        anotherSurfaceView.holder.addCallback(this)
        anotherSurfaceView.setOnClickListener {
            if (!renderAnotherSurfaceEnable) {
                simpleGLSurfaceView.renderAnotherSurface(anotherSurface)
            } else {
                simpleGLSurfaceView.stopRenderAnotherSurface()
            }
            renderAnotherSurfaceEnable = !renderAnotherSurfaceEnable
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        anotherSurface = holder!!.surface
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        val params = simpleGLSurfaceView.layoutParams
        val viewWidth = simpleGLSurfaceView.width
        val viewHeight = simpleGLSurfaceView.height
        val ratio = viewWidth * 1.0f / viewHeight
        val videoRatio = width * 1.0f / height
        if (ratio > videoRatio) {
            params.width = (viewHeight * videoRatio).toInt()
        } else {
            params.height = (viewWidth / videoRatio).toInt()
        }
        simpleGLSurfaceView.layoutParams = params
    }

    override fun onPause() {
        super.onPause()
        mPlayer.setPlayWhenReady(false)
        if (isRecording) {
            isRecording = false
            simpleGLSurfaceView.stopRecording()
        }
    }

    override fun onResume() {
        super.onResume()
        mPlayer.setPlayWhenReady(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

    fun updateFps(tfps: Int, dropped: Int) {
        fpsView.text = "Frame rate: ${tfps / 1000.0f}fps (${dropped} dropped)"
    }

    fun updateGLVersion(version: Int) {
        glVersionView.text = "GLES Version: ${version}"
    }

    class FilterAdapter(context: Context, list: List<String>) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

        private var mList = list
        private var mContext = context

        override fun onCreateViewHolder(parent: ViewGroup, pos: Int): FilterViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_filter, parent, false)
            return FilterViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onBindViewHolder(filterViewHolder: FilterViewHolder, pos: Int) {
            filterViewHolder.textView.text = mList[pos]
            filterViewHolder.textView.setOnClickListener {
                (mContext as SimpleGLActivity).simpleGLSurfaceView.changeFilter(pos)
                (mContext as SimpleGLActivity).simpleGLSurfaceView.filterType = pos
            }
        }

        class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var textView: Button = itemView.findViewById(R.id.text)

        }
    }

}