package com.swmem.voicetoview.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;

public class HideView implements OnClickListener {
	private View view;
	private Handler handler;

	public HideView(Context c, WindowManager wManager, Handler handler) {
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.view_hide, null);
		this.handler = handler;
		ImageView hideIV = (ImageView) view.findViewById(R.id.iv_hide);
		hideIV.setOnClickListener(this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		
		view.setVisibility(View.GONE);
		wManager.addView(view, params);
	}

	public View getView() {
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_hide:
			handler.sendEmptyMessage(Constants.SWAP);
			break;
		}
	}
}
