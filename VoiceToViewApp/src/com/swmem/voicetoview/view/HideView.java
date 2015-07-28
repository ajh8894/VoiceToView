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
	private View mView;
	private Handler mHandler;

	public HideView(Context c, WindowManager wManager, Handler mHandler) {
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mView = inflater.inflate(R.layout.view_hide, null);
		this.mHandler = mHandler;
		ImageView hideIV = (ImageView) mView.findViewById(R.id.iv_hide);
		hideIV.setOnClickListener(this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		
		mView.setVisibility(View.GONE);
		wManager.addView(mView, params);
	}

	public View getView() {
		return mView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_hide:
			mHandler.sendEmptyMessage(Constants.SWAP);
			break;
		}
	}
}
