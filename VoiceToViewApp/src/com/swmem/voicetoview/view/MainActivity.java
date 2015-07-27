package com.swmem.voicetoview.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.User;
import com.swmem.voicetoview.service.VoiceToViewService;
import com.swmem.voicetoview.util.Database;

public class MainActivity extends Activity implements OnClickListener {
	private final String LOG_TAG = MainActivity.class.getName();
	private User option;
	
	private LinearLayout modeLayout;
	private LinearLayout genderLayout;
	private LinearLayout dataLayout;

	private ImageView modeIV;
	private ImageView genderIV;
	
	private TextView modeTV;
	
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

		modeLayout = (LinearLayout) findViewById(R.id.layout_mode);
		genderLayout = (LinearLayout) findViewById(R.id.layout_gender);
		dataLayout = (LinearLayout) findViewById(R.id.layout_data);

		modeIV = (ImageView) findViewById(R.id.iv_mode);
		genderIV = (ImageView) findViewById(R.id.iv_gender);
		
		modeTV = (TextView) findViewById(R.id.tv_mode);
		
		startBtn = (Button) findViewById(R.id.button1);
		stopBtn = (Button) findViewById(R.id.button2);
		
		modeLayout.setOnClickListener(this);
		genderLayout.setOnClickListener(this);
		dataLayout.setOnClickListener(this);
		
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

		if (option.getMode() == Constants.VIEW_OFF) {
			modeIV.setImageResource(R.drawable.category_icon_switch_off);
			modeTV.setTextColor(Color.parseColor("#747474"));
			modeLayout.setSelected(false);
			header[0] = Constants.KIND_SEND; 
			header[1] = "01086048894"; // from
			header[2] = "01067108898"; // to
		} else {
			modeIV.setImageResource(R.drawable.category_icon_switch_on);
			modeTV.setTextColor(Color.parseColor("#FFFFFF"));
			modeLayout.setSelected(true);
			header[0] = Constants.KIND_RECEIVE;
			header[1] = "01067108898"; // from
			header[2] = "01086048894"; // to
		}

		if (option.getGender() == Constants.MALE) {
			genderIV.setImageResource(R.drawable.category_icon_man);
		} else {
			genderIV.setImageResource(R.drawable.category_icon_woman);
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
		case R.id.layout_mode:
			if (option.getMode() == Constants.VIEW_OFF) {
				modeIV.setImageResource(R.drawable.category_icon_switch_on);
				modeTV.setTextColor(Color.parseColor("#FFFFFF"));
				modeLayout.setSelected(true);
				option.setMode(Constants.VIEW_ON);
				header[0] = Constants.KIND_RECEIVE;
				header[1] = "01067108898"; // from
				header[2] = "01086048894"; // to
			} else {
				modeIV.setImageResource(R.drawable.category_icon_switch_off);
				modeTV.setTextColor(Color.parseColor("#747474"));
				modeLayout.setSelected(false);
				option.setMode(Constants.VIEW_OFF);
				header[0] = Constants.KIND_SEND;
				header[1] = "01086048894"; // from
				header[2] = "01067108898"; // to
			}
			Database.updateUser(option);
			Log.d(LOG_TAG, "MODE: " + option.getMode());
			break;
		case R.id.layout_gender:
			if (option.getGender() == Constants.MALE) {
				genderIV.setImageResource(R.drawable.category_icon_woman);
				option.setGender(Constants.FEMALE);
			} else {
				genderIV.setImageResource(R.drawable.category_icon_man);
				option.setGender(Constants.MALE);
			}
			Database.updateUser(option);
			Log.d(LOG_TAG, "GENDER: " + option.getGender());
			break;
		case R.id.layout_data:
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
