package com.convert.mymediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class Player01Activity : AppCompatActivity() ,  SeekBar.OnSeekBarChangeListener{

    private var play: Button? = null;
    private  var pause:Button? = null;
    private  var stop:Button? = null
    private var player: MediaPlayer? = null
    private var mSeekBar: SeekBar? = null
    private var tv: TextView? = null;
    private  var tv2:TextView? = null
    private var hadDestroy = false
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0x01 -> {
                }
                else -> {
                }
            }
        }
    }
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (!hadDestroy) {
                mHandler.postDelayed(this, 1000)
                val currentTime = (player?.getCurrentPosition()?.div(1000))?.let {
                    Math.round(it.toDouble()).toInt()
                }
                val currentStr = String.format("%s%02d:%02d", "当前时间 ",
                        currentTime?.div(60), currentTime?.rem(60))
                tv!!.text = currentStr
                if (player != null) {
                    mSeekBar!!.progress = player!!.getCurrentPosition()
                    Log.d("ddebug","mSeekBar!!.progress = player!!.getCurrentPosition")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player01)
        play = findViewById<View>(R.id.play) as Button
        pause = findViewById<View>(R.id.pause) as Button
        stop = findViewById<View>(R.id.stop) as Button
        mSeekBar = findViewById<View>(R.id.seekbar) as SeekBar
        tv = findViewById<View>(R.id.tv) as TextView
        tv2 = findViewById<View>(R.id.tv2) as TextView
        mSeekBar!!.setOnSeekBarChangeListener(this)
        player = MediaPlayer()
        initMediaplayer()
        val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tmp/y03.mp3"
        Log.d("ddebug","path = $path  ${File(path).exists()}")
    }

    /**
     * 初始化播放器
     */
    private fun initMediaplayer() {
        try {
//            val file = File(Environment.getExternalStorageDirectory()
//                    .toString() + "/Download/", "aiqiu.mp3")
            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tmp/y03.mp3"
            val file = File(path)
            Log.d("ddebug","---file.getPath---${file.getPath()}")
            player?.setDataSource(file.getPath())
            Log.e("播放器", file.toString())
            player!!.prepareAsync();
            player!!.setOnPreparedListener(object : MediaPlayer.OnPreparedListener{
                override fun onPrepared(mp: MediaPlayer?) {
                    Log.d("ddebug","---onPrepared---")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(view: View) {
        player?.let {
            it.start()

            val totalTime = Math.round((it.duration / 1000).toFloat())
            val str = String.format("%02d:%02d", totalTime / 60,
                    totalTime % 60)
            tv2!!.text = str
            mSeekBar!!.max = it.duration
            mHandler.postDelayed(runnable, 1000)
        }
    }
    fun pause(view: View) {
        player?.let {
            it.pause();
        }
    }
    fun stop(view: View) {
        player?.reset()
        initMediaplayer()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        Log.d("ddebug","---onProgressChanged---fromUser={$fromUser}")
        if (seekBar != null && fromUser) {
            player?.seekTo(seekBar.getProgress())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        //自动生成的方法存根
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        //自动生成的方法存根
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop();
        hadDestroy = true;
        player?.release();
    }




}