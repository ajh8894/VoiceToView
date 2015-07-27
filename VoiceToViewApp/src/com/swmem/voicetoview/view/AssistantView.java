package com.swmem.voicetoview.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class AssistantView implements OnClickListener {
	private View view;
	private Animation animation;
	private Handler handler;
	private RadarChart totalEmotionChart;
	private List<Entry> totalEmotion;
	private ImageView currentEmotion;
	private ListView listView;
	private ModelListAdapter listAdapter;
	private List<Model> modelList;

	public AssistantView(Context c, WindowManager wManager, Handler handler) {
		this.animation = AnimationUtils.loadAnimation(c, R.anim.abc_fade_in);
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = inflater.inflate(R.layout.view_assistant, null);
		this.handler = handler;
		this.totalEmotionChart = (RadarChart) view.findViewById(R.id.chart_emotion);
		this.currentEmotion = (ImageView) view.findViewById(R.id.iv_current_emotion);
		this.listView = (ListView) view.findViewById(R.id.lv_model);
		this.modelList = new ArrayList<Model>();
		this.listAdapter = new ModelListAdapter(c, R.layout.item_model, modelList);
		this.listView.setAdapter(listAdapter);
		ImageView hideIV = (ImageView) view.findViewById(R.id.iv_hide);
		hideIV.setOnClickListener(this);
		emotionChartsetData();

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,

				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;

		view.setVisibility(View.VISIBLE);
		wManager.addView(view, params);
	}

	private void emotionChartsetData() {
		totalEmotionChart.setDescription("");
		totalEmotionChart.setRotationEnabled(false);
		totalEmotionChart.setClickable(false);

		totalEmotionChart.setDrawWeb(true);
		totalEmotionChart.setWebLineWidth(0);
		totalEmotionChart.setWebLineWidthInner(0.75f);
		totalEmotionChart.setWebAlpha(100);

		totalEmotion = new ArrayList<Entry>();
		totalEmotion.add(new Entry(1, Constants.SAD));
		totalEmotion.add(new Entry(1, Constants.NATURAL));
		totalEmotion.add(new Entry(1, Constants.ANGRY));
		totalEmotion.add(new Entry(1, Constants.HAPPY));

		String[] xVal = { Constants.STR_SAD, Constants.STR_NATURAL,
				Constants.STR_ANGRY, Constants.STR_HAPPY };

		RadarDataSet set = new RadarDataSet(totalEmotion, "°¨Á¤");
		// [0 : ÃÊ·Ï / 2 : ÁÖÈ² / 3 : ÆÄ¶û / 4 : »¡°­ ]
		set.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
		set.setDrawFilled(true);
		set.setLineWidth(2f);

		RadarData radarData = new RadarData(xVal, set);
		radarData.setValueTextSize(10f);
		radarData.setDrawValues(false);

		totalEmotionChart.getXAxis().setTextSize(8f);
		totalEmotionChart.getXAxis().setTextColor(Color.parseColor("#000000"));

		YAxis yAxis = totalEmotionChart.getYAxis();
		yAxis.setEnabled(false);
		yAxis.setLabelCount(4);
		yAxis.setAxisMaxValue(50);

		Legend legend = totalEmotionChart.getLegend();
		legend.setEnabled(false);

		totalEmotionChart.setData(radarData);
		totalEmotionChart.invalidate();
	}

	public View getView() {
		return view;
	}

	public List<Model> getModelList() {
		return modelList;
	}

	public void setCurrentEmotion(int emotion) {
		switch (emotion) {
		case Constants.SAD:
			currentEmotion.setImageResource(R.drawable.current_emotion_sad);
			break;
		case Constants.NATURAL:
			currentEmotion.setImageResource(R.drawable.current_emotion_natural);
			break;
		case Constants.ANGRY:
			currentEmotion.setImageResource(R.drawable.current_emotion_angry);
			break;
		case Constants.HAPPY:
			currentEmotion.setImageResource(R.drawable.current_emotion_happy);
			break;
		default:
			currentEmotion.setVisibility(View.INVISIBLE);
			break;
		}
		currentEmotion.startAnimation(animation);
	}

	synchronized public void modelListAdd(Model m) {
		if (m != null) {
			for (Model model : modelList) {
				if (m.getMessageNum() == model.getMessageNum()) {
					if (m.getTextResult() != null) {
						if (model.getTextResult() == null) {
							model.setTextResult(m.getTextResult());
							listAdapter.notifyDataSetChanged();
							return;
						}
					} else if (m.getEmotionType() != 0) {
						if (model.getEmotionType() == 0) {
							model.setEmotionType(m.getEmotionType());
							setCurrentEmotion(m.getEmotionType());

							Entry emoEntry = totalEmotion.get(m
									.getEmotionType() - 1);
							emoEntry.setVal(emoEntry.getVal() + 1);
							totalEmotionChart.notifyDataSetChanged();

							listAdapter.notifyDataSetChanged();
							return;
						}
					}
				}
			}

			if (m.getEmotionType() != 0) {
				setCurrentEmotion(m.getEmotionType());

				Entry emoEntry = totalEmotion.get(m.getEmotionType() - 1);
				emoEntry.setVal(emoEntry.getVal() + 1);
				totalEmotionChart.notifyDataSetChanged();

			}
			modelList.add(m);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_hide:
			handler.sendEmptyMessage(Constants.SWAP);
			break;
		}
	}
}
