package com.example.uhfxintong;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.db.UhfService;
import com.example.uhfxintong.util.Accompaniment;
import com.example.uhfxintong.util.DataTransfer;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.InterrogatorModelD2;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdErrorCode;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdFrequencyPoint;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdOnIso18k6cRealTimeInventory;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdRssi;
import com.senter.support.openapi.StUhf.UII;

public class EntryActivity extends Activity {

	private static final String TAG = "UIIEntry Activity";
	private ImageView save, cancel, shutup,modify;
	private EditText uii, device, factory, volt, line, operator, time;
	private Timer mTimer;
	private TimerTask mTask;
	private TextView tv;
	SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");

	// ////////////////////////////////////////
	com.senter.support.openapi.StUhf.UII uii_change;
	private String uii_str;
	private final Accompaniment accompaniment = new Accompaniment(this,
			R.raw.tag_inventoried);
	private Handler accompainimentsHandler;
	private final Runnable accompainimentRunnable = new Runnable() {
		@Override
		public void run() {
			accompaniment.start();
			accompainimentsHandler.removeCallbacks(this);
			//uii_str = DataTransfer.xGetString(uii_change.getBytes()).substring(0, 41);
			uii_str = "039800110985";
			clear();
			try {
				Uhf uhf = getUhfById(uii_str);
				if (uhf != null) {
					uii.setText(uhf.getUhfId());
					device.setText(uhf.getUhfName());
					volt.setText(uhf.getVoltGrade());
					factory.setText(uhf.getFactory());
					line.setText(uhf.getLineSpace());
					modify.setVisibility(View.VISIBLE);
					save.setVisibility(View.GONE);
					stop();
					return;
				}
				uii.setText(uii_str);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			modify.setVisibility(View.GONE);
			save.setVisibility(View.VISIBLE);
			uii.setText(null);
			uii.append(uii_str);
			stop();
		}
	};
	// 启动
	private void start() {
		if (mTimer == null) {
			mTimer = new Timer();
		}
			mTask = new TimerTask() {
				@Override
				public void run() {
					//startInventory();
				}
			};
		mTimer.schedule(mTask, 1000, 1000);

	}

	// Stop
	private void stop() {

		App.stop();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.write_uii_view);
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.write_main_layout);
		if(getResources().getConfiguration().orientation==0){
			//竖屏
			layout.setBackgroundResource(R.drawable.pandian_bg);
		}else{
			layout.setBackgroundResource(R.drawable.pandian_bg);
		}
		tv = (TextView) findViewById(R.id.uii_tv);
		uii = (EditText) findViewById(R.id.edit_uii);
		Intent i = getIntent();
		{
			String uiiStr = i.getStringExtra("UII");
			uii.setText("039800110985");
		}
		device = (EditText) findViewById(R.id.edit_device);
		factory = (EditText) findViewById(R.id.edit_facory);
		// operator = (EditText) findViewById(R.id.edit_operator);
		volt = (EditText) findViewById(R.id.edit_volt);
		line = (EditText) findViewById(R.id.edit_line);
		// time = (EditText) findViewById(R.id.edit_time);
		// time.setText(df.format(new Date()));
		save = (ImageView) findViewById(R.id.save);
		modify=(ImageView) findViewById(R.id.modify);
		cancel = (ImageView) findViewById(R.id.cancel);
		shutup = (ImageView) findViewById(R.id.shutup);
		// //////////////////////////////////////////////////////////////////
		HandlerThread htHandlerThread = new HandlerThread("");
		htHandlerThread.start();
		accompainimentsHandler = new Handler(htHandlerThread.getLooper());
		accompaniment.init();
		// //////////////////////////////////////////////
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//startInventory();
			}
		});
		shutup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				App.stop();
				EntryActivity.this.finish();
			}
		});
		//修改标签信息
		modify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(uii == null)
					return;
				try {
					Uhf uhf=getUhfById(uii.getText().toString());
					if(uhf == null)
						return;
					uhf.setUhfName(device.getText().toString());
					uhf.setFactory(factory.getText().toString());
					uhf.setVoltGrade(volt.getText().toString());
					uhf.setLineSpace(line.getText().toString());
					UhfService service = new UhfService(EntryActivity.this);
					if(service.update(uhf)>0){
						Toast.makeText(EntryActivity.this, "数据修改成功", Toast.LENGTH_SHORT).show();
						clear();
						start();
					} else {
						Toast.makeText(EntryActivity.this, "数据修改失败", Toast.LENGTH_SHORT).show();
					}
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (BiffException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// //////////////////////////////////////////
		save.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				if (uii.getText().toString().equals("")
						|| uii.getText().toString() == null) {
					alarm();
					return;

				} else {
					Uhf uhfInfo = new Uhf();

					uhfInfo.setUhfId(uii.getText().toString());
					uhfInfo.setFactory(factory.getText().toString());
					// uhfInfo.setOperator(operator.getText().toString());
					// uhfInfo.setTime(df.format(new Date()));
					uhfInfo.setLineSpace(line.getText().toString());
					uhfInfo.setVoltGrade(volt.getText().toString());
					uhfInfo.setUhfName(device.getText().toString());
					// 插入数据库
					sqliteInsert(uhfInfo);
					try {
						getAllUhf();// 获取所有的标签信息
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					} catch (BiffException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clear();
				start();
			}

		});
	}

	// ////////////////////////////////////////////////////////////////

	protected void alarm() {

		Toast.makeText(this, "UII 不能为空", Toast.LENGTH_SHORT).show();

	}

	public void sqliteInsert(Uhf uhf) {

		UhfService service = new UhfService(this);

		if (service.saveUhf(uhf) > 0) {
			Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
			clear();
			start();
		} else {
			Toast.makeText(this, "UII 号码已经存在数据不能保存", Toast.LENGTH_SHORT).show();

		}
		;

	}

	// ///////////////////////////////////////////////////////

	public void getAllUhf() throws RowsExceededException, WriteException,
			BiffException, IOException {

		UhfService service = new UhfService(this);
		List<Uhf> uhfs = service.getAllUhf();

		for (Uhf uhf : uhfs) {
			Log.i(TAG, uhf.toString());
			System.out.println("gggggggggggggggggggggggggg");
			System.out.println(uhf.getFactory());
			System.out.println(uhf.getUhfId());
			System.out.println(uhf.getUhfName());
			// System.out.println(uhf.getTime());
			System.out.println("endendendendendendend");
		}
	}

	// ////////////////////////////////////////////////////
	public void clear() {

		System.out.println("执行数据清除");
		uii.setText("");
		device.setText("");
		factory.setText("");
		// operator.setText(null);
		volt.setText("");
		line.setText("");
		Toast.makeText(this, "清空数据成功", Toast.LENGTH_SHORT).show();
	}

	// ///////////////////////////////////////////////////////////
