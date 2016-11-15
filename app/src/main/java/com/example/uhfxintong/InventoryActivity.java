package com.example.uhfxintong;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.example.uhfxintong.tts.ApkInstaller;
import com.example.uhfxintong.tts.TtsSettings;
import com.example.uhfxintong.util.Accompaniment;
import com.example.uhfxintong.util.DataTransfer;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.senter.support.openapi.StUhf.UII;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.InterrogatorModelD2;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdErrorCode;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdFrequencyPoint;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdOnIso18k6cRealTimeInventory;
import com.senter.support.openapi.StUhf.InterrogatorModelDs.UmdRssi;

import android.R.array;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.db.UhfService;
import com.example.uhfxintong.eventbus.DefectListener;
import com.example.uhfxintong.eventbus.NoteListener;

import de.greenrobot.event.EventBus;

@SuppressLint("SimpleDateFormat")
public class InventoryActivity extends Activity {

	SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	private String operator = null;
	private String time = null;

	private SpeechSynthesizer mTts;
	ApkInstaller mInstaller;
	private String mEngineType = SpeechConstant.TYPE_LOCAL;
	private String voicer = "xiaoyan";
	private SharedPreferences mSharedPreferences;
	@SuppressWarnings("unused")
	private int mPercentForBuffering = 0;
	@SuppressWarnings("unused")
	private int mPercentForPlaying = 0;
	
	private ImageView scan;
	//private EditText edit_UII;
	//private Button clear;
	private com.senter.support.openapi.StUhf.UII uii_change;
	private String uii;
	private final Accompaniment accompaniment = new Accompaniment(this,	R.raw.tag_inventoried);
	@SuppressWarnings("unused")
	private Handler mhandler;
	private Handler accompainimentsHandler;

	private Timer mTimer;
	private TimerTask mTask;

	private boolean isSpeaker = false;
	private ListView listView = null;
	private ArrayList<Uhf> uhfArrayList = new ArrayList<Uhf>();
	private MyListAdapter listAdapter = null;
	private TextView riqiTV = null;
	private TextView renyuanTV = null;
	private TextView zhengchangTV = null;
	private TextView quexianTV = null;
	private ImageView complateTV = null;
	private int zhengchangCount = 0;
	private int quexianCount = 0;
	private List<Uhf> readedUhfs = new ArrayList<Uhf>();
	//放入timertask内执行
	private final Runnable accompainimentRunnable = new Runnable() {
		@Override
		public void run() {
			accompaniment.start();
			accompainimentsHandler.removeCallbacks(this);
			uii = DataTransfer.xGetString(mUii.getBytes()).substring(0, 41);
			// edit_UII.setText(null);
			// edit_UII.append(uii);
			// edit_UII.append("\n");
			setParam();

			try {
				if (getUhfById(uii) != null) {
					Uhf uhfById = getUhfById(uii);
					uhfById.setTime(df.format(new Date()));
					uhfById.setOperator(operator);
					UhfService service = new UhfService(InventoryActivity.this);
					service.update(uhfById);
					getAllUhf();

					if(!uhfArrayList.contains(uhfById)){
						uhfArrayList.add(uhfById);
						zhengchangCount++;
						int qx = Integer.parseInt(quexianTV.getText().toString());
						zhengchangTV.setText(uhfArrayList.size()-qx+"");
						listAdapter.notifyDataSetChanged();
					}
					
				} /*else {
					stop();
					mTts.startSpeaking("数据库中无此设备信息，请录入", mTtsListener);
//					edit_UII.setText(null);
//					edit_UII.setText("数据库中无此设备信息，请录入");
					// 调取写入主程序
					Intent intent = new Intent(InventoryActivity.this, EntryActivity.class);
					intent.putExtra("UII", uii);
					// startActivity(intent);
					startActivityForResult(intent, 1);
					isSpeaker = true;
				}*/
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}// getUhfById

		}
	};

	// 启动
	private void start() {
		if (mTimer == null) {
			mTimer = new Timer();
		}
		//if (mTask == null) {
			mTask = new TimerTask() {
				@Override
				public void run() {
					//startInventory();
				}
			};
		//}
		mTimer.schedule(mTask, 1000, 1000);

		// mhandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// startInventory();
		// mhandler.postDelayed(this, 1000);
		// }
		//
		// });
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

		// mhandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// //startInventory();
		// mhandler.removeCallbacks(this, 1000);
		// }
		//
		// });
	}

	private boolean state = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		EventBus.getDefault().register(this);
		if(i.hasExtra("operator")&&i.getExtras().containsKey("operator")){
			operator = i.getExtras().getString("operator");
		}
		setContentView(R.layout.pandian_view);
		riqiTV = (TextView)this.findViewById(R.id.pandian_panel_riqi2);
		renyuanTV = (TextView)this.findViewById(R.id.pandian_panel_renyuan2);
		zhengchangTV = (TextView)this.findViewById(R.id.pandian_panel_zhengchang2);
		quexianTV = (TextView)this.findViewById(R.id.pandian_panel_quexian2);
		complateTV = (ImageView)this.findViewById(R.id.complateBtn);
		
