package com.rhino.mediarecorderdemo.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;

/**
 * @author LuoLin
 * @since Create on 2018/5/5.
 **/
public class MediaRecorderUtils {

    private static final String TAG = "MediaRecorderUtils";
    /**
     * 参考振幅值
     */
    private static final int BASE = 1;
    /**
     * 间隔取样时间
     */
    private static final int SPACE = 100;
    /**
     * 最大录音时长
     */
    private static final int MAX_DURATION = 1000 * 60 * 10;
    /**
     * 默认保存目录
     */
    private static final String DEFAULT_DIR_PATH = Environment.getExternalStorageDirectory() + "/record";
    /**
     * 默认名称
     */
    private static final String DEFAULT_FILE_NAME = System.currentTimeMillis() + ".amr";
    /**
     * 文件路径
     */
    private String mFilePath;
    /**
     * MediaRecorder对象
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 是否正在录音
     */
    private boolean mIsRecording = false;
    /**
     * 监听事件
     */
    private OnMediaRecorderListener mOnMediaRecorderListener;

    private static MediaRecorderUtils mInstance;
    public static MediaRecorderUtils getInstance() {
        if (null == mInstance) {
            mInstance = new MediaRecorderUtils();
        }
        return mInstance;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateRecordRunnable = new Runnable() {
        public void run() {
            updateRecordStatus();
        }
    };

    public MediaRecorderUtils() {
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        startRecord(MAX_DURATION, DEFAULT_DIR_PATH, DEFAULT_FILE_NAME);
    }

    /**
     * 开始录音
     *
     * @param maxDuration  最大长度毫秒
     * @param dirPath  保存目录
     * @param fileName 文件名称
     */
    public void startRecord(int maxDuration, String dirPath, String fileName) {
        this.mFilePath = dirPath + File.separator + fileName;
        this.mMediaRecorder = new MediaRecorder();
        try {
            new File(dirPath).mkdirs();
            /*第1步：设置音频来源（MIC表示麦克风）*/
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            /*第2步：设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * 第3步：设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            /* 第4步：准备*/
            mMediaRecorder.setOutputFile(mFilePath);
            mMediaRecorder.setMaxDuration(maxDuration);
            mMediaRecorder.prepare();
            /* 第5步：开始*/
            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            updateRecordStatus();
            mIsRecording = true;
            Log.d(TAG, "startRecord: startTime = " + startTime);
        } catch (Exception e) {
            Log.e(TAG, "startRecord: failed!" + e.getMessage());
            mFilePath = null;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mHandler.removeCallbacks(mUpdateRecordRunnable);
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        try {
            Log.d(TAG, "stopRecord: endTime = " + endTime + ", mFilePath = " + mFilePath);
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if (null != mOnMediaRecorderListener) {
                mOnMediaRecorderListener.onStop(mFilePath, duration);
            }
        } catch (Exception e) {
            Log.e(TAG, "stopRecord: failed!" + e.getMessage());
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mIsRecording = false;
    }

    /**
     * 更新录音状态
     */
    private void updateRecordStatus() {
        if (null != mMediaRecorder) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            if (ratio > 1) {
                double db = 20 * Math.log10(ratio);
                long time = System.currentTimeMillis() - startTime;
                Log.d(TAG, "updateRecordStatus: db = " + db + ", time = " + time);
                if (null != mOnMediaRecorderListener) {
                    mOnMediaRecorderListener.onUpdate(db, time);
                }
            }
            mHandler.postDelayed(mUpdateRecordRunnable, SPACE);
        }
    }

    /**
     * 是否正在录音
     *
     * @return 正在录音
     */
    public boolean isRecording() {
        return mIsRecording;
    }

    /**
     * 设置正在录音
     *
     * @param recording 正在录音
     */
    public void setRecording(boolean recording) {
        this.mIsRecording = recording;
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setOnMediaRecorderListener(OnMediaRecorderListener listener) {
        this.mOnMediaRecorderListener = listener;
    }

    public interface OnMediaRecorderListener {
        /**
         * 更新录音状态
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 文件路径
         * @param duration 录音时长
         */
        void onStop(String filePath, long duration);
    }

}