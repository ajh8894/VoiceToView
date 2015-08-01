package com.swmem.voicetoview.view;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class AssistantView implements OnClickListener {
	private Handler mHandler;
	private View mView;

	private Animation mAnimation;
	private ImageView mCurEmotionIV;

	private RadarChart mTotalEmotionChart;
	private List<Entry> mTotalEmotionList;

	private ListView mListView;
	private List<Model> mModelList;
	private ModelListAdapter mListAdapter;

	public AssistantView(Context c, WindowManager wManager, Handler mHandler) {
		this.mHandler = mHandler;
		this.mAnimation = AnimationUtils.loadAnimation(c, R.anim.abc_fade_in);
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mView = inflater.inflate(R.layout.view_assistant, null);
		((ImageView) mView.findViewById(R.id.iv_hide)).setOnClickListener(this);
		this.loadContact(c.getContentResolver());
		this.mTotalEmotionChart = (RadarChart) mView.findViewById(R.id.chart_emotion);
		this.mCurEmotionIV = (ImageView) mView.findViewById(R.id.iv_current_emotion);
		emotionChartsetData();

		this.mListView = (ListView) mView.findViewById(R.id.lv_model);
		this.mModelList = new ArrayList<Model>();
		this.mListAdapter = new ModelListAdapter(c, R.layout.item_model, mModelList);
		this.mListView.setAdapter(mListAdapter);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,

				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;

		mView.setVisibility(View.VISIBLE);
		wManager.addView(mView, params);
	}

	
	private void loadContact(ContentResolver contentResolver) {
		String name = null;
		String number = Connection.header[2].replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");

		Cursor cursor = contentResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
				new String[] { number }, null);

		if(cursor.moveToFirst())
			name = cursor.getString(0);
		
		cursor.close();
		

		if(name != null) {
			((TextView) mView.findViewById(R.id.tv_phonenumber)).setText(number);
			((TextView) mView.findViewById(R.id.tv_name)).setText(name);
		} else {
			((TextView) mView.findViewById(R.id.tv_phonenumber)).setVisibility(View.INVISIBLE);
			((TextView) mView.findViewById(R.id.tv_name)).setText(number);
		}
	}
	

	private void emotionChartsetData() {
		mTotalEmotionChart.setDescription("");
		mTotalEmotionChart.setRotationEnabled(false);
		mTotalEmotionChart.setClickable(false);

		mTotalEmotionChart.setDrawWeb(true);
		mTotalEmotionChart.setWebLineWidth(0);
		mTotalEmotionChart.setWebLineWidthInner(0.75f);
		mTotalEmotionChart.setWebAlpha(100);

		mTotalEmotionList = new ArrayList<Entry>();
		mTotalEmotionList.add(new Entry(0, Constants.SAD));
		mTotalEmotionList.add(new Entry(0, Constants.NATURAL));
		mTotalEmotionList.add(new Entry(0, Constants.ANGRY));
		mTotalEmotionList.add(new Entry(0, Constants.HAPPY));

		String[] xVal = { Constants.STR_SAD, Constants.STR_NATURAL,
				Constants.STR_ANGRY, Constants.STR_HAPPY };

		RadarDataSet set = new RadarDataSet(mTotalEmotionList, "°¨Á¤");
		// [0 : ÃÊ·Ï / 2 : ÁÖÈ² / 3 : ÆÄ¶û / 4 : »¡°­ ]
		set.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
		set.setDrawFilled(true);
		set.setLineWidth(2f);

		RadarData radarData = new RadarData(xVal, set);
		radarData.setValueTextSize(10f);
		radarData.setDrawValues(false);

		mTotalEmotionChart.getXAxis().setTextSize(8f);
		mTotalEmotionChart.getXAxis().setTextColor(Color.parseColor("#000000"));

		YAxis yAxis = mTotalEmotionChart.getYAxis();
		yAxis.setEnabled(false);
		yAxis.setLabelCount(4);
		yAxis.setAxisMaxValue(20);

		Legend legend = mTotalEmotionChart.getLegend();
		legend.setEnabled(false);

		mTotalEmotionChart.setData(radarData);
		mTotalEmotionChart.invalidate();
	}

	public View getView() {
		return mView;
	}

	public List<Model> getModelList() {
		return mModelList;
	}

	public void setCurrentEmotion(int emotion) {
		switch (emotion) {
		case Constants.SAD:
			mCurEmotionIV.setImageResource(R.drawable.current_emotion_sad);
			break;
		case Constants.NATURAL:
			mCurEmotionIV.setImageResource(R.drawable.current_emotion_natural);
			break;
		case Constants.ANGRY:
			mCurEmotionIV.setImageResource(R.drawable.current_emotion_angry);
			break;
		case Constants.HAPPY:
			mCurEmotionIV.setImageResource(R.drawable.current_emotion_happy);
			break;
		default:
			mCurEmotionIV.setVisibility(View.INVISIBLE);
			break;
		}
		mCurEmotionIV.startAnimation(mAnimation);
	}
	
	public void modelListRemove() {
		for (Model model : mModelList) {
			if(model.getMessageNum() == -2) {
				mModelList.remove(model);
				mListAdapter.notifyDataSetChanged();
			}
		}
	}
	
	public void modelListFailRemove() {
		for (Model model : mModelList) {
			if(model.getMessageNum() == -1) {
				mModelList.remove(model);
				mListAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	public void modelListAdd(Model m) {
		if (m != null) {
			if(m.getMessageNum() == -2) {
				mModelList.add(m);
				mListAdapter.notifyDataSetChanged();
				return;
			}
			
			for (Model model : mModelList) {
				if (m.getMessageNum() == -1 && model.getMessageNum() == -2) {
					model.setMessageNum(m.getMessageNum());
					Model ready = new Model();
					ready.setMessageNum(-2);
					mModelList.add(ready);
					mListAdapter.notifyDataSetChanged();
					break;
				} else if (model.getMessageNum() == m.getMessageNum()) {
					if (m.getTextResult() != null) {
						if (model.getTextResult() == null) {
							model.setTextResult(m.getTextResult());
							model.setConfidence(m.getConfidence());
							mListAdapter.notifyDataSetChanged();
							break;
						}
					} else if (m.getEmotionType() != 0) {
						if (model.getEmotionType() == 0) {
							model.setEmotionType(m.getEmotionType());
							setCurrentEmotion(m.getEmotionType());

							Entry emoEntry = mTotalEmotionList.get(m.getEmotionType() - 1);
							emoEntry.setVal(emoEntry.getVal() + 1);
							mTotalEmotionChart.notifyDataSetChanged();
							mListAdapter.notifyDataSetChanged();
							break;
						}
					} 
				} else if(model.getMessageNum() == -1) {
					model.setMessageNum(m.getMessageNum());
					if(m.getEmotionType() != 0) {
						model.setEmotionType(m.getEmotionType());
						
						setCurrentEmotion(m.getEmotionType());
						Entry emoEntry = mTotalEmotionList.get(m.getEmotionType() - 1);
						emoEntry.setVal(emoEntry.getVal() + 1);
						mTotalEmotionChart.notifyDataSetChanged();
					} else {
						model.setTextResult(m.getTextResult());
						model.setConfidence(m.getConfidence());	
					}
					mListAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
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