		riqiTV.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
		renyuanTV.setText(operator);
		zhengchangTV.setText("0");
		quexianTV.setText("0");
		scan = (ImageView)this.findViewById(R.id.scan);
		listView = (ListView)this.findViewById(R.id.listView);
		listAdapter = new MyListAdapter();
		listView.setAdapter(listAdapter);
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.read_main_layout);
		if(getResources().getConfiguration().orientation==0){
			//竖屏
			layout.setBackgroundResource(R.drawable.pandian_bg);
		}else{
			layout.setBackgroundResource(R.drawable.pandian_bg);
		}
		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));

		mInstaller = new ApkInstaller(this);
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		mEngineType = SpeechConstant.TYPE_LOCAL;
		
		//edit_UII = (EditText) findViewById(R.id.uii_data);
/*		clear = (Button) findViewById(R.id.clear_uii);		*/
		if (!state) {
			scan.setImageResource(R.drawable.scan_start);

		} else {
			scan.setImageResource(R.drawable.scan_pause);
		}

		HandlerThread htHandlerThread = new HandlerThread("");
		htHandlerThread.start();

		accompainimentsHandler = new Handler(htHandlerThread.getLooper());
		accompaniment.init();
		
		
		complateTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 保存已经盘点的数据
				if(uhfArrayList.size()>0){
					ProgressDialog progressDialog;
					progressDialog = new ProgressDialog(InventoryActivity.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressDialog.setMessage("请稍后...");
					progressDialog.setCancelable(false);
					progressDialog.show();
					for(Uhf u : uhfArrayList){
						sqliteInsert(u);
					}
					progressDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
				InventoryActivity.this.finish();
			}
		});
		
		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!state) {
					scan.setImageResource(R.drawable.scan_pause);
					state = true;
					start();

				} else {
					if (mTts != null) {
						mTts.stopSpeaking();
						// 退出时释放连接
						// mTts.destroy();
					}
					state = false;
					scan.setImageResource(R.drawable.scan_start);
					stop();

				}
				// mhandler = new Handler();
				// start();
			}
		});
/*
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				while (edit_UII.length() > 0) {

					edit_UII.setText(null);

				}
			}

		});
*/
	}

	@SuppressWarnings("unused")
	private void initView() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
		stop();
	}

	protected Uhf getUhfById(String uii2) throws RowsExceededException,
			WriteException, BiffException, IOException {
		UhfService service = new UhfService(this);
		Uhf uhf = service.getUhfById(uii2);

		return uhf;
	}

	public void getAllUhf() throws RowsExceededException, WriteException,
			BiffException, IOException {
		UhfService service = new UhfService(this);
		List<Uhf> uhfs = service.getAllUhf();

		for (Uhf uhf : uhfs) {

			System.out.println(uhf.getUhfId());
			System.out.println(uhf.getUhfName());
		}

		service.createExcel(uhfs);

	}

	@SuppressWarnings("unused")
	private void stopInv() {
		App.stop();

	}

	private UII mUii;
	//开启RFID扫描器
	protected void startInventory() {
		App.getRfid()
				.getInterrogatorAs(InterrogatorModelD2.class)
				.iso18k6cRealTimeInventory(1,
						new UmdOnIso18k6cRealTimeInventory() {

							@Override
							public void onTagInventory(UII uii,	UmdFrequencyPoint frequencyPoint, Integer antennaId, UmdRssi rssi) {
								System.out.println("----begin onTagInventory------");
								mUii = uii;

							}

							@Override
							public void onFinishedSuccessfully(Integer arg0, int arg1, int arg2) {
								System.out.println("-----onFinishedSuccessfully-----");
								if (mUii != null) {
									addToplay(mUii);
									//stop();
								}

							}

							@Override
							public void onFinishedWithError(UmdErrorCode arg0) {

								System.out.println("------onFinishedWithError-------");

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


	private void setParam() {
		System.out.println("进入设置界面");

		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		} else {
			System.out.println("进入本地合同选择发音人界面");
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_LOCAL);
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
		}
		mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
		mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
		mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
		mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
	}

	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			// showTip("开始播放");
			System.out.println("开始语音播报*******************");
		}

		@Override
		public void onSpeakPaused() {
			// showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			// showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// mPercentForBuffering = percent;
			// showTip(String.format(getString(R.string.tts_toast_format),
			// mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// mPercentForPlaying = percent;
			// showTip(String.format(getString(R.string.tts_toast_format),
			// mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				// showTip("播放完成");
				if (state) {
					if (isSpeaker == true)
						return;
					start();
				}

			} else if (error != null) {
				// showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		start();
		isSpeaker = false;
	};

	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {

			if (code != ErrorCode.SUCCESS) {

			}
		}
	};

	@SuppressWarnings("static-access")
