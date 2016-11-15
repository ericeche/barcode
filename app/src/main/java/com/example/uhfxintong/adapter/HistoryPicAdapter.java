package com.example.uhfxintong.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.uhfxintong.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HistoryPicAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private Context context;
	private List<String> photos = new ArrayList<String>();
	private String photoStr;
	public HistoryPicAdapter(Context context, String photoStr) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.photoStr = photoStr;
		String[] ss = photoStr.split(",");
		for(String s : ss) {
			this.photos.add(s);
		}
	}
	
	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return photos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder vh;
		if(convertView == null) {
			vh = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_history_pic_grid, null);
			vh.iv = (ImageView) convertView.findViewById(R.id.photo);
			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
//		Log.i("a", "pic = " + photos.get(position));
		ImageLoader.getInstance().displayImage("file:///" + photos.get(position), vh.iv);
		return convertView;
	}
	
	static class ViewHolder {
		ImageView iv;
	}

}
