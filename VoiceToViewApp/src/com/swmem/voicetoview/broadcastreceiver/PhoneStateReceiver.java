package com.swmem.voicetoview.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.User;
import com.swmem.voicetoview.service.VoiceToViewService;
import com.swmem.voicetoview.util.Database;

//State
//receiver: IDLE -> OFFHOOK -> IDLE
//sender: IDLE -> RINGING -> OFFHOOK -> IDLE
public class PhoneStateReceiver extends BroadcastReceiver {
	private final String LOG_TAG = PhoneStateReceiver.class.getName();
    private static int pState = TelephonyManager.CALL_STATE_IDLE;
    private static String[] header = new String[3];

    @Override
	public void onReceive(Context context, Intent intent) {
    	Database.openOrCreateDB(context);
		final User option = Database.selectUser();
		final Context c = context;
		final TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        telManager.listen(new PhoneStateListener(){
            @Override
			public void onCallStateChanged(int state, String incomingNumber){
                if(state != pState){
                    if(state == TelephonyManager.CALL_STATE_IDLE){
                        Log.i(LOG_TAG,"IDLE");
                        if(header[0] != null){
                        	header[0] = null;

                        	Intent serviceIntent = new Intent(c, VoiceToViewService.class);
                            c.stopService(serviceIntent);
                        }
                    }
                    else if(state == TelephonyManager.CALL_STATE_RINGING){
                        header[1] = telManager.getLine1Number();
                        header[2] = incomingNumber;
                        Log.i(LOG_TAG, "RINGING " + header[0] + " " + header[1] + " " + header[2]);
                    }
                    else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
						if (option.getMode() == Constants.VIEW_ON)
							header[0] = Constants.KIND_RECEIVE;
						else
							header[0] = Constants.KIND_SEND;
						
						Intent serviceIntent = new Intent(c, VoiceToViewService.class);
						serviceIntent.putExtra(Constants.SERVICE_EXTRA_HEADER, header);
						
						if (option.getGender() == Constants.MALE)
							serviceIntent.putExtra(Constants.SERVICE_EXTRA_GENDER, true);
						else
							serviceIntent.putExtra(Constants.SERVICE_EXTRA_GENDER, false);
						Log.i(LOG_TAG, "OFFHOOK " + header[0] + " " + header[1] + " " + header[2] + " " + option.getGender());
						c.startService(serviceIntent);
                    }
                     
                    pState = state;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
         
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            Log.i(LOG_TAG, "OUTGOING_CALL");
            header[1] = telManager.getLine1Number();
            header[2] = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }
    }
}