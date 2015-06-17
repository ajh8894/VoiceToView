package com.swmem.voicetoview.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.swmem.voicetoview.audio.AudioPauser;
import com.swmem.voicetoview.audio.RawAudioRecorder;
import com.swmem.voicetoview.audio.RawAudioRecorder.State;
import com.swmem.voicetoview.task.SpeechRecognition;

public class VoiceToViewService extends Service {
	private static final String LOG_TAG = VoiceToViewService.class.getName();
	private final IBinder mBinder = new VoiceToViewServiceBinder();

	private boolean isActivated;

	// Recorder instance
	private AudioPauser mAudioPauser;
	private RawAudioRecorder mRecorder;
	private long mStartTime = 0;
	private int mMaxRecordingTime = 1000;
	private int sapmleRate;

	private SpeechRecognition mSpeechRecognition;
	
	String fileName = Environment.getExternalStorageDirectory()	+ "/recording";
	
	private Handler mStopHandler;
	private Runnable mStopRunnable = new Runnable() {
		public void run() {
			if (isActivated) {
				if (mMaxRecordingTime < (SystemClock.elapsedRealtime() - mStartTime)) {
					Log.i(LOG_TAG, "Max recording time exceeded");
					mRecorder.stop();
					mSpeechRecognition.getTranscription(mRecorder.consumeRecordingAndTruncate(), sapmleRate);
					mRecorder.start();
					mStartTime = SystemClock.elapsedRealtime();
					mStopHandler.postDelayed(this, 1000);
				} else if (mRecorder.isPausing()) {
					Log.i(LOG_TAG, "Speaker finished speaking");
					mRecorder.stop();
					mSpeechRecognition.getTranscription(mRecorder.consumeRecordingAndTruncate(), sapmleRate);
					mRecorder.start();
					mStartTime = SystemClock.elapsedRealtime();
					mStopHandler.postDelayed(this, 1000);
				} else {
					mStopHandler.postDelayed(this, 1000);
				}
			}
		}
	};

	public class VoiceToViewServiceBinder extends Binder {
		public VoiceToViewService getService() {
			return VoiceToViewService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG, "onBind");
		isActivated = intent.getBooleanExtra("activate", false);
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate();

		init();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG, "onStartCommand");
		isActivated = intent.getBooleanExtra("activate", true);
		
		mRecorder.start();
		mStartTime = SystemClock.elapsedRealtime();
		startAllTasks();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		super.onDestroy();
		stopAllTasks();
	}

	private void init() {
		// 30 second, sampleRate 16000 setting
		mMaxRecordingTime *= 30;
		sapmleRate = 16000;

		mAudioPauser = new AudioPauser(this);
		mRecorder = new RawAudioRecorder();
		mSpeechRecognition = new SpeechRecognition(sapmleRate);

		mStopHandler = new Handler();
	}

	private void startAllTasks() {
		mAudioPauser.pause();
		mStopHandler.postDelayed(mStopRunnable, 1000);
	}

	private void stopAllTasks() {
		isActivated = false;
		
		if(mRecorder.getState() == State.READY || mRecorder.getState() == State.RECORDING)
			mRecorder.stop();
		mRecorder.release();
		
		mStopHandler.removeCallbacks(mStopRunnable);

		if (mAudioPauser != null) {
			mAudioPauser.resume();
		}
	}
}
