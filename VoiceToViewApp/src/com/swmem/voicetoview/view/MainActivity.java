package com.swmem.voicetoview.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.User;
import com.swmem.voicetoview.service.VoiceToViewService;
import com.swmem.voicetoview.util.Database;

public class MainActivity extends Activity implements OnClickListener {
	private final String LOG_TAG = MainActivity.class.getName();
	private User option;
	private Button modeBtn;
	private Button genderBtn;
	private Button dataBtn;

	private Button startBtn;
	private Button stopBtn;
	private String[] header = new String[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// DB create
		Database.openOrCreateDB(getApplicationContext());
		option = Database.selectUser();

		// set UI
		setContentView(R.layout.activity_main);

		modeBtn = (Button) findViewById(R.id.btn_mode);
		genderBtn = (Button) findViewById(R.id.btn_gender);
		dataBtn = (Button) findViewById(R.id.btn_data);

		startBtn = (Button) findViewById(R.id.button1);
		stopBtn = (Button) findViewById(R.id.button2);
		
		modeBtn.setOnClickListener(this);
		genderBtn.setOnClickListener(this);
		dataBtn.setOnClickListener(this);
		
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

		if (option.getMode() == Constants.VIEW_OFF) {
			modeBtn.setText("OFF");
			modeBtn.setBackgroundColor(Color.WHITE);
			header[0] = Constants.KIND_SEND; 
			header[1] = "01086048894"; // from
			header[2] = "01067108898"; // to
		} else {
			modeBtn.setText("ON");
			modeBtn.setBackgroundColor(Color.RED);
			header[0] = Constants.KIND_RECEIVE;
			header[1] = "01067108898"; // from
			header[2] = "01086048894"; // to
		}

		if (option.getMode() == Constants.MALE) {
			genderBtn.setText("MAN");
			genderBtn.setBackgroundColor(Color.BLUE);
		} else {
			genderBtn.setText("WOMAN");
			genderBtn.setBackgroundColor(Color.RED);
		}
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
		case R.id.btn_mode:
			if (option.getMode() == Constants.VIEW_OFF) {
				option.setMode(Constants.VIEW_ON);
				modeBtn.setBackgroundColor(Color.RED);
				modeBtn.setText("ON");
				header[0] = Constants.KIND_RECEIVE;
				header[1] = "01067108898"; // from
				header[2] = "01086048894"; // to
			} else {
				option.setMode(Constants.VIEW_OFF);
				modeBtn.setBackgroundColor(Color.TRANSPARENT);
				modeBtn.setText("OFF");
				header[0] = Constants.KIND_SEND;
				header[1] = "01086048894"; // from
				header[2] = "01067108898"; // to
			}
			Database.updateUser(option);
			Log.d(LOG_TAG, "MODE: " + option.getMode());
			break;
		case R.id.btn_gender:
			if (option.getGender() == Constants.MALE) {
				option.setGender(Constants.FEMALE);
				genderBtn.setBackgroundColor(Color.RED);
				genderBtn.setText("WOMAN");
			} else {
				option.setGender(Constants.MALE);
				genderBtn.setBackgroundColor(Color.BLUE);
				genderBtn.setText("MAN");
			}
			Database.updateUser(option);
			Log.d(LOG_TAG, "GENDER: " + option.getGender());

			break;
		case R.id.btn_data:
			startActivity(new Intent(this, TalkActivity.class));
			break;
		case R.id.button1:
			Intent serviceIntent = new Intent(this, VoiceToViewService.class);
			serviceIntent.putExtra(Constants.SERVICE_EXTRA_HEADER, header);
			serviceIntent.putExtra(Constants.SERVICE_EXTRA_GENDER, true);
			startService(serviceIntent);
			break;
		case R.id.button2:
			Intent serviceIntent1 = new Intent(this, VoiceToViewService.class);
			stopService(serviceIntent1);
			break;
		default:
			break;
		}

	}
}