//	public void onConfigurationChanged(Configuration newConfig) {
//
//		super.onConfigurationChanged(newConfig);
//		LinearLayout layout = (LinearLayout)this.findViewById(R.id.read_main_layout);
//		//切换为竖屏
//		if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
//			layout.setBackgroundResource(R.drawable.frame_bg_v);
//
//		}
//		//切换为横屏
//		else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
//			layout.setBackgroundResource(R.drawable.frame_bg);
//		}
//	}
	class MyListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return uhfArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return uhfArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = getLayoutInflater().inflate(R.layout.pandian_listitem, null);
			TextView numberTV = (TextView)convertView.findViewById(R.id.number);
			TextView equipmentTV = (TextView)convertView.findViewById(R.id.pandian_listitem_shebeimingcheng2);
			TextView personTV = (TextView)convertView.findViewById(R.id.pandian_listitem_xunshirenyuan2);
			TextView dateTimeTV = (TextView)convertView.findViewById(R.id.pandian_listitem_xunshishijian2);
			final ImageView quexianImg = (ImageView)convertView.findViewById(R.id.quexian_imgview);
//			if(uhfArrayList.get(position).getDefect()!=null&&!uhfArrayList.get(position).getDefect().equals("")){
//				quexianImg.setImageResource(R.drawable.pandian_quexian_p);
//				quexianImg.setTag(R.drawable.pandian_quexian_p);
//			}else{
//				quexianImg.setImageResource(R.drawable.quexian_btn);
//				quexianImg.setTag(R.drawable.quexian_btn);
//			}
			Uhf uhf = uhfArrayList.get(position);
			if(!"".equals(uhf.getDefect()) || !"".equals(uhf.getPhotos())) {
				quexianImg.setImageResource(R.drawable.pandian_quexian_p);
				quexianImg.setTag(R.drawable.pandian_quexian_p);
			}else {
				quexianImg.setImageResource(R.drawable.quexian_btn);
				quexianImg.setTag(R.drawable.quexian_btn);
			}

			final ImageView beizhuImg = (ImageView)convertView.findViewById(R.id.beizhu_imgview);
			if(uhfArrayList.get(position).getNotes()!=null&&!uhfArrayList.get(position).getNotes().equals("")){
				beizhuImg.setImageResource(R.drawable.pandian_beizhu_p);
			}
			numberTV.setText(position+1+"");
			equipmentTV.setText(uhfArrayList.get(position).getUhfName());
			personTV.setText(uhfArrayList.get(position).getOperator());
			dateTimeTV.setText(uhfArrayList.get(position).getTime());
			//缺陷按钮
			quexianImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(InventoryActivity.this, QueXianInputActivity.class);
					final Uhf uhf = uhfArrayList.get(position);
					Log.i("a", "uhf = " + uhf.toString());
					intent.putExtra("uhf", uhf);
					intent.putExtra("position", position);
					startActivity(intent);
					// TODO Auto-generated method stub
//					final Dialog dialog = new Dialog(InventoryActivity.this, R.style.operatordialog);
//					dialog.setContentView(R.layout.pandian_dialog);
//					TextView submitBtn = (TextView)dialog.findViewById(R.id.queding);
//					TextView cancelBtn = (TextView)dialog.findViewById(R.id.quxiao);
//					TextView equipmentTV = (TextView)dialog.findViewById(R.id.pandian_listitem_shebeimingcheng2);
//					TextView personTV = (TextView)dialog.findViewById(R.id.pandian_listitem_xunshirenyuan2);
//					TextView dateTimeTV = (TextView)dialog.findViewById(R.id.pandian_listitem_xunshishijian2);
//					final EditText edt = (EditText)dialog.findViewById(R.id.edit_text);
//					edt.setHint("缺陷：请点击输入...");
//					edt.setText(uhfArrayList.get(position).getDefect());
//					equipmentTV.setText(uhfArrayList.get(position).getUhfName());
//					personTV.setText(uhfArrayList.get(position).getOperator());
//					dateTimeTV.setText(uhfArrayList.get(position).getTime());
//					submitBtn.setOnClickListener(new OnClickListener() {
//						
//						/* (non-Javadoc)
//						 * @see android.view.View.OnClickListener#onClick(android.view.View)
//						 */
//						@Override
//						public void onClick(View v) {
//							//添加、更新缺陷数据
//							uhfArrayList.get(position).setDefect(edt.getText().toString());
//							if(edt.getText().toString().trim().equals("")){
//								if(!quexianImg.getTag().equals(R.drawable.quexian_btn)){
//									quexianCount--;
//									zhengchangTV.setText((uhfArrayList.size()-quexianCount)+"");
//									quexianTV.setText(quexianCount+"");
//									quexianImg.setImageResource(R.drawable.quexian_btn);
//									quexianImg.setTag(R.drawable.quexian_btn);
//								}
//							}else{
//								if(!quexianImg.getTag().equals(R.drawable.pandian_quexian_p)){
//									
//									quexianCount++;
//									zhengchangTV.setText((uhfArrayList.size()-quexianCount)+"");
//									quexianTV.setText(quexianCount+"");
//									quexianImg.setImageResource(R.drawable.pandian_quexian_p);
//									quexianImg.setTag(R.drawable.pandian_quexian_p);
//								}
//							}
//							dialog.dismiss();
//						}
//					});
//					cancelBtn.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							dialog.dismiss();
//						}
//					});
//					dialog.show();
				}
			});
			//备注按钮
			beizhuImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(InventoryActivity.this, BeiZhuInputActivity.class);
					final Uhf uhf = uhfArrayList.get(position);
					Log.i("a", "uhf = " + uhf.toString());
					intent.putExtra("uhf", uhf);
					intent.putExtra("position", position);
					startActivity(intent);
					// TODO Auto-generated method stub
