package com.swmem.voicetoview.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Talk;

public class TalkListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mLayout;
	private List<Talk> mItemList;
	private class ViewHolder {
		public TextView date;
		public TextView id;
	}
	
	public TalkListAdapter(Context context, int layout, List<Talk> itemList) {
		this.mInflater = LayoutInflater.from(context);
		this.mLayout = layout;
		this.mItemList = itemList;
	}
	@Override	
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Talk getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(mLayout, parent, false);
			
			viewHolder = new ViewHolder();

			viewHolder.date = (TextView) convertView.findViewById(R.id.tv_date);
			viewHolder.id = (TextView) convertView.findViewById(R.id.tv_id);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Talk item = getItem(position);
		
		if(item.getDate() != null)
			viewHolder.date.setText(item.getDate());
		if(item.getId() != null)
			viewHolder.id.setText(item.getId());
		
		return convertView;
	}
}
