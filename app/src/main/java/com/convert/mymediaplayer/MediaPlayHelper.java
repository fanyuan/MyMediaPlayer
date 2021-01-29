package com.convert.mymediaplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.IOException;

public class MediaPlayHelper {
    private static Handler mHandler; //= new Handler(Looper.getMainLooper());
    /**
     * 播放器
     */
    private static MediaPlayer player;

    private static boolean hadDestroy = false;

    private static Runnable updateTask ;

    private static PlayCallback playCallback;
    /**
     * 是否已准备就绪
     */
    private static boolean isPrepared = false;
    /**
     * 当前播放源路径
     */
    private static String currentSourcePath;

    /**
     * 初始化方法
     */
    public static void init(PlayCallback callback) throws IOException {
        init(null,callback);
    }
    public static void init(String path,PlayCallback callback) throws IOException {
        init(path,callback,true);
    }
    private static void init(String path,PlayCallback callback,boolean isNotifyPrepared) throws IOException {
        hadDestroy = false;
        mHandler = new Handler(Looper.getMainLooper());
        playCallback = callback;
        player = new MediaPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                if(playCallback != null && isNotifyPrepared){
                    playCallback.onPrepared(mp);
                }
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(playCallback != null /*&& isNotifyPrepared*/){
                    playCallback.onCompletion(mp);
                }
            }
        });
        setDataSource(path);

        updateTask = new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    mHandler.postDelayed(this, 1000);
                    int currentPosition = player.getCurrentPosition();
                    if(playCallback != null){
                        playCallback.updatePosition(currentPosition);
                    }
                }
            }
        };
    }
    public static void setDataSource(String path) throws IOException {
        currentSourcePath = path;
        if(player != null && !TextUtils.isEmpty(path)){
            player.setDataSource(path);
            player.prepareAsync();
        }
    }
    public static void change(String path) throws IOException {
        stop();
        init(path,playCallback);
    }
    /**
     * 获取播放时长
     * @return
     */
    public static int getDuration(){
        if(player == null){
            return -1;
        }
        return player.getDuration();
    }

    /**
     * 播放
     */
    public static void play() {
        if(player == null){
            if(playCallback != null){
                playCallback.onError("播放前需要先初始化");
            }
            return;
        }

        if(!isPrepared){
            if(playCallback != null){
                playCallback.onError("还未准备就绪，请稍候再试");
            }
        }

        if(playCallback != null){
            playCallback.notifyDuration(player.getDuration());
        }

        player.start();

        mHandler.postDelayed(updateTask, 1000);
    }
    public static boolean isPlaying(){
        if(player != null){
            return player.isPlaying();
        }
        return false;
    }
    /**
     * 重置播放进度
     * @param progress
     */
    public static void seekTo(int progress){
        if(player == null){
            if(playCallback != null){
                playCallback.onError("播放器未初始化");
            }
            return;
        }

        player.seekTo(progress);
    }
    /**
     * 暂停
     */
    public static void pause() {
        if(player == null){
            if(playCallback != null){
                playCallback.onError("播放器未初始化");
            }
            return;
        }
        if(player.isPlaying()){
            player.pause();
        }
    }

    /**
     * 停止
     */
    public static void stop() throws IOException {
        if(player == null){
            if(playCallback != null){
                playCallback.onError("播放器未初始化");
            }
            return;
        }
        player.reset();
        isPrepared = false;
        init(currentSourcePath,playCallback,false);
    }
    /**
     * 释放资源功能
     */
    public static void release(){
        if (player != null) {
            player.stop();
            hadDestroy = true;
            player.release();
            player = null;
            playCallback = null;
            isPrepared = false;
        }
    }
    /**
     * 播放回调接口
     */
    public interface PlayCallback{
        /**
         * 更新播放进度
         * @param currentPosition
         */
        public void updatePosition(int currentPosition);

        /**
         * 准备工作完成回调
         * @param mp
         */
        public void onPrepared(MediaPlayer mp);

        /**
         * 播放时长通知
         * @param duration
         */
        public void notifyDuration(int duration);

        /**
         * 播放完毕回调
         * @param mp
         */
        public void onCompletion(MediaPlayer mp);

        /**
         * 错误情况通知
         * @param msg
         */
        public void onError(String msg);
    }
}
