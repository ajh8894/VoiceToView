package com.swmem.voicetoview.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Database;

public class MainActivity extends Activity implements OnCheckedChangeListener {
	private static final String LOG_TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//DB create
		Database.openOrCreateDB(getApplicationContext());
		//set UI
		Switch mode = (Switch) findViewById(R.id.switch_stt);
		mode.setOnCheckedChangeListener(this);
		Intent i = new Intent("com.swmem.voicetoview.service.VoiceToViewService");
		i.putExtra("activate", true);
		startService(i);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent i = new Intent("com.swmem.voicetoview.service.VoiceToViewService");
		i.putExtra("activate", false);
		stopService(i);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.switch_stt:
			if (isChecked) {

			} else {

			}
		}
	}
}
