package com.swmem.voicetoview.service;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.swmem.voicetoview.audio.AudioPauser;
import com.swmem.voicetoview.audio.RawAudioRecorder;
import com.swmem.voicetoview.audio.RawAudioRecorder.State;
import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.ConnectionInfo;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.task.ChunkConsumer;
import com.swmem.voicetoview.task.ChunkProducer;
import com.swmem.voicetoview.task.ChunkReceiver;
import com.swmem.voicetoview.view.AssistantView;
import com.swmem.voicetoview.view.HideView;

public class VoiceToViewService extends Service {
	private static final String LOG_TAG = VoiceToViewService.class.getName();
	private final IBinder mBinder = new VoiceToViewBinder();
	// Data instance
	private BlockingQueue<Chunk> mSenderQueue;
	private BlockingQueue<Chunk> mReceiverQueue;

	private WindowManager mWindowManager;
	private boolean mVisible;
	private HideView mHideView;
	private AssistantView mAssistantView;
	private ArrayList<Chunk> mViewList;

	// Recorder instance
	private boolean isActivated;
	private AudioPauser mAudioPauser;
	private RawAudioRecorder mRecorder;
	private long mStartTime = 0;
	private int mMaxRecordingTime = 1000;
	private Handler mStopHandler;
	private Runnable mStopRunnable = new Runnable() {
		@Override
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

	// Http, Tcp/Ip instance
	private ChunkReceiver mChunkReceiver;
	private ChunkConsumer mChunkConsumer;
	private Handler mReceiverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.REFRESH:
				try {
					mAssistantView.getChunkList().add(mReceiverQueue.take());
					mAssistantView.getListAdapter().reflesh(
							mAssistantView.getChunkList());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case Constants.RECONNECT:
				break;
			case Constants.SWAP:
				if (mVisible) {
					mAssistantView.getView().setVisibility(View.GONE);
					mHideView.getView().setVisibility(View.VISIBLE);
					mVisible = false;
				} else {
					mAssistantView.getView().setVisibility(View.VISIBLE);
					mHideView.getView().setVisibility(View.GONE);
					mVisible = true;
				}
				break;
			default:
				break;
			}
		};
	};
	private Handler mSenderHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.CONNECT:
				isActivated = true;
				mRecorder.start();
				mStartTime = SystemClock.elapsedRealtime();
				mStopHandler.postDelayed(mStopRunnable,
						Constants.TASK_DELAY_STOP);
				break;
			case Constants.RECONNECT:
				break;
			default:
				break;
			}
		};
	};

	public class VoiceToViewBinder extends Binder {
		public VoiceToViewService getService() {
			return VoiceToViewService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
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

		/*
		 * ConnectionInfo.header = new String[3]; ConnectionInfo.header[0] =
		 * "sender"; // 종류 ConnectionInfo.header[1] = "01086048894"; // from
		 * ConnectionInfo.header[2] = "01067108898"; // to ConnectionInfo.call =
		 * Constants.KIND_CALL_SENDER;
		 */

		ConnectionInfo.header = new String[3];
		ConnectionInfo.header[0] = "receiver"; // 종류
		ConnectionInfo.header[1] = "01067108898"; // from
		ConnectionInfo.header[2] = "01086048894"; // to
		ConnectionInfo.call = Constants.KIND_CALL_RECEIVER;

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
		mSenderQueue = new ArrayBlockingQueue<Chunk>(1024);
		mReceiverQueue = new ArrayBlockingQueue<Chunk>(1024);
		mViewList = new ArrayList<Chunk>();

		mAudioPauser = new AudioPauser(this);
		mRecorder = new RawAudioRecorder();
		// 30 second, sampleRate 16000 setting
		mMaxRecordingTime *= Constants.MAX_RECORD_TIME;
		mStopHandler = new Handler();
	}

	private void startAllTasks() {
		mAudioPauser.pause();
		if (ConnectionInfo.header[0].equals(Constants.KIND_RECEIVE)) { // STT_OFF
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			mAssistantView = new AssistantView(getApplicationContext(),
					mWindowManager, mReceiverHandler);
			mHideView = new HideView(getApplicationContext(), mWindowManager,
					mReceiverHandler);
			mAssistantView.getView().setVisibility(View.VISIBLE);
			mHideView.getView().setVisibility(View.GONE);
			mVisible = true;
			mChunkReceiver = new ChunkReceiver(mReceiverQueue, mReceiverHandler);
			mChunkReceiver.start();
		} else if (ConnectionInfo.header[0].equals(Constants.KIND_SEND)) { // STT_ON
			mChunkConsumer = new ChunkConsumer(mSenderQueue, mSenderHandler);
			mChunkConsumer.start();
			if (ConnectionInfo.call == Constants.KIND_CALL_RECEIVER) {
				isActivated = true;
				mRecorder.start();
				mStartTime = SystemClock.elapsedRealtime();
				mStopHandler.postDelayed(mStopRunnable,
						Constants.TASK_DELAY_STOP);
			}
		}

	}

	private void stopAllTasks() {
		isActivated = false;

		if (mRecorder.getState() == State.READY
				|| mRecorder.getState() == State.RECORDING)
			mRecorder.stop();
		mRecorder.release();

		if (mStopHandler != null) {
			mStopHandler.removeCallbacks(mStopRunnable);
		}

		if (mReceiverHandler != null) {
			mReceiverHandler.removeMessages(Constants.RECONNECT);
			mReceiverHandler.removeMessages(Constants.REFRESH);
		}

		if (mSenderHandler != null) {
			mSenderHandler.removeMessages(Constants.RECONNECT);
		}

		if (mChunkReceiver != null) {
			mChunkReceiver.close();
			if (mChunkReceiver.isAlive())
				mChunkReceiver.interrupt();
		}
		if (mChunkConsumer != null) {
			mChunkConsumer.setActivated(false);
			mChunkConsumer.close();
			if (mChunkConsumer.isAlive())
				mChunkConsumer.interrupt();
		}

		if (mWindowManager != null) {
			if (mAssistantView != null)
				mWindowManager.removeView(mAssistantView.getView());
			if (mHideView != null)
				mWindowManager.removeView(mHideView.getView());
		}

		mSenderQueue.clear();
		mReceiverQueue.clear();

		if (mAudioPauser != null) {
			mAudioPauser.resume();
		}
	}

	private void repeatRecoding() {
		if (mRecorder.getState() == State.RECORDING)
			mRecorder.stop();

		ChunkProducer aChunkProducer = new ChunkProducer(mSenderQueue,
				mRecorder.consumeRecordingAndTruncate());
		new Thread(aChunkProducer).start();

		mRecorder.release();

		mRecorder = new RawAudioRecorder();
		mRecorder.start();
		mStartTime = SystemClock.elapsedRealtime();
	}
}
