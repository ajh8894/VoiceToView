package com.swmem.voicetoview.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swmem.voicetoview.data.Chunk;

public class ChunkListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<Chunk> mItemList;
	
	private class ViewHolder {
		public ImageView emotion;
		public TextView text;
	}
	
	public ChunkListAdapter(Context context, int layout, ArrayList<Chunk> itemList) {
		this.mInflater = LayoutInflater.from(context);
		this.mLayout = layout;
		this.mItemList = itemList;
	}
	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Chunk getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		Chunk item = getItem(position);
		
		if(convertView == null) {
			convertView = mInflater.inflate(mLayout, parent, false);
			
			viewHolder = new ViewHolder();
			
			
			//viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconImage);
/*			switch (item.getEmotion()) {
			
			}*/
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		/*viewHolder.name.setText(mItemList.get(position).getName());
	    viewHolder.address.setText(mItemList.get(position).getAddress());
	    viewHolder.phone.setText(mItemList.get(position).getPhone());*/
		
		return convertView;
	}
}
