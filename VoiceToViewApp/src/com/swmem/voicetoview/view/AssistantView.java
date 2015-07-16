package com.swmem.voicetoview.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class AssistantView implements OnClickListener {
	private View view;
	private Handler handler;
	private ListView listView;
	private ModelListAdapter listAdapter;
	private List<Model> modelList;

	public AssistantView(Context c, WindowManager wManager, Handler handler) {
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.view_assistant, null);
		this.handler = handler;
		this.listView = (ListView) view.findViewById(R.id.lv_model);
		this.modelList = new ArrayList<Model>();
		this.listAdapter = new ModelListAdapter(c, R.layout.item_model, modelList);
		this.listView.setAdapter(listAdapter);
		Button hideBtn = (Button) view.findViewById(R.id.btn_hide);
		hideBtn.setOnClickListener(this);
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,

				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.LEFT | Gravity.TOP;

		view.setVisibility(View.VISIBLE);
		wManager.addView(view, mParams);
	}

	public View getView() {
		return view;
	}

	public void modelListAdd(Model m) {
		if (m != null) {
			modelList.add(m);
			listAdapter.reflesh(modelList);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_hide:
			handler.sendEmptyMessage(Constants.SWAP);
			break;
		}
	}
}
