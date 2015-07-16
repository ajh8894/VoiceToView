package com.swmem.voicetoview.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

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
import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;
import com.swmem.voicetoview.task.ModelReceiver;
import com.swmem.voicetoview.task.ModelSender;
import com.swmem.voicetoview.task.TaskOperator;
import com.swmem.voicetoview.view.AssistantView;
import com.swmem.voicetoview.view.HideView;

public class VoiceToViewService extends Service {
	private static final String LOG_TAG = VoiceToViewService.class.getName();
	private final IBinder mBinder = new VoiceToViewBinder();
	
	// DataQueue instance
	private BlockingQueue<Model> mSenderQueue;
	private BlockingQueue<Model> mReceiverQueue;

	// View instance
	private WindowManager mWindowManager;
	private boolean mVisible;
	private HideView mHideView;
	private AssistantView mAssistantView;

	// Recorder instance
	private boolean isActivated;
	private AudioPauser mAudioPauser;
	private RawAudioRecorder mRecorder;
	private long mStartTime = 0;
	private int mMaxRecordingTime = 1000;
	private int order = 0;
	private Handler mStopHandler;
	private Runnable mStopRunnable = new Runnable() {
		@Override
		public void run() {
			if (isActivated) {
				if (mMaxRecordingTime < (SystemClock.elapsedRealtime() - mStartTime)) {
					Log.d(LOG_TAG, "Max recording time exceeded");
					repeatRecoding();
				} else if (mRecorder.isPausing()) {
					Log.d(LOG_TAG, "Speaker finished speaking");
					repeatRecoding();
				}
				mStopHandler.postDelayed(this, Constants.TASK_DELAY_STOP);
			}
		}
	};

	// Http, Tcp/Ip instance
	private TaskOperator mOperator;
	private ModelSender mModelSender;
	private ModelReceiver mModelReceiver;
	private Handler mReceiverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d("receiverHandler", String.valueOf(msg.what));
			switch (msg.what) {
			case Constants.CONNECT:
				mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
				mAssistantView = new AssistantView(getApplicationContext(), mWindowManager, mReceiverHandler);
				mHideView = new HideView(getApplicationContext(), mWindowManager, mReceiverHandler);
				mVisible = true;
				mModelReceiver = new ModelReceiver(mReceiverQueue, this);
				mModelReceiver.start();
				break;
			case Constants.RECONNECT:
				mModelReceiver = new ModelReceiver(mReceiverQueue, this);
				mModelReceiver.start();
				break;
			case Constants.REFRESH:
				mAssistantView.modelListAdd(mReceiverQueue.poll());
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
			Log.d("senderHandler", String.valueOf(msg.what));
			switch (msg.what) {
			case Constants.CONNECT_INIT:
				mOperator = new TaskOperator(Constants.CONNECT_INIT, mSenderHandler, mReceiverHandler);
				mOperator.start();
				break;
			case Constants.CONNECT:
				isActivated = true;
				mRecorder.start();
				mStartTime = SystemClock.elapsedRealtime();
				mStopHandler.postDelayed(mStopRunnable, Constants.TASK_DELAY_STOP);
				mModelSender = new ModelSender(mSenderQueue, this);
				mModelSender.start();
				break;
			case Constants.RECONNECT:
				mModelSender = new ModelSender(mSenderQueue, this);
				mModelSender.start();
				break;
			case Constants.DISCONNECT:
				mOperator = new TaskOperator(Constants.DISCONNECT, mSenderHandler, mReceiverHandler);
				mOperator.start();
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
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG, "onStartCommand");
		Connection.header = intent.getStringArrayExtra(Constants.SERVICE_EXTRA_HEADER);
		startAllTasks();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		super.onDestroy();
		stopAllTasks();
	}

	private void startAllTasks() {
		mSenderQueue = new PriorityBlockingQueue<Model>(1024);
		mReceiverQueue = new ArrayBlockingQueue<Model>(1024);

		mAudioPauser = new AudioPauser(this);
		mRecorder = new RawAudioRecorder();
		// 15 second, sampleRate 16000 setting
		mMaxRecordingTime *= Constants.MAX_RECORD_TIME;
		mStopHandler = new Handler();
		
		mOperator = new TaskOperator(Constants.CONNECT_INIT, mSenderHandler, mReceiverHandler);
		mModelSender = new ModelSender(mSenderQueue, mSenderHandler);
		
		mAudioPauser.pause();
		Log.i(LOG_TAG, Connection.header[0] + " " + Connection.header[1] + " " + Connection.header[2]);
		mOperator.start();
	}

	private void stopAllTasks() {
		isActivated = false;
		Connection.disconnect();
		
		if (mRecorder.getState() == State.READY || mRecorder.getState() == State.RECORDING)
			mRecorder.stop();
		mRecorder.release();

		if (mStopHandler != null) {
			mStopHandler.removeCallbacks(mStopRunnable);
		}
		
		if (mOperator != null) {
			if (mOperator.isAlive())
				mOperator.interrupt();
		}
		
		if (mReceiverHandler != null) {
			mReceiverHandler.removeMessages(Constants.CONNECT);
			mReceiverHandler.removeMessages(Constants.RECONNECT);
			mReceiverHandler.removeMessages(Constants.REFRESH);
			mReceiverHandler.removeMessages(Constants.SWAP);
		}

		if (mSenderHandler != null) {
			mSenderHandler.removeMessages(Constants.CONNECT_INIT);
			mSenderHandler.removeMessages(Constants.CONNECT);
			mSenderHandler.removeMessages(Constants.RECONNECT);
		}

		if (mModelReceiver != null) {
			mModelReceiver.setActivated(false);
			if (mModelReceiver.isAlive())
				mModelReceiver.interrupt();
		}
		if (mModelSender != null) {
			mModelSender.setActivated(false);
			if (mModelSender.isAlive())
				mModelSender.interrupt();
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
		
		mOperator = new TaskOperator(Constants.DISCONNECT, mSenderHandler, mReceiverHandler);
		mOperator.start();
		
		Log.i(LOG_TAG, "Destroy Complete");
	}

	private void repeatRecoding() {
		if (mRecorder.getState() == State.RECORDING)
			mRecorder.stop();

		try {
			Model model = new Model(Connection.header[1], Connection.header[2], true, order++, mRecorder.consumeRecordingAndTruncate());
			mSenderQueue.put(model);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			/*ModelProducer aModelProducer = new ModelProducer(mSenderQueue, order++, mRecorder.consumeRecordingAndTruncate());
			new Thread(aModelProducer).start();*/
		
		
		mRecorder.release();

		mRecorder = new RawAudioRecorder();
		mRecorder.start();
		mStartTime = SystemClock.elapsedRealtime();
	}
}
