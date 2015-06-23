package com.swmem.voicetoview.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.audio.AudioPauser;
import com.swmem.voicetoview.audio.RawAudioRecorder;
import com.swmem.voicetoview.audio.RawAudioRecorder.State;
import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.task.Consumer;
import com.swmem.voicetoview.task.Producer;

public class VoiceToViewService extends Service {
	private static final String LOG_TAG = VoiceToViewService.class.getName();
	private final RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();
	private boolean isActivated;
	private int mode;
	private final int STT_OFF = 0;
	private final int STT_ON = 1;
	
	private WindowManager mWindowManager;
	private View mHideView, mAssistView;
	
	private BlockingQueue<Chunk> mSenderQueue;
	private BlockingQueue<Chunk> mReceiverQueue;
		
	// Recorder instance
	private AudioPauser mAudioPauser;
	private RawAudioRecorder mRecorder;
	private long mStartTime = 0;
	private int mMaxRecordingTime = 1000;

	String fileName = Environment.getExternalStorageDirectory()	+ "/recording";
	
	private Handler mStopHandler;
	private Runnable mStopRunnable = new Runnable() {
		public void run() {
			if (isActivated) {
				if (mMaxRecordingTime < (SystemClock.elapsedRealtime() - mStartTime)) {
					Log.i(LOG_TAG, "Max recording time exceeded");
					repeatRecoding();
				} else if (mRecorder.isPausing()) {
					Log.i(LOG_TAG, "Speaker finished speaking");
					repeatRecoding();
				}
				mStopHandler.postDelayed(this, 1000);
			}
		}
	};

	private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
		@Override
		public void unregisterCallback(IRemoteServiceCallback callback)
				throws RemoteException {
			if(callback != null) {
				mCallbacks.register(callback);
			}
			
		}

		@Override
		public void registerCallback(IRemoteServiceCallback callback)
				throws RemoteException {
			if(callback != null) {
				mCallbacks.unregister(callback);
			}	
		}

		@Override
		public void getChunkList() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void sendMessageCallback(int msg) {
		final int N = mCallbacks.beginBroadcast();
		for(int i = 0; i < N; i++) {
			try {
				mCallbacks.getBroadcastItem(i).messageCallback(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		mCallbacks.finishBroadcast();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG, "onBind");
		//isActivated = intent.getBooleanExtra("activate", false);
		//mode = intent.getIntExtra("mode", STT_OFF);
		if(IRemoteService.class.getName().equals(intent.getAction())) {
			return mBinder;
		}
		return null;
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
		
		if (mode == STT_ON) {

		} else if(mode == STT_OFF) {
		
		}

		
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
		//sapmleRate = 16000;
		
		mAudioPauser = new AudioPauser(this);
		mRecorder = new RawAudioRecorder();
		mStopHandler = new Handler();

		mSenderQueue = new ArrayBlockingQueue<Chunk>(1024);
		mReceiverQueue = new ArrayBlockingQueue<Chunk>(1024);
		
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mHideView = (View) inflater.inflate(R.layout.activity_hide, null);
		mAssistView = (View) inflater.inflate(R.layout.activity_assistant, null);
		WindowManager.LayoutParams aParams = new WindowManager.LayoutParams
		(
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,		
																		
			PixelFormat.TRANSLUCENT
		);		
		
		WindowManager.LayoutParams hParams = new WindowManager.LayoutParams
		(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,		
			
			PixelFormat.TRANSLUCENT
		);
		hParams.gravity = Gravity.TOP | Gravity.RIGHT;
		
		mWindowManager.addView(mAssistView, aParams);
		mWindowManager.addView(mHideView, hParams);
		
		mAssistView.setVisibility(View.VISIBLE);
		mHideView.setVisibility(View.GONE);
		
		if (mode == STT_ON) {

		} else if(mode == STT_OFF) {
			/*
			Intent intent = new Intent(getApplicationContext(), AssistantActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);*/	
		}
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
	
	private void repeatRecoding() {
		if(mRecorder.getState() == State.RECORDING)
			mRecorder.stop();
		
		Producer producer = new Producer(mSenderQueue, mRecorder.consumeRecordingAndTruncate());
		Consumer consumer = new Consumer(mSenderQueue);
	
		new Thread(producer).start();
		new Thread(consumer).start();
		
		mRecorder.release();
		
		mRecorder = new RawAudioRecorder();
		mRecorder.start();
		mStartTime = SystemClock.elapsedRealtime();
	}
}
