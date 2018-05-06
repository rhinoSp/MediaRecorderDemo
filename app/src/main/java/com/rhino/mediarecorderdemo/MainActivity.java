package com.rhino.mediarecorderdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rhino.mediarecorderdemo.utils.MediaPlayerUtils;
import com.rhino.mediarecorderdemo.utils.MediaRecorderUtils;
import com.rhino.mediarecorderdemo.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private String mRecordFilePath;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtils.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);

        findViewById(R.id.start_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaRecorderUtils.getInstance().startRecord();
            }
        });
        findViewById(R.id.stop_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaRecorderUtils.getInstance().stopRecord();
            }
        });
        findViewById(R.id.start_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayerUtils.getInstance().startPlay(mRecordFilePath);
                mCountDownTimer = new CountDownTimer(MediaPlayerUtils.getMediaDuration(getApplicationContext(), mRecordFilePath),
                        1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        ((TextView)findViewById(R.id.duration)).setText(MediaPlayerUtils.formatSeconds((int) (millisUntilFinished/1000)));
                    }

                    @Override
                    public void onFinish() {
                        ((TextView)findViewById(R.id.duration)).setText(MediaPlayerUtils.formatSeconds(0));
                    }
                };
                mCountDownTimer.start();
            }
        });
        findViewById(R.id.stop_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayerUtils.getInstance().stopPlay();
                if (null != mCountDownTimer) {
                    mCountDownTimer.cancel();
                }
            }
        });

        MediaRecorderUtils.getInstance().setOnMediaRecorderListener(new MediaRecorderUtils.OnMediaRecorderListener() {
            @Override
            public void onUpdate(double db, long time) {
                ((TextView)findViewById(R.id.duration)).setText("db: " + db + ", time: "
                        + MediaPlayerUtils.formatSeconds((int) (time/1000)));
            }

            @Override
            public void onStop(String filePath, long duration) {
                mRecordFilePath = filePath;
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION_CODE == requestCode) {
            if (PermissionUtils.checkPermissionsGrantResults(grantResults)) {
                Toast.makeText(getApplicationContext(), "已获得所有权限!", Toast.LENGTH_SHORT).show();
            } else {
                PermissionUtils.showMissingPermissionDialog(this, false, "提示",
                        "当前应用缺少必要权限，请点击 “设置” - “权限管理” 打开所需权限。",
                        "设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtils.gotToAppSettings(MainActivity.this);
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
            }
        }
    }
}
