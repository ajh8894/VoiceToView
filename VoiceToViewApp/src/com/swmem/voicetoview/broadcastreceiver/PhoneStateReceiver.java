package com.swmem.voicetoview.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.swmem.voicetoview.data.ConnectionInfo;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Database;
import com.swmem.voicetoview.data.User;
import com.swmem.voicetoview.service.VoiceToViewService;

public class PhoneStateReceiver extends BroadcastReceiver {
	private final String LOG_TAG = PhoneStateReceiver.class.getName();
    private static int pState = TelephonyManager.CALL_STATE_IDLE;
    private static String[] header;
    private static int call;

    @Override
	public void onReceive(Context context, final Intent intent) {
    	Database.openOrCreateDB(context);
		final User option = Database.selectUser();
		final Context c = context;
		
		final TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        telManager.listen(new PhoneStateListener(){
            @Override
			public void onCallStateChanged(int state, String incomingNumber){
                if(state != pState){
                    if(state == TelephonyManager.CALL_STATE_IDLE){
                        Log.i("Phone","IDLE");
                        if(header != null){
                        	header = null;

                        	Intent serviceIntent = new Intent(c, VoiceToViewService.class); //명시적 인텐트, 롤리팝
                            c.stopService(serviceIntent);
                        }
                    }
                    else if(state == TelephonyManager.CALL_STATE_RINGING){
                        Log.i("Phone","RINGING");
                        header = new String[3];
                        header[1] = telManager.getLine1Number();
                        header[2] = incomingNumber;
                        call = Constants.KIND_CALL_RECEIVER;
                        Log.i(LOG_TAG, "RINGING " + header[0] + " " + header[1] + " " + header[2]);
                    }
                    else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
						if (option.getMode() == Constants.STT_ON)
							header[0] = Constants.KIND_SEND;
						else
							header[0] = Constants.KIND_RECEIVE;
						 Log.i(LOG_TAG, "OFFHOOK " + header[0] + " " + header[1] + " " + header[2]);
						Intent serviceIntent = new Intent(c, VoiceToViewService.class);
						serviceIntent.putExtra(Constants.SERVICE_EXTRA_HEADER, header);
						serviceIntent.putExtra(Constants.SERVICE_EXTRA_KIND_CALL, call);
						c.startService(serviceIntent);
                    }
                     
                    pState = state;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
         
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            Log.i("Phone","OUT");
            header = new String[3];
            header[1] = telManager.getLine1Number();
            header[2] = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            call = Constants.KIND_CALL_SENDER;
        }
    }
}