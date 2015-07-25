package com.swmem.voicetoview.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
	private boolean type;
	private Talk talk;
	private ListView listView;
	
	public TalkFragment() {
		this.type = false;
	}
	
	public TalkFragment(boolean type, Talk talk) {
		this.type = type;
		this.talk = talk;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_talk, container, false);
		listView = (ListView) view.findViewById(R.id.lv_talk);
		
		if (!type) {
			view.setBackgroundColor(Color.WHITE);
			((TextView) view.findViewById(R.id.tv_date)).setText("date");
			((TextView) view.findViewById(R.id.tv_id)).setText("id");
			
			final TalkListAdapter talkListAdapter = new TalkListAdapter(view.getContext(), R.layout.item_talk, Database.selectTalkList());
			this.listView.setAdapter(talkListAdapter);
			this.listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					talk = (Talk) parent.getItemAtPosition(position);
					
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.addToBackStack(null);
					TalkFragment fragment = new TalkFragment(true, talk);
					ft.replace(R.id.frag_talk, fragment);
					ft.addToBackStack(null);
					ft.commit();
				}
			});
			this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						Database.deleteTalk(((Talk)parent.getItemAtPosition(position)).getKey());
						talkListAdapter.notifyDataSetChanged();
						return false;
					}
			});
		} else {
			view.setBackgroundColor(Color.WHITE);
			((TextView) view.findViewById(R.id.tv_date)).setText(talk.getDate());
			((TextView) view.findViewById(R.id.tv_id)).setText(talk.getId());
			
			this.listView.setAdapter(new ModelListAdapter(getActivity(), R.layout.item_model, Database.selectModelList(talk.getKey())));
		}
		return view;
	}

}
