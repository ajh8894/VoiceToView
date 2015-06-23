package com.swmem.voicetoview.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.service.IRemoteService;
import com.swmem.voicetoview.service.IRemoteServiceCallback;

public class AssistantActivity extends Activity implements OnClickListener {
	private final String LOG_TAG = AssistantActivity.class.getName();
	private IRemoteService mService = null;

	private ListView mListView;
	private ChunkListAdapter mListAdapter;
	private ArrayList<Chunk> mChunkList;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mListView.notify();
			mListAdapter.notifyDataSetChanged();
		};
	};
	
	private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
		@Override
		public void messageCallback(int msg) throws RemoteException {
			mHandler.sendEmptyMessage(msg);
		}
	};
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IRemoteService.Stub.asInterface(service);
			try {
				mService.registerCallback(mCallback);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, "onServiceConnected error", e);
			}
			
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assistant);
		
		mListView = (ListView) findViewById(R.id.chunk_lv);
		ImageButton closeBtn = (ImageButton) findViewById(R.id.close_btn);
		ImageButton hideBtn = (ImageButton) findViewById(R.id.hide_btn);
		ImageButton keyPadBtn = (ImageButton) findViewById(R.id.keypad_btn);
		
		closeBtn.setOnClickListener(this);
		hideBtn.setOnClickListener(this);
		keyPadBtn.setOnClickListener(this);
		
		mChunkList = new ArrayList<Chunk>();
		mListAdapter = new ChunkListAdapter(getApplicationContext(), R.layout.chunk_item, mChunkList);
		mListView.setAdapter(mListAdapter);
		
		//mService.getChunkList();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.close_btn:
			break;
		case R.id.hide_btn:
			break;
		case R.id.keypad_btn:
			break;
			
		}
		
	}
}
