package com.swmem.voicetoview.view;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
			final List<Talk> talkList = Database.selectTalkList();
			final TalkListAdapter talkListAdapter = new TalkListAdapter(view.getContext(), R.layout.item_talk, talkList);
			this.mListView.setClickable(true);
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
						final AdapterView<?> p = parent;
						final int pos = position;
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("삭제하시겠습니까?")
						.setCancelable(true)
						.setPositiveButton("예", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								Database.deleteTalk(((Talk)p.getItemAtPosition(pos)).getKey());
								talkList.remove((Talk)p.getItemAtPosition(pos));
								talkListAdapter.notifyDataSetChanged();
							}
						})
						.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.show();
						
						return true;
					}
			});
		} else {
			((TextView) view.findViewById(R.id.tv_date)).setTextSize(15f);
			((TextView) view.findViewById(R.id.tv_id)).setTextSize(15f);
			((TextView) view.findViewById(R.id.tv_date)).setText(mTalk.getDate());
			((TextView) view.findViewById(R.id.tv_id)).setText(mTalk.getId());
			
			this.mListView.setDividerHeight(0);
			this.mListView.setClickable(false);
			this.mListView.setAdapter(new ModelListAdapter(getActivity(), R.layout.item_model, Database.selectModelList(mTalk.getKey())));
		}
		return view;
	}

}
