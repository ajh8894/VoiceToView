package com.swmem.voicetoview.view;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
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
		
		loadContact(c.getContentResolver());
		((TextView) mView.findViewById(R.id.tv_phonenumber)).setText(Connection.header[1]);
		((ImageView) mView.findViewById(R.id.iv_hide)).setOnClickListener(this);
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

	public void loadContact(ContentResolver contentResolver)
	{
		String name = null;

		Cursor cursor = contentResolver
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
						ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
						new String[] { Connection.header[1] }, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			name = cursor.getString(0);
		} else
			name = null;

		cursor.close();
		
		if(name != null)
			((TextView) mView.findViewById(R.id.tv_name)).setText(name);
		else
			((TextView) mView.findViewById(R.id.tv_name)).setText("없는 번호");
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

		RadarDataSet set = new RadarDataSet(mTotalEmotionList, "감정");
		// [0 : 초록 / 2 : 주황 / 3 : 파랑 / 4 : 빨강 ]
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

	public void modelListAdd(Model m) {
		if (m != null) {
			for (Model model : mModelList) {
				if (m.getMessageNum() == model.getMessageNum()) {
					if (m.getTextResult() != null) {
						if (model.getTextResult() == null) {
							model.setTextResult(m.getTextResult());
							mListAdapter.notifyDataSetChanged();
							return;
						}
					} else if (m.getEmotionType() != 0) {
						if (model.getEmotionType() == 0) {
							model.setEmotionType(m.getEmotionType());
							setCurrentEmotion(m.getEmotionType());

							Entry emoEntry = mTotalEmotionList.get(m.getEmotionType() - 1);
							emoEntry.setVal(emoEntry.getVal() + 1);
							mTotalEmotionChart.notifyDataSetChanged();
							mListAdapter.notifyDataSetChanged();
							return;
						}
					}
				}
			}

			if (m.getEmotionType() != 0) {
				setCurrentEmotion(m.getEmotionType());

				Entry emoEntry = mTotalEmotionList.get(m.getEmotionType() - 1);
				emoEntry.setVal(emoEntry.getVal() + 1);
				mTotalEmotionChart.notifyDataSetChanged();
			}
			mModelList.add(m);
			mListAdapter.notifyDataSetChanged();
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
