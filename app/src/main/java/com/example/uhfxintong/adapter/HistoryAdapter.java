package com.example.uhfxintong.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhfxintong.QueXianInputActivity;
import com.example.uhfxintong.R;
import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.views.MyInnerGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HistoryAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context context;
	private List<Uhf> uhfs;
	private HistoryPicAdapter picAdapter;
	private PopupWindow imgPw;
	GuideActivityPagerViewAdapter mViewPager;
	private View parentView;
	public HistoryAdapter(Context context, List<Uhf> uhfs, View parentView) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.uhfs = uhfs;
		this.parentView = parentView;
	}
	
	@Override
	public int getCount() {
		return uhfs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return uhfs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder vh;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.item_lishijilu, null);
			vh = new ViewHolder();
			vh.qxLayout = (RelativeLayout) convertView.findViewById(R.id.qxLayout);
			vh.yxLayout = (RelativeLayout) convertView.findViewById(R.id.yxLayout);
			vh.yxTime = (TextView) convertView.findViewById(R.id.yxtime);
			vh.qxTime = (TextView) convertView.findViewById(R.id.qxtime);
			vh.yxDate = (TextView) convertView.findViewById(R.id.yxDate);
			vh.qxDate = (TextView) convertView.findViewById(R.id.qxDate);
			vh.yxSbName = (TextView) convertView.findViewById(R.id.yxsbName);
			vh.qxSbName = (TextView) convertView.findViewById(R.id.qxsbName);
			vh.yxXsrName = (TextView) convertView.findViewById(R.id.yxxsrName);
			vh.qxXsrName = (TextView) convertView.findViewById(R.id.qxxsrName);
			vh.yxJwd = (TextView) convertView.findViewById(R.id.yxjwd);
			vh.qxJwd = (TextView) convertView.findViewById(R.id.qxjwd);
			vh.yxBz = (TextView) convertView.findViewById(R.id.yxbz);
			vh.qxBz = (TextView) convertView.findViewById(R.id.qxbz);
			vh.qx = (TextView) convertView.findViewById(R.id.qx);
			vh.qxGridView = (MyInnerGridView) convertView.findViewById(R.id.qxgrid);
			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		final Uhf u = uhfs.get(position);
		if((u.getDefect() != null && !"".equals(u.getDefect())) || (u.getPhotos() != null && !"".equals(u.getPhotos()))) {
			vh.qxLayout.setVisibility(View.VISIBLE);
			vh.yxLayout.setVisibility(View.GONE);
			vh.qxSbName.setText(u.getUhfName());
			vh.qxXsrName.setText(u.getOperator());
			if(u.getDefect() == null || "".equals(u.getDefect())) {
				vh.qx.setText("无缺陷内容");
			}else {
				vh.qx.setText(u.getDefect());
			}
			if(u.getNotes() == null ||"".equals(u.getNotes())) {
				vh.qxBz.setText("无备注内容");
			}else {
				vh.qxBz.setText(u.getNotes());
			}
			if(u.getPhotos() != null && !"".equals(u.getPhotos())) {
				picAdapter = new HistoryPicAdapter(context, u.getPhotos());
				vh.qxGridView.setAdapter(picAdapter);
				vh.qxGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String str = u.getPhotos();
						if(str != null && !"".equals(str)) {
							ArrayList<String> photos = new ArrayList<String>();
							String[] ss = str.split(",");
							for(String s : ss) {
								photos.add(s);
							}
							initPopwindow(arg2, photos, parentView);
						}
						
					}
				});
			}
			String dateTime = u.getTime();
			String date = dateTime.substring(0, dateTime.lastIndexOf(" "));
			String time = dateTime.substring(dateTime.lastIndexOf(" ") + 1, dateTime.length());
			vh.qxDate.setText(date);
			vh.qxTime.setText(time);
			
		}else {
			vh.qxLayout.setVisibility(View.GONE);
			vh.yxLayout.setVisibility(View.VISIBLE);
			vh.yxSbName.setText(u.getUhfName());
			vh.yxXsrName.setText(u.getOperator());
			if(u.getNotes() == null || "".equals(u.getNotes())) {
				vh.yxBz.setText("无备注内容");
			}else {
				vh.yxBz.setText(u.getNotes());
			}
			String dateTime = u.getTime();
			String date = dateTime.substring(0, dateTime.lastIndexOf(" "));
			String time = dateTime.substring(dateTime.lastIndexOf(" ") + 1, dateTime.length());
			vh.yxDate.setText(date);
			vh.yxTime.setText(time);
		}
		return convertView;
	}
	
	static class ViewHolder {
		RelativeLayout qxLayout, yxLayout;
		TextView yxTime, qxTime,yxSbName, qxSbName,yxXsrName, qxXsrName,yxJwd,qxJwd,yxBz, qxBz, qx, yxDate, qxDate;
		MyInnerGridView qxGridView;
	}
	
	private void initPopwindow(int position, List<String> imgUrls, View parentId) {
		View popContentView = inflater.inflate(
				R.layout.image_preview_viewpager, null);
		ImageView closeIv = (ImageView) popContentView.findViewById(R.id.back);
		closeIv.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (imgPw.isShowing()) {
					imgPw.dismiss();
				}
			}
		});
		ViewPager mViewPager = (ViewPager) popContentView
				.findViewById(R.id.viewPager);
		ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
		for(String s : imgUrls) {
			ImageView iv = new ImageView(context);
			ImageLoader.getInstance().displayImage("file:///" + s, iv);
			imageViews.add(iv);
		}
		LinearLayout point_layout = (LinearLayout) popContentView.findViewById(R.id.point_layout);
        initPointLayout(imageViews.size() ,point_layout);
		
		mViewPager.setAdapter(new GuideActivityPagerViewAdapter(imageViews));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
				
				public void onPageSelected(int arg0) {
					for(int i=0; i<guide_points.length; i++) {
						guide_points[i].setBackgroundResource(R.drawable.pagectr_inactive);
						if(arg0 == i) {
							guide_points[i].setBackgroundResource(R.drawable.pagectr_active);
						}
					}
				}
				
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
				
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		mViewPager.setCurrentItem(position);
		imgPw = new PopupWindow(popContentView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imgPw.showAtLocation(parentView, Gravity.TOP,
				0, 0);
	}
	private ImageView[] guide_points = null;
	private ImageView point = null;
	private void initPointLayout(int imageSize, LinearLayout point_layout) {
    	guide_points = new ImageView[imageSize];
    	for(int i=0; i<imageSize; i++) {
    		point = new ImageView(context);
    		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
    		lp.setMargins(10, 0, 10, 0);
    		point.setLayoutParams(lp);
    		guide_points[i] = point;
    		if(i==0) {
    			point.setBackgroundResource(R.drawable.pagectr_active);
    		}else {
    			point.setBackgroundResource(R.drawable.pagectr_inactive);
    		}
    		point_layout.addView(point);
    	}
    }
	
	
	
	

}
