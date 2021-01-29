package com.convert.mymediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class Player02ActivityByHelper : AppCompatActivity() , SeekBar.OnSeekBarChangeListener{

    private var mSeekBar: SeekBar? = null
    private var tv: TextView? = null;
    private  var tv2:TextView? = null

    private var displayTv:TextView? =null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player02)

        mSeekBar = findViewById<View>(R.id.seekbar) as SeekBar
        tv = findViewById<View>(R.id.tv) as TextView
        tv2 = findViewById<View>(R.id.tv2) as TextView
        mSeekBar!!.setOnSeekBarChangeListener(this)

        displayTv = findViewById(R.id.display_info)
        initPlay()
    }

    private fun initPlay() {
        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tmp/y03.mp3"
        path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tmp/y07.mp3"
        //path = "http://walking-test.oss-cn-shanghai.aliyuncs.com/poem/mp3/b0000003.mp3"
        path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tmp/test.mp3"
        //path = "http://walking-test.oss-cn-shanghai.aliyuncs.com/poem/mp3/y0000007.mp3"
        MediaPlayHelper.init(path, object : MediaPlayHelper.PlayCallback {
            override fun updatePosition(currentPosition: Int) {
                val currentTime = Math
                        .round(currentPosition.toFloat() / 1000).toInt()
                val currentStr = String.format("%s%02d:%02d", "当前时间 ",
                        currentTime / 60, currentTime % 60)
                tv!!.text = currentStr
                mSeekBar!!.progress = currentPosition
            }

            override fun onPrepared(mp: MediaPlayer?) {
                Log.d("ddebug", "Player02Activity --- onPrepared")
                MediaPlayHelper.play();
            }

            override fun notifyDuration(duration: Int) {
                val totalTime = Math.round((duration / 1000).toFloat())
                val str = String.format("%02d:%02d", totalTime / 60, totalTime % 60)
                tv2!!.text = str
                mSeekBar!!.max = duration
            }

            override fun onCompletion(mp: MediaPlayer?) {
            }

            override fun onError(msg: String?) {
                displayTv?.append("\n$msg")
                Log.d("ddebug", "Player02Activity --- onError---$msg")
            }

        })
    }

    fun play(view: View) {
        MediaPlayHelper.play();
    }
    fun pause(view: View) {
        MediaPlayHelper.pause();
    }
    fun stop(view: View) {
        MediaPlayHelper.stop();
        //MediaPlayHelper.play()
    }
    fun change(view:View){
        val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tmp/y07.mp3"
        //MediaPlayHelper.setDataSource(path)
        MediaPlayHelper.change(path)
    }
    override fun onDestroy() {
        super.onDestroy()
        MediaPlayHelper.release();
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar != null && fromUser) {
            MediaPlayHelper.seekTo(seekBar.getProgress())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}