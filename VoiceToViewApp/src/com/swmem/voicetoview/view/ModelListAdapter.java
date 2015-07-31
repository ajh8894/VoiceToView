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
		public ImageView warning;
		public ViewAnimator emotionSwitching;
		public ViewAnimator messageSwitching;
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
			viewHolder.emotionSwitching = (ViewAnimator) convertView.findViewById(R.id.va_emotion);
			
			viewHolder.warning = (ImageView) viewHolder.messageSwitching.getChildAt(1).findViewById(R.id.iv_warning);
			viewHolder.message = (TextView) viewHolder.messageSwitching.getChildAt(1).findViewById(R.id.tv_message);
			viewHolder.emotion = (TextView) viewHolder.emotionSwitching.getChildAt(1);
			
			//viewHolder.warning = (ImageView) convertView.findViewById(R.id.iv_warning);
			//viewHolder.emotion = (TextView) convertView.findViewById(R.id.tv_emotion);
			//viewHolder.message = (TextView) convertView.findViewById(R.id.tv_message);
			viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Model item = getItem(position);

		if(item.getMessageNum() == -1) {
			viewHolder.warning.setVisibility(View.VISIBLE);
			viewHolder.messageSwitching.setVisibility(View.GONE);
			viewHolder.emotionSwitching.setVisibility(View.GONE);
		} else {
			viewHolder.warning.setVisibility(View.GONE);
			viewHolder.messageSwitching.setVisibility(View.VISIBLE);
			viewHolder.emotionSwitching.setVisibility(View.VISIBLE);
		}
		
		if (item.getConfidence() < Constants.CONFIDENCE)
			viewHolder.warning.setVisibility(View.GONE);
		else
			viewHolder.warning.setVisibility(View.VISIBLE);
		
		if (item.getTextResult() != null) {
			viewHolder.message.setText(item.getTextResult());
			if(viewHolder.messageSwitching.getCurrentView() == viewHolder.messageSwitching.getChildAt(0)) {
				viewHolder.messageSwitching.showNext();
			}
		}
		else {
			if(viewHolder.messageSwitching.getCurrentView() == viewHolder.messageSwitching.getChildAt(1)) {
				viewHolder.messageSwitching.showPrevious();
			}
		}
		
		if (item.getTime() != null)
			viewHolder.time.setText(item.getTime());
		else
			viewHolder.time.setText(null);
		
		switch (item.getEmotionType()) {
		case Constants.SAD:
			//viewHolder.emotion.setVisibility(View.VISIBLE);
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_sad);
			viewHolder.emotion.setText(Constants.STR_SAD);
			if(viewHolder.emotionSwitching.getCurrentView() == viewHolder.emotionSwitching.getChildAt(0)) {
				viewHolder.emotionSwitching.showNext();
			}
			break;
		case Constants.NATURAL:
			//viewHolder.emotion.setVisibility(View.VISIBLE);
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_natural);
			viewHolder.emotion.setText(Constants.STR_NATURAL);
			if(viewHolder.emotionSwitching.getCurrentView() == viewHolder.emotionSwitching.getChildAt(0)) {
				viewHolder.emotionSwitching.showNext();
			}
			break;
		case Constants.ANGRY:
			//viewHolder.emotion.setVisibility(View.VISIBLE);
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_angry);
			viewHolder.emotion.setText(Constants.STR_ANGRY);
			if(viewHolder.emotionSwitching.getCurrentView() == viewHolder.emotionSwitching.getChildAt(0)) {
				viewHolder.emotionSwitching.showNext();
			}
			break;
		case Constants.HAPPY:
			//viewHolder.emotion.setVisibility(View.VISIBLE);
			viewHolder.emotion.setBackgroundResource(R.drawable.model_icon_happy);
			viewHolder.emotion.setText(Constants.STR_HAPPY);
			if(viewHolder.emotionSwitching.getCurrentView() == viewHolder.emotionSwitching.getChildAt(0)) {
				viewHolder.emotionSwitching.showNext();
			}
			break;
		default:
			if(viewHolder.emotionSwitching.getCurrentView() == viewHolder.emotionSwitching.getChildAt(1)) {
				viewHolder.emotionSwitching.showPrevious();
			}
			//viewHolder.emotionSwitching.getChildAt(0).setVisibility(View.GONE);
			//viewHolder.emotion.setVisibility(View.GONE);
			break;
		}

		return convertView;
	}
}
