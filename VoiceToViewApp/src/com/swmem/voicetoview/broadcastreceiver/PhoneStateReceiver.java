package com.swmem.voicetoview.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.swmem.voicetoview.service.VoiceToViewService;
import com.swmem.voicetoview.service.VoiceToViewService.VoiceToViewServiceBinder;
import com.swmem.voicetoview.util.CallLog;

public class PhoneStateReceiver extends BroadcastReceiver {
	private final String LOG_TAG = PhoneStateReceiver.class.getName();
	private int pState = TelephonyManager.CALL_STATE_IDLE;
	private CallLog cLog;
	public enum LogKind {
		KIND_RECEIVE, KIND_SEND
	}

	private VoiceToViewService mService;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(LOG_TAG, "Service connected");
			mService = ((VoiceToViewServiceBinder) service).getService();
		}
		public void onServiceDisconnected(ComponentName className) {
			Log.i(LOG_TAG, "Service disconnected");
			mService = null;
		}
	};
	
	public void onReceive(Context context, final Intent intent) {
		final Context c = context;
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		telManager.listen(new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				if (state != pState) {
					if (state == TelephonyManager.CALL_STATE_IDLE) {
						Log.i(LOG_TAG, "IDLE");
						if (cLog != null) {
							cLog.setEndDate(System.currentTimeMillis());
							Log.i("LOG_TAG", ""+cLog.toString());
							cLog = null;
							
							Intent i = new Intent("com.swmem.voicetoview.service.VoiceToViewService");
							i.putExtra("activate", false);
							c.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
							c.stopService(i);
						}
					} else if (state == TelephonyManager.CALL_STATE_RINGING) {
						Log.i(LOG_TAG, "RINGING");
						cLog = new CallLog(incomingNumber, LogKind.KIND_RECEIVE);
						cLog.setRingingDate(System.currentTimeMillis());
					} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
						Log.i(LOG_TAG, "OFFHOOK");
						cLog.setStartDate(System.currentTimeMillis());
						
						Intent i = new Intent("com.swmem.voicetoview.service.VoiceToViewService");
						i.putExtra("activate", true);
						c.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
						c.startService(i);
					}

					pState = state;
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			Log.i("LOG_TAG", "out");
			cLog = new CallLog(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER),LogKind.KIND_SEND);
			cLog.setRingingDate(System.currentTimeMillis());
		}
	}
}