package com.swmem.voicetoview.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.Constants;

public class AssistantView implements OnClickListener {
	private View view;
	private Handler handler;
	private ListView listView;
	private ChunkListAdapter listAdapter;
	private List<Chunk> chunkList;
	
	public AssistantView(Context c, WindowManager wManager, Handler handler) {
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.activity_assistant, null);
		this.handler = handler;
		this.listView = (ListView) view.findViewById(R.id.lv_chunk);
		this.chunkList = new ArrayList<Chunk>();
		this.listAdapter = new ChunkListAdapter(c, R.layout.item_chunk, chunkList);
		this.listView.setAdapter(listAdapter);
		Button hideBtn = (Button) view.findViewById(R.id.btn_hide);
		hideBtn.setOnClickListener(this);
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams
		(
			WindowManager.LayoutParams.TYPE_PHONE,			
			WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,		

			PixelFormat.TRANSLUCENT
		);												
		view.setVisibility(View.VISIBLE);
		wManager.addView(view, mParams);
	}
	
    public View getView() {
    	return view;
    }
    
    public List<Chunk> getChunkList() {
		return chunkList;
	}

	public void setChunkList(List<Chunk> chunkList) {
		this.chunkList = chunkList;
	}

	public ChunkListAdapter getListAdapter() {
		return listAdapter;
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
