package com.swmem.voicetoview.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.User;
import com.swmem.voicetoview.service.VoiceToViewService;
import com.swmem.voicetoview.util.Database;

public class MainActivity extends Activity implements OnClickListener {
	private User option;
	private LinearLayout[] btnLayout;
	
	private String[] header = new String[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// DB create
		Database.openOrCreateDB(getApplicationContext());
		option = Database.selectUser();
		
		// set UI
		setContentView(R.layout.activity_main);
		int[] layoutId = { R.id.onoff_layout, R.id.gender_layout, R.id.gender_layout, R.id.animation_layout, R.id.theme_layout, R.id.data_layout, R.id.help_layout };
		btnLayout = new LinearLayout[layoutId.length];
		for (int i = 0; i < layoutId.length; i++) {
			btnLayout[i] = (LinearLayout) findViewById(layoutId[i]);
			btnLayout[i].setOnClickListener(this);
		}
		if (option.getMode() == Constants.VIEW_OFF) {
			btnLayout[0].setBackgroundColor(Color.WHITE);
			header[0] = Constants.KIND_SEND; // 종류
			header[1] = "01086048894"; // from
			header[2] = "01067108898"; // to
		} else {
			btnLayout[0].setBackgroundColor(Color.RED);
			header[0] = Constants.KIND_RECEIVE; // 종류
			header[1] = "01067108898"; // from
			header[2] = "01086048894"; // to
		}
		/*
		 * textLayout.setOnClickListener(this);
		 * animationLayout.setOnClickListener(this);
		 * storageLayout.setOnClickListener(this);
		 */


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


		Intent serviceIntent = new Intent(this, VoiceToViewService.class);
		stopService(serviceIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.onoff_layout:
			if (option.getMode() == Constants.VIEW_OFF) {
				option.setMode(Constants.VIEW_ON);
				Database.updateUser(option);
				btnLayout[0].setBackgroundColor(Color.RED);
/*				
				header[0] = Constants.KIND_RECEIVE; // 종류
				header[1] = "01067108898"; // from
				header[2] = "01086048894"; // to
*/				
				header[0] = Constants.KIND_RECEIVE; // 종류
				header[1] = "01066664444"; // from
				header[2] = "05050505050"; // to
			} else {
				option.setMode(Constants.VIEW_OFF);
				Database.updateUser(option);
				btnLayout[0].setBackgroundColor(Color.WHITE);
				
				header[0] = Constants.KIND_RECEIVE; // 종류
				header[1] = "05050505050"; // from
				header[2] = "01066664444"; // to

			}

			Log.d("Main", "" + option.getMode());
			break;
		case R.id.gender_layout:
			Intent serviceIntent = new Intent(this, VoiceToViewService.class);
			serviceIntent.putExtra(Constants.SERVICE_EXTRA_HEADER, header);
			startService(serviceIntent);
			break;
		case R.id.animation_layout:
			Intent serviceIntent1 = new Intent(this, VoiceToViewService.class);
			serviceIntent1.putExtra(Constants.SERVICE_EXTRA_HEADER, header);
			stopService(serviceIntent1);
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
