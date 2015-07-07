package com.swmem.voicetoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Database;
import com.swmem.voicetoview.data.User;

public class MainActivity extends Activity implements OnClickListener {
	private static final String LOG_TAG = MainActivity.class.getName();
	private User option;
	LinearLayout onoffLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set UI
		setContentView(R.layout.activity_main);
		onoffLayout = (LinearLayout) findViewById(R.id.onoff_layout);
		//LinearLayout genderLayout = (LinearLayout) findViewById(R.id.gender_layout);
		//LinearLayout animationLayout = (LinearLayout) findViewById(R.id.animation_layout);
		//LinearLayout themeLayout = (LinearLayout) findViewById(R.id.theme_layout);
		//LinearLayout dataLayout = (LinearLayout) findViewById(R.id.data_layout);
		//LinearLayout helpLayout = (LinearLayout) findViewById(R.id.help_layout);
		
		onoffLayout.setOnClickListener(this);
		/*genderLayout.setOnClickListener(this);
		animationLayout.setOnClickListener(this);
		themeLayout.setOnClickListener(this);
		dataLayout.setOnClickListener(this);
		helpLayout.setOnClickListener(this);*/
		
		// DB create
		Database.openOrCreateDB(getApplicationContext());
		option = Database.selectUser();
		
		if(option.getMode() == Constants.STT_OFF) {
			//onoffLayout.setSelected(false);
		} else {
			//onoffLayout.setSelected(true);
		}
		
/*		textLayout.setOnClickListener(this);
		animationLayout.setOnClickListener(this);
		storageLayout.setOnClickListener(this);*/
	
		
		Log.d("Main", "" + option.getMode());
/*		
    	Intent serviceIntent = new Intent(Constants.SERVICE_ACTION);
        startService(serviceIntent);*/
	}

	@Override
	protected void onPause() {
		super.onPause();
    	Intent serviceIntent = new Intent(Constants.SERVICE_ACTION);
        stopService(serviceIntent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
    	Intent serviceIntent = new Intent(Constants.SERVICE_ACTION);
        stopService(serviceIntent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
    	Intent serviceIntent = new Intent(Constants.SERVICE_ACTION);
        stopService(serviceIntent);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.onoff_layout:
/*			Log.d("onoff_layout", "" + onoffLayout.isPressed());
			Log.d("onoff_layout",
					"" + option.getMode() + " " + onoffLayout.isSelected());
			if(onoffLayout.isPressed()) {

				option.setMode(Constants.STT_OFF);
				onoffLayout.setPressed(false);
			} else {
				option.setMode(Constants.STT_ON);
				onoffLayout.setPressed(true);
			}*/
			Database.updateUser(option);
			break;
		case R.id.gender_layout:
			break;
		case R.id.animation_layout:
			break;
		case R.id.theme_layout:
			break;
		case R.id.data_layout:
			break;
		case R.id.help_layout:
			break;
		default:
			break;
		}
		
	}
}
