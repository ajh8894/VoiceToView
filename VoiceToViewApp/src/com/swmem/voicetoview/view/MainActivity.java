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
		Database.updateUser(option);

		
/*		textLayout.setOnClickListener(this);
		animationLayout.setOnClickListener(this);
		storageLayout.setOnClickListener(this);*/
	
		
		
    	Intent serviceIntent = new Intent(Constants.SERVICE_ACTION);
        startService(serviceIntent);
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
			if(option.getMode() == Constants.STT_OFF) {
				option.setMode(Constants.STT_ON);
				Database.updateUser(option);
			} else {
				option.setMode(Constants.STT_OFF);
				Database.updateUser(option);
			}

			Log.d("Main", "" + option.getMode());
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
