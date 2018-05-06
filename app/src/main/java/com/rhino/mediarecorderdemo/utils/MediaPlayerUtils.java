package com.rhino.mediarecorderdemo.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * @author LuoLin
 * @since Create on 2018/5/6.
 **/
public class MediaPlayerUtils {

    private static final String TAG = "MediaPlayerUtils";
    /**
     * 播放器
     */
    private MediaPlayer mMediaPlayer;
    /**
     * 是否正在播放
     */
    private boolean mIsPlaying = false;

    private static MediaPlayerUtils mInstance;
    public static MediaPlayerUtils getInstance() {
        if (null == mInstance) {
            mInstance = new MediaPlayerUtils();
        }
        return mInstance;
    }

    public MediaPlayerUtils() {
    }

    /**
     * 开始播放
     *
     * @param filePath           文件路径
     * @return true 成功播放
     */
    public boolean startPlay(String filePath) {
        return startPlay(filePath, false, null, null);
    }

    /**
     * 开始播放
     *
     * @param filePath           文件路径
     * @param isLooping          是否循环播放
     * @param completionListener 播放完成监听事件
     * @param preparedListener   准备播放事件
     * @return true 成功播放
     */
    public boolean startPlay(String filePath, boolean isLooping,
            MediaPlayer.OnCompletionListener completionListener,
            MediaPlayer.OnPreparedListener preparedListener) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setLooping(isLooping);
            if (null != completionListener) {
                mMediaPlayer.setOnCompletionListener(completionListener);
            }
            if (null != completionListener) {
                mMediaPlayer.setOnPreparedListener(preparedListener);
            }
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mIsPlaying = true;
        } catch (IOException e) {
            Log.e(TAG, "startPlay: " + e.toString());
            e.printStackTrace();
            mIsPlaying = false;
        }
        return mIsPlaying;
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mIsPlaying = false;
    }

    /**
     * 是否正在播放
     *
     * @return true 正在播放
     */
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * 设置是否正常播放
     *
     * @param playing true 正在播放
     */
    public void setPlaying(boolean playing) {
        this.mIsPlaying = playing;
    }

    /**
     * 获得音频长度
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return 音频长度
     */
    public static int getMediaDuration(Context context, String filePath) {
        int duration = 0;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file));
            if (null != mediaPlayer) {
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    duration = mediaPlayer.getDuration();
                }
            }
        }
        return duration;
    }

    /**
     * 转换秒为 02.03'04"格式
     *
     * @param seconds 秒
     * @return 02.03'04"格式 或 03'04"
     */
    public static String formatSeconds(int seconds) {
        int minutes = (seconds / 60) % 60;
        int hours = seconds / 3600;
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d.%02d\'%02d\"", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d\'%02d\"", minutes, seconds);
        }
    }

}
