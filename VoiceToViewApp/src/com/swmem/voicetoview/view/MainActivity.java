package com.swmem.voicetoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.swmem.voicetoview.R;

public class MainActivity extends Activity implements OnClickListener {
	private static final String LOG_TAG = MainActivity.class.getName();
	// ArrayList<ListItem> list = new ArrayList<ListItem>(); // 구조체 배열 선언

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LinearLayout textLayout = (LinearLayout) findViewById(R.id.text_layout);
		LinearLayout animationLayout = (LinearLayout) findViewById(R.id.animation_layout);
		LinearLayout storageLayout = (LinearLayout) findViewById(R.id.storage_layout);
		
		textLayout.setOnClickListener(this);
		animationLayout.setOnClickListener(this);
		storageLayout.setOnClickListener(this);
		// DB create
		// Database.openOrCreateDB(getApplicationContext());
		// set UI
		// Switch mode = (Switch) findViewById(R.id.switch_stt);


		 Intent i = new
		 Intent("com.swmem.voicetoview.service.VoiceToViewService");
		 i.putExtra("activate", true); startService(i);
		 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		 Intent i = new
		 Intent("com.swmem.voicetoview.service.VoiceToViewService");
		 i.putExtra("activate", false); stopService(i);
		 
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.text_layout) {
			Toast.makeText(getApplicationContext(), "ss", 1).show();
		} else if (v.getId() == R.id.animation_layout) {

		} else if (v.getId() == R.id.storage_layout) {

		}

	}
}
