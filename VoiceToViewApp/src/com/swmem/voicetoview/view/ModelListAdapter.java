package com.swmem.voicetoview.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class ModelListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mLayout;
	private List<Model> mItemList;
	private class ViewHolder {
		public ImageView emotion;
		public TextView message;
		public TextView time;
	}
	
	public ModelListAdapter(Context context, int layout, List<Model> itemList) {
		this.mInflater = LayoutInflater.from(context);
		this.mLayout = layout;
		this.mItemList = itemList;
	}
	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Model getItem(int position) {
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

			viewHolder.emotion = (ImageView) convertView.findViewById(R.id.iv_emotion);
			viewHolder.message = (TextView) convertView.findViewById(R.id.tv_message);
			viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Model item = getItem(position);
		
		if(item.getTextResult() != null)
			viewHolder.message.setText(item.getTextResult());
		if(item.getTextResult() != null)
			viewHolder.time.setText(item.getTime());
		
		switch (item.getEmotionType()) {
		case Constants.SAD:
			viewHolder.emotion.setImageResource(R.drawable.sad);
			break;
		case Constants.NATURAL:
			viewHolder.emotion.setImageResource(R.drawable.natural);
			break;
		case Constants.ANGRY:
			viewHolder.emotion.setImageResource(R.drawable.angry);
			break;
		case Constants.HAPPY:
			viewHolder.emotion.setImageResource(R.drawable.happy);
			break;
		default:
			viewHolder.emotion.setImageResource(R.drawable.user_icon);
			break;
		}
		
		return convertView;
	}
}
