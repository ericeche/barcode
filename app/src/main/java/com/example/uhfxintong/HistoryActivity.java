package com.example.uhfxintong;

import java.util.ArrayList;
import java.util.List;

import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.db.UhfService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
	private ImageView img1 = null;
	private ImageView img2 = null;
	private ImageView img3 = null;
	private ListView listView = null;
	private BaseAdapter listAdapter = null;
	private List<Uhf> arrayList = new ArrayList<Uhf>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		img1 = (ImageView)findViewById(R.id.img1);
		img2 = (ImageView)findViewById(R.id.img2);
		img3 = (ImageView)findViewById(R.id.img3);
		listView = (ListView)findViewById(R.id.history_listView);
		listAdapter = new HistoryListAdapter();
		UhfService uhfService = new UhfService(this);
		arrayList = uhfService.getAllHistoryUhf();
		for(Uhf uhf : arrayList) {
			Log.i("a", "uhf = " + uhf.toString());
		}
		listView.setAdapter(listAdapter);
	}
	
	private class HistoryListAdapter extends BaseAdapter{
		private TextView numberTV = null;
		private TextView shebeiTV = null;
		private TextView quexianTV = null;
		private TextView renyuanTV = null;
		private TextView xunshiDateTV = null;
		private TextView tianbaoDateTV = null;
		private View itemLayout = null;
		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(R.layout.history_listitem, null);
			Uhf u = arrayList.get(position);
			itemLayout = convertView.findViewById(R.id.item_layout);
			numberTV = (TextView)convertView.findViewById(R.id.number);
			shebeiTV = (TextView)convertView.findViewById(R.id.pandian_listitem_shebeimingcheng2);
			quexianTV = (TextView)convertView.findViewById(R.id.pandian_listitem_quexianneirong2);
			renyuanTV = (TextView)convertView.findViewById(R.id.pandian_listitem_xunshirenyuan2);
			xunshiDateTV = (TextView)convertView.findViewById(R.id.pandian_listitem_xunshishijian2);
			tianbaoDateTV = (TextView)convertView.findViewById(R.id.pandian_listitem_tianbaoshijian2);
			numberTV.setText(position+1+"");
			shebeiTV.setText(u.getUhfName());
			System.out.println(arrayList.get(position).getDefect()==null);
			if(arrayList.get(position).getDefect()==null || arrayList.get(position).getDefect().equals("")){
				itemLayout.setBackgroundResource(R.drawable.history_bg_green);
				numberTV.setBackgroundResource(R.drawable.text_circle_green_bg);
				quexianTV.setText("无");
			}else{
				itemLayout.setBackgroundResource(R.drawable.history_bg_red);
				numberTV.setBackgroundResource(R.drawable.text_circle_red_bg);
				quexianTV.setText(u.getDefect());
			}
			renyuanTV.setText(u.getOperator());
			xunshiDateTV.setText(u.getTime());
			if(arrayList.get(position).getNotes()==null || arrayList.get(position).getNotes().equals("")){
				tianbaoDateTV.setText("无");
			}else{
				tianbaoDateTV.setText(u.getNotes());
			}
			return convertView;
		}
		
	}
}