//					final Dialog dialog = new Dialog(InventoryActivity.this, R.style.operatordialog);
//					dialog.setContentView(R.layout.pandian_dialog);
//					TextView submitBtn = (TextView)dialog.findViewById(R.id.queding);
//					TextView cancelBtn = (TextView)dialog.findViewById(R.id.quxiao);
//					TextView equipmentTV = (TextView)dialog.findViewById(R.id.pandian_listitem_shebeimingcheng2);
//					TextView personTV = (TextView)dialog.findViewById(R.id.pandian_listitem_xunshirenyuan2);
//					TextView dateTimeTV = (TextView)dialog.findViewById(R.id.pandian_listitem_xunshishijian2);
//					final EditText edt = (EditText)dialog.findViewById(R.id.edit_text);
//					edt.setHint("备注：请点击输入...");
//					edt.setText(uhfArrayList.get(position).getNotes());
//					equipmentTV.setText(uhfArrayList.get(position).getUhfName());
//					personTV.setText(uhfArrayList.get(position).getOperator());
//					dateTimeTV.setText(uhfArrayList.get(position).getTime());
//					submitBtn.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							//添加、更新备注数据
//							uhfArrayList.get(position).setNotes(edt.getText().toString().trim());
//							if(edt.getText().toString().trim().equals("")){
//								beizhuImg.setImageResource(R.drawable.beizhu_btn);
//							}else{
//								beizhuImg.setImageResource(R.drawable.pandian_beizhu_p);
//							}
//							dialog.dismiss();
//						}
//					});
//					cancelBtn.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							dialog.dismiss();
//						}
//					});
//					dialog.show();
				}
			});
			if(!readedUhfs.contains(uhfArrayList.get(position))) {
				speaking(uhfArrayList.get(position));
			}
			return convertView;
		}
		
	}
	public void sqliteInsert(Uhf uhf) {

		UhfService service = new UhfService(this);
		long count;
		if ((count = service.saveUhfHistory(uhf)) > 0) {
			Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
		}
	}
	public void speaking(Uhf uhfById){
		String edit_UII = null;
		edit_UII = "";
		edit_UII += "设备名称  "+uhfById.getUhfName()+"  巡视人员   " + uhfById.getOperator() + "  巡视时间   " + uhfById.getTime();
		int code = mTts.startSpeaking(edit_UII, mTtsListener);
		readedUhfs.add(uhfById);
		stop();
		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页面
				mInstaller.install();
			} else {

			} 
		}
	}
	
	public void onEvent(DefectListener event) {
		Uhf uhf = uhfArrayList.get(event.getPosition());
		uhf.setDefect(event.getContent());
		uhf.setPhotos(event.getPhotoPaths());
		listAdapter.notifyDataSetChanged();
		quexianCount = 0;
		for(Uhf u : uhfArrayList) {
			if(u.getDefect() != null && !"".equals(u.getDefect())) {
				quexianCount++;
			}
		}
		quexianTV.setText(quexianCount+"");
		int qx = Integer.parseInt(quexianTV.getText().toString());
		zhengchangTV.setText(uhfArrayList.size()-qx+"");
	}
	
	public void onEvent(NoteListener event) {
		Uhf uhf = uhfArrayList.get(event.getPosition());
		uhf.setNotes(event.getContent());
		listAdapter.notifyDataSetChanged();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(state){
			scan.setImageResource(R.drawable.scan_pause);
		}else{
			scan.setImageResource(R.drawable.scan_start);
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stop();
	}
}
