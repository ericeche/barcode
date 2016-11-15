package com.example.uhfxintong;

import java.util.ArrayList;
import java.util.List;

import com.example.uhfxintong.adapter.HistoryAdapter;
import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.db.UhfService;
import com.example.uhfxintong.views.MyInnerListView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class HistoryActivityNew extends Activity {
	private ListView listView;
	private List<Uhf> arrayList = new ArrayList<Uhf>();
	private HistoryAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_history_new);
		listView = (ListView) this.findViewById(R.id.listView);
		UhfService uhfService = new UhfService(this);
		arrayList = uhfService.getAllHistoryUhf();
		adapter = new HistoryAdapter(this, arrayList, this.findViewById(R.id.main));
		for(Uhf uhf : arrayList) {
//			Log.i("a", "uhf = " + uhf.toString());
		}
		listView.setAdapter(adapter);
	}
	
	
	public void btnClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			this.finish();
			break;
		}
	}
}
