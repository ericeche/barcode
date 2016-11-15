package com.example.uhfxintong;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.uhfxintong.adapter.GuideActivityPagerViewAdapter;
import com.example.uhfxintong.adapter.PhotoGridViewAdapter;
import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.eventbus.DefectListener;
import com.example.uhfxintong.util.OpenCameraUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class QueXianInputActivity extends Activity {
	private EditText qxContent;
	private GridView gridView;
	private List<String> photoPaths = new ArrayList<String>();
	private int addPic = R.drawable.add_icon;
	private PhotoGridViewAdapter adapter;
	public static final int GET_IMAGE_VIA_CAMERA = 5000;
	private String localTempImgDir = "localTempImgDir";
//	private String rfidDir = "";
	private Uhf uhf;
	private int position = 0;
	private String localTempImgFileName = "test.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.acctivity_quexian_inpput);
//		rfidDir = this.getIntent().getStringExtra("rifd");
		uhf = (Uhf) this.getIntent().getSerializableExtra("uhf");
		position = this.getIntent().getIntExtra("position", 0);
		qxContent = (EditText) this.findViewById(R.id.quexianContent);
		gridView = (GridView) this.findViewById(R.id.gridView);
		photoPaths.add("" + R.drawable.add_icon);
		qxContent.setText(uhf.getDefect());
		if(!"".equals(uhf.getPhotos())) {
			String[] photos = uhf.getPhotos().split(",");
			if(photos.length > 0) {
				for(String s : photos) {
					photoPaths.add(s);
				}
			}
		}
		adapter = new PhotoGridViewAdapter(this, photoPaths);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 == 0) {
//					OpenCameraUtils.openCameraImage(QueXianInputActivity.this);
					File dir=new File(Environment.getExternalStorageDirectory() + "/"+ localTempImgDir + "/" + uhf.getUhfId()); 
					if(!dir.exists())dir.mkdirs(); 
					SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
					long time = System.currentTimeMillis();
					Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
					localTempImgFileName = time+".jpg";
					File f=new File(dir, localTempImgFileName);//localTempImgDir和localTempImageFileName是自己定义的名字 
					Uri u=Uri.fromFile(f); 
					intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0); 
					intent.putExtra(MediaStore.EXTRA_OUTPUT, u); 
					startActivityForResult(intent, GET_IMAGE_VIA_CAMERA); 
				}else {
					List<String> realPaths = new ArrayList<String>();
					realPaths.clear();
					realPaths.addAll(photoPaths);
					realPaths.remove("" + R.drawable.add_icon);
					initPopwindow(arg2 - 1, realPaths);
				}
			}
		});
	}
	private PopupWindow imgPw;
	GuideActivityPagerViewAdapter mViewPager;
	private void initPopwindow(int position, List<String> imgUrls) {
		View popContentView = LayoutInflater.from(this).inflate(
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
			ImageView iv = new ImageView(QueXianInputActivity.this);
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
		imgPw.showAtLocation(this.findViewById(R.id.main), Gravity.TOP,
				0, 0);
	}
	private ImageView[] guide_points = null;
	private ImageView point = null;
	private void initPointLayout(int imageSize, LinearLayout point_layout) {
    	guide_points = new ImageView[imageSize];
    	for(int i=0; i<imageSize; i++) {
    		point = new ImageView(this);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_IMAGE_VIA_CAMERA:
			File f=new File(Environment.getExternalStorageDirectory() 
					+"/"+localTempImgDir + "/" + uhf.getUhfId() + "/"+localTempImgFileName); 
			Log.i("a", "f path = " + f.getAbsolutePath());
			if(f.exists()) {
				photoPaths.add(f.getAbsolutePath());
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}
	
	public void btnClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			this.finish();
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		StringBuffer sb = new StringBuffer();
		if(photoPaths.size() > 1) {
			for(int i=1; i<photoPaths.size(); i++) {
				sb.append(photoPaths.get(i)).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		EventBus.getDefault().post(new DefectListener(qxContent.getText().toString(), sb.toString(), position));
		
	}
	
}
