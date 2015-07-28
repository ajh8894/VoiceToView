package com.swmem.voicetoview.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.swmem.voicetoview.R;
import com.swmem.voicetoview.data.Talk;
import com.swmem.voicetoview.util.Database;

public class TalkFragment extends Fragment {
	private boolean mType;
	private Talk mTalk;
	private ListView mListView;
	
	public TalkFragment() {
		this.mType = false;
	}
	
	public TalkFragment(boolean type, Talk talk) {
		this.mType = type;
		this.mTalk = talk;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_talk, container, false);
		mListView = (ListView) view.findViewById(R.id.lv_talk);
		
		if (!mType) {
			view.setBackgroundColor(Color.WHITE);
			((TextView) view.findViewById(R.id.tv_date)).setText("date");
			((TextView) view.findViewById(R.id.tv_id)).setText("id");
			
			final TalkListAdapter talkListAdapter = new TalkListAdapter(view.getContext(), R.layout.item_talk, Database.selectTalkList());
			this.mListView.setAdapter(talkListAdapter);
			this.mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					mTalk = (Talk) parent.getItemAtPosition(position);
					
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.addToBackStack(null);
					TalkFragment fragment = new TalkFragment(true, mTalk);
					ft.replace(R.id.frag_talk, fragment);
					ft.addToBackStack(null);
					ft.commit();
				}
			});
			this.mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						Database.deleteTalk(((Talk)parent.getItemAtPosition(position)).getKey());
						talkListAdapter.notifyDataSetChanged();
						return false;
					}
			});
		} else {
			view.setBackgroundColor(Color.WHITE);
			((TextView) view.findViewById(R.id.tv_date)).setText(mTalk.getDate());
			((TextView) view.findViewById(R.id.tv_id)).setText(mTalk.getId());
			
			this.mListView.setAdapter(new ModelListAdapter(getActivity(), R.layout.item_model, Database.selectModelList(mTalk.getKey())));
		}
		return view;
	}

}
