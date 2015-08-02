package com.swmem.voicetoview.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class ModelListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mLayout;
	private List<Model> mItemList;

	private class ViewHolder {
		public ViewAnimator emotionSwitching;
		public ViewAnimator messageSwitching;
		public ImageView warning;
		public TextView message;
		public TextView emotion;
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
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(mLayout, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.messageSwitching = (ViewAnimator) convertView.findViewById(R.id.va_message);
			viewHolder.warning = (ImageView) viewHolder.messageSwitching.getChildAt(2).findViewById(R.id.iv_warning);
			viewHolder.message = (TextView) viewHolder.messageSwitching.getChildAt(2).findViewById(R.id.tv_message);
			
			viewHolder.emotionSwitching = (ViewAnimator) convertView.findViewById(R.id.va_emotion);
			viewHolder.emotion = (TextView) viewHolder.emotionSwitching.getChildAt(1);
			
			viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Model item = getItem(position);
		
		if (item.getMessageNum() == -2) {
			viewHolder.emotionSwitching.setVisibility(View.GONE);
			viewHolder.messageSwitching.setDisplayedChild(0);
			viewHolder.warning.setVisibility(View.GONE);
			viewHolder.message.setVisibility(View.GONE);
			viewHolder.emotion.setVisibility(View.GONE);
			viewHolder.time.setVisibility(View.GONE);
		} else if(item.getMessageNum() == -1) {
			viewHolder.emotionSwitching.setDisplayedChild(0);
			viewHolder.emotionSwitching.setVisibility(View.VISIBLE);
			viewHolder.messageSwitching.setDisplayedChild(1);
			viewHolder.warning.setVisibility(View.GONE);
			viewHolder.message.setVisibility(View.GONE);
			viewHolder.emotion.setVisibility(View.GONE);
			viewHolder.time.setVisibility(View.GONE);
		} else {
			if (item.getTextResult() != null) {
				if ((item.getConfidence() > Constants.CONFIDENCE) || item.getConfidence() == 0.0)
					viewHolder.warning.setVisibility(View.GONE);
				else
					viewHolder.warning.setVisibility(View.VISIBLE);
					
				viewHolder.message.setVisibility(View.VISIBLE);
				viewHolder.message.setText(item.getTextResult());
				viewHolder.messageSwitching.setDisplayedChild(2);

			} else {
				viewHolder.warning.setVisibility(View.GONE);
				viewHolder.message.setVisibility(View.GONE);
				viewHolder.messageSwitching.setDisplayedChild(1);
			}

			viewHolder.emotionSwitching.setVisibility(View.VISIBLE);
			switch (item.getEmotionType()) {
			case Constants.SAD:
				viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_sad);
				viewHolder.emotion.setText(Constants.STR_SAD);
				viewHolder.emotionSwitching.setDisplayedChild(1);
				break;
			case Constants.NATURAL:
				viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_natural);
				viewHolder.emotion.setText(Constants.STR_NATURAL);
				viewHolder.emotionSwitching.setDisplayedChild(1);
				break;
			case Constants.ANGRY:
				viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_angry);
				viewHolder.emotion.setText(Constants.STR_ANGRY);
				viewHolder.emotionSwitching.setDisplayedChild(1);
				break;
			case Constants.HAPPY:
				viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_happy);
				viewHolder.emotion.setText(Constants.STR_HAPPY);
				viewHolder.emotionSwitching.setDisplayedChild(1);
				break;
			default:
				viewHolder.emotion.setVisibility(View.GONE);
				viewHolder.emotionSwitching.setDisplayedChild(0);
				break;
			}
			
			if (item.getTime() != null) {
				viewHolder.time.setVisibility(View.VISIBLE);
				viewHolder.time.setText(item.getTime());
			}
			else {
				viewHolder.time.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
}
