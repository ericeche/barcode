package com.example.uhfxintong.adapter;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.example.uhfxintong.R;
import com.example.uhfxintong.util.AbImageUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoGridViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context context;
	private List<String> photoPaths;
	public PhotoGridViewAdapter(Context context, List<String> photoPaths) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.photoPaths = photoPaths;
	}
	
	@Override
	public int getCount() {
		return photoPaths.size();
	}

	@Override
	public Object getItem(int arg0) {
		return photoPaths.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup arg2) {
		ViewHolder vh;
		if(contentView == null) {
			contentView = inflater.inflate(R.layout.item_pic_grid, null);
			vh = new ViewHolder();
			vh.photo = (ImageView) contentView.findViewById(R.id.photo);
			vh.deleteTag = (ImageView) contentView.findViewById(R.id.deleteTag);
			contentView.setTag(vh);
		}else {
			vh = (ViewHolder) contentView.getTag();
		}
		if(position == 0) {
			vh.deleteTag.setVisibility(View.GONE);
			vh.photo.setImageResource(R.drawable.add_icon);
		}else  {
			vh.deleteTag.setVisibility(View.VISIBLE);
			Log.i("a", "photo path = " + photoPaths.get(position));
			ImageLoader.getInstance().displayImage("file:///" + photoPaths.get(position), vh.photo);
			vh.deleteTag.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String path = photoPaths.get(position);
					File file = new File(photoPaths.get(position));
					if(file.exists()) {
						file.delete();
					}
					photoPaths.remove(path);
					notifyDataSetChanged();
				}
			});
		}
		
		
		
		return contentView;
	}

	static class ViewHolder {
		ImageView photo, deleteTag;
	}
	
	
	
	
}