/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode
				|| event.getAction() == KeyEvent.ACTION_DOWN) {
			App.stop();
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
*/
	// /////////////////////////////////////////////////////////////

	protected void startInventory() {
		App.getRfid()
				.getInterrogatorAs(InterrogatorModelD2.class)
				.iso18k6cRealTimeInventory(1,
						new UmdOnIso18k6cRealTimeInventory() {

							@Override
							public void onTagInventory(UII uii,
									UmdFrequencyPoint frequencyPoint,
									Integer antennaId, UmdRssi rssi) {
								System.out
										.println("----begin onTagInventory------");
								addToplay(uii);
							}

							@Override
							public void onFinishedSuccessfully(Integer arg0,
									int arg1, int arg2) {
								System.out
										.println("-----onFinishedSuccessfully-----");
							}

							@Override
							public void onFinishedWithError(UmdErrorCode arg0) {

								System.out
										.println("------onFinishedWithError-------");

							}
						});

	}


	protected void addToplay(com.senter.support.openapi.StUhf.UII uii2) {
		uii_change = uii2;
		tagAccompainiment();

	}

	private void tagAccompainiment() {
		// accompainimentsHandler.post(accompasinimentRunnable);
		this.runOnUiThread(accompainimentRunnable);
	}
	protected Uhf getUhfById(String uii2) throws RowsExceededException,
		WriteException, BiffException, IOException {
		UhfService service = new UhfService(this);
		Uhf uhf = service.getUhfById(uii2);

		return uhf;
	}
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.write_main_layout);
		//切换为竖屏
		if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
			layout.setBackgroundResource(R.drawable.frame_bg_v);

		} 
		//切换为横屏
		else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
			layout.setBackgroundResource(R.drawable.frame_bg);
		}
	}
	public void backBtn(View v){
		this.finish();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		start();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stop();
	}
}
