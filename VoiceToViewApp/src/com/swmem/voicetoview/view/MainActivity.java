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
	private User mOption;
	
	private LinearLayout mModeLayout;
	private LinearLayout mGenderLayout;
	private LinearLayout mDataLayout;

	private ImageView mModeIV;
	private ImageView mGenderIV;
	
	private TextView mModeTV;
	
	private Button startBtn;
	private Button stopBtn;
	private String[] header = new String[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// DB create
		Database.openOrCreateDB(getApplicationContext());
		mOption = Database.selectUser();

		// set UI
		setContentView(R.layout.activity_main);

		mModeLayout = (LinearLayout) findViewById(R.id.layout_mode);
		mGenderLayout = (LinearLayout) findViewById(R.id.layout_gender);
		mDataLayout = (LinearLayout) findViewById(R.id.layout_data);

		mModeIV = (ImageView) findViewById(R.id.iv_mode);
		mGenderIV = (ImageView) findViewById(R.id.iv_gender);
		
		mModeTV = (TextView) findViewById(R.id.tv_mode);
		
		startBtn = (Button) findViewById(R.id.button1);
		stopBtn = (Button) findViewById(R.id.button2);
		
		mModeLayout.setOnClickListener(this);
		mGenderLayout.setOnClickListener(this);
		mDataLayout.setOnClickListener(this);
		
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

		if (mOption.getMode() == Constants.VIEW_OFF) {
			mModeIV.setImageResource(R.drawable.category_icon_switch_off);
			mModeTV.setTextColor(Color.parseColor("#747474"));
			mModeLayout.setSelected(false);
			header[0] = Constants.KIND_SEND; 
			header[1] = "01086048894"; // from
			header[2] = "01067108898"; // to
		} else {
			mModeIV.setImageResource(R.drawable.category_icon_switch_on);
			mModeTV.setTextColor(Color.parseColor("#FFFFFF"));
			mModeLayout.setSelected(true);
			header[0] = Constants.KIND_RECEIVE;
			header[1] = "01067108898"; // from
			header[2] = "01086048894"; // to
		}

		if (mOption.getGender() == Constants.MALE) {
			mGenderIV.setImageResource(R.drawable.category_icon_man);
		} else {
			mGenderIV.setImageResource(R.drawable.category_icon_woman);
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
			if (mOption.getMode() == Constants.VIEW_OFF) {
				mModeIV.setImageResource(R.drawable.category_icon_switch_on);
				mModeTV.setTextColor(Color.parseColor("#FFFFFF"));
				mModeLayout.setSelected(true);
				mOption.setMode(Constants.VIEW_ON);
				header[0] = Constants.KIND_RECEIVE;
				header[1] = "01067108898"; // from
				header[2] = "01086048894"; // to
			} else {
				mModeIV.setImageResource(R.drawable.category_icon_switch_off);
				mModeTV.setTextColor(Color.parseColor("#747474"));
				mModeLayout.setSelected(false);
				mOption.setMode(Constants.VIEW_OFF);
				header[0] = Constants.KIND_SEND;
				header[1] = "01086048894"; // from
				header[2] = "01067108898"; // to
			}
			Database.updateUser(mOption);
			Log.d(MainActivity.class.getName(), "MODE: " + mOption.getMode());
			break;
		case R.id.layout_gender:
			if (mOption.getGender() == Constants.MALE) {
				mGenderIV.setImageResource(R.drawable.category_icon_woman);
				mOption.setGender(Constants.FEMALE);
			} else {
				mGenderIV.setImageResource(R.drawable.category_icon_man);
				mOption.setGender(Constants.MALE);
			}
			Database.updateUser(mOption);
			Log.d(MainActivity.class.getName(), "GENDER: " + mOption.getGender());
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
