package com.example.android.basicpermissions.microphone.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.android.basicpermissions.R;

import java.io.File;

/**
 * Desc:    MIC模块测试的fragment
 * Email:   neo.he@zeusis.com
 * Date:    2016-02-14 14:56
 */
public class MICFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MICFragment";
    //标识了进行过测试
    private int mOperateCount = 0;
    private int mMaxRecordDuration = 60 * 1000;
    private TextView mExplainMsg;
    private View mPlayBtn;
    private View mRecordBtn;
    private View mRecordStatus;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private boolean mIsRecording = false;
    private boolean mIsPlaying = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                mRecordBtn.setEnabled(true);

            if (msg.what == 1)
                mPlayBtn.setEnabled(true);
        }
    };

    /**
     * 录音文件名
     */
    private String mRecordeFileName = "audio_record_test_perm_file.mp3";
    private AudioManager mAudioManager;
    private int mDefaultVolume;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.microphone_fragment_module_mic, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecordeFileName = getContext().getFilesDir().getAbsolutePath() + File.separator + mRecordeFileName;
        Log.d(TAG, "mRecordeFileName = " + mRecordeFileName);

        mExplainMsg = (TextView) mView.findViewById(R.id.tv_explain_text);
        mRecordBtn = mView.findViewById(R.id.ic_record_view);
        mRecordStatus = mView.findViewById(R.id.ic_record_status);
        mPlayBtn = mView.findViewById(R.id.ic_paly_status);
        mRecordBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "defaultVolume = " + mDefaultVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeToRecordPrepared();
    }

    @Override
    public void onClick(View v) {
        //点击的是录音按钮
        if (v == mRecordBtn) {
            if (mIsRecording) { //在录音，点击后停止录音，变成可播放状态
                changeToPlayPrepared();
            } else { //不在录音，点击后开始录音，变成录音中状态
                if (getExternalStorageFreeSpace() < (15 * 1024 * 1024)) {
                    mExplainMsg.setText("空间不足");
                    return;
                } else {
                    changeToRecording();
                }
            }
            return;
        }

        //点击的是播放按钮
        if (v == mPlayBtn) {
            //播放中，点击后变成停止播放，进入可录音状态
            if (mIsPlaying) {
                changeToRecordPrepared();
            } else { //不在播放，点击后开始播放，变成播放中状态
                changeToPlaying();
            }
        }
    }

    /**
     * 跳转到可播放状态
     */
    private void changeToPlayPrepared() {
        mOperateCount++;
        if (mIsRecording) {
            mRecorder.reset();
            mRecorder.release();
        }

        mIsRecording = false;
        mRecordBtn.setVisibility(View.GONE);
        mRecordBtn.setEnabled(false);
        mPlayBtn.setVisibility(View.VISIBLE);
        mPlayBtn.setEnabled(true);
        mExplainMsg.setText("开始播放");
        mPlayBtn.setBackgroundResource(R.drawable.ic_play_circle_filled_white_24dp);
    }

    /**
     * 跳转到播放中状态
     */
    private void changeToPlaying() {
        mOperateCount++;
        //录音文件不存在，无法播放
        if (!new File(mRecordeFileName).exists()) {
            return;
        }
        mPlayer = initPlayer(getActivity(), mRecordeFileName);
        if (mPlayer == null) {
            Log.e(TAG, "创建播放器失败！");
            return;
        }
        mPlayer.start();
        mIsPlaying = true;
        mExplainMsg.setText("暂停播放");
        mPlayBtn.setBackgroundResource(R.drawable.ic_pause_circle_filled_white_24dp);
        mPlayBtn.setEnabled(false);
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 跳转到可录音状态
     */
    private void changeToRecordPrepared() {
        mOperateCount++;
        if (mIsPlaying) {
            mPlayer.stop();
            mPlayer.release();
        }
        mIsPlaying = false;
        mPlayBtn.setVisibility(View.GONE);
        mPlayBtn.setEnabled(false);
        mRecordBtn.setVisibility(View.VISIBLE);
        mRecordBtn.setEnabled(true);
        mExplainMsg.setText("开始录音");
        mRecordStatus.setBackgroundResource(R.drawable.ic_record_prepared_black_24dp);
    }

    /**
     * 跳转到录音中状态
     */
    private void changeToRecording() {
        mOperateCount++;
        //有录音文件存在，删除
        if (!isFileExistAndDelete(mRecordeFileName)) {
            return;
        }
        startRecord(mRecordeFileName);
        mExplainMsg.setText("结束录音");
        mRecordStatus.setBackgroundResource(R.drawable.ic_record_ing_black_24dp);
        mRecordBtn.setEnabled(false);
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 开始录音
     *
     * @param recordeFileName 录音存储的文件名 @nonull
     */
    private void startRecord(String recordeFileName) {
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mRecorder.setOutputFile(recordeFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setMaxDuration(mMaxRecordDuration);
            //监听，当达到最大值的时候，跳入准备播放状态
            mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        changeToPlayPrepared();
                    }
                }
            });

            mRecorder.prepare();
            mRecorder.start();
            mIsRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "start failed, " + e.getLocalizedMessage());
            Toast.makeText(getContext(), "start failed, " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mIsRecording = false;
        }
    }

    /**
     * 初始化 {@link MediaPlayer} 对象
     *
     * @param context  全局上下文
     * @param fileName 需要播放的音频文件名称
     * @return 初始化过的 {@link MediaPlayer} 对象
     */
    private MediaPlayer initPlayer(Context context, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Log.e(TAG, "文件不存在！");
            return null;
        }
        try {
            MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(file));
            player.setVolume(1f, 1f);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    changeToRecordPrepared();
                }
            });
            return player;
        } catch (Exception e) {
            Log.e(TAG, "创建播放器异常");
            return null;
        }
    }

    @Override
    public void onPause() {
        if (mRecorder != null && mIsRecording) {
            mRecorder.reset();
            mRecorder.release();
            mIsRecording = false;
        }

        if (mPlayer != null && mIsPlaying) {
            mPlayer.stop();
            mPlayer.release();
            mIsPlaying = false;
        }
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mDefaultVolume, 0);
        }
        super.onPause();
    }

    @Override
    public void onDetach() {
        mPlayer = null;
        mRecorder = null;
        mAudioManager = null;
        super.onDetach();
    }


    /**
     * 判断文件是否存在，存在则删除
     *
     * @param fileName 文件绝对路径
     * @return 操作结果
     */
    private boolean isFileExistAndDelete(String fileName) {
        try {
            File file = new File(fileName);
            return !file.exists() || file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private long getExternalStorageFreeSpace() {
        long freespace = 0;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            freespace = availCount * blockSize;
        }
        return freespace;
    }
}
