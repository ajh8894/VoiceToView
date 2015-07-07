package com.swmem.voicetoview.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;

public class HideView implements OnClickListener {
	private View view; 
	private Handler handler;
	public HideView(Context c, WindowManager wManager, Handler handler) {
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.activity_hide, null);
		this.handler = handler;
		Button hideBtn = (Button) view.findViewById(R.id.btn_hide);
		hideBtn.setOnClickListener(this);
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams
		(
			LayoutParams.WRAP_CONTENT,
			LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
																
			PixelFormat.TRANSLUCENT
		);													
		//mParams.gravity = Gravity.TOP | Gravity.RIGHT;
		view.setVisibility(View.GONE);
		wManager.addView(view, mParams);
	}

	public View getView() {
    	return view;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_hide:
			handler.sendEmptyMessage(Constants.SWAP);
			break;
		}
	}
}
