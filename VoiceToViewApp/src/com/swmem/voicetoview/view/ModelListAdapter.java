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
		public ImageView warning;
		public TextView emotion;
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

			viewHolder.warning = (ImageView) convertView.findViewById(R.id.iv_warning);
			viewHolder.emotion = (TextView) convertView.findViewById(R.id.tv_emotion);
			viewHolder.message = (TextView) convertView.findViewById(R.id.tv_message);
			viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Model item = getItem(position);
		
		if(item.getConfidence() < Constants.CONFIDENCE)
			viewHolder.warning.setVisibility(View.GONE);
		if(item.getTextResult() != null)
			viewHolder.message.setText(item.getTextResult());
		if(item.getTextResult() != null)
			viewHolder.time.setText(item.getTime());
		
		switch (item.getEmotionType()) {
		case Constants.SAD:
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_sad);
			viewHolder.emotion.setText(Constants.STR_SAD);
			break;
		case Constants.NATURAL:
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_natural);
			viewHolder.emotion.setText(Constants.STR_NATURAL);
			break;
		case Constants.ANGRY:
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_angry);
			viewHolder.emotion.setText(Constants.STR_ANGRY);
			break;
		case Constants.HAPPY:
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_happy);
			viewHolder.emotion.setText(Constants.STR_HAPPY);
			break;
		default:
			viewHolder.emotion.setVisibility(View.GONE);
			break;
		}
		
		return convertView;
	}
}
