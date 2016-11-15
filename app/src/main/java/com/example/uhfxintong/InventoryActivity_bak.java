package com.example.uhfxintong;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.db.UhfService;

public class InventoryActivity_bak extends Activity {

	SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	private String operator = "sina";
	private String time = df.format(new Date());
	// /////////////////////////////////
	private SpeechSynthesizer mTts;
	ApkInstaller mInstaller;
	private String mEngineType = SpeechConstant.TYPE_LOCAL;
	private String voicer = "xiaoyan";
	private SharedPreferences mSharedPreferences;
	private int mPercentForBuffering = 0;
	private int mPercentForPlaying = 0;
	// ////////////////////////////////////

	private Button scan;
	private EditText edit_UII;
	private Button clear;
	com.senter.support.openapi.StUhf.UII uii_change;
	private String uii;
	private final Accompaniment accompaniment = new Accompaniment(this,
			R.raw.tag_inventoried);
	private Handler mhandler;
	private Handler accompainimentsHandler;

	private Timer mTimer;
	private TimerTask mTask;

	private boolean isSpeaker = false;

	private final Runnable accompainimentRunnable = new Runnable() {
		@Override
		public void run() {
			accompaniment.start();
			accompainimentsHandler.removeCallbacks(this);
			uii = DataTransfer.xGetString(mUii.getBytes()).substring(0, 41);
			edit_UII.setText(null);
			// edit_UII.append(uii);
			// edit_UII.append("\n");
			// /////////////////////////////////

			// ///////////////////////////////
			setParam();

			try {
				if (getUhfById(uii) != null) {

					Uhf uhfById = getUhfById(uii);
					uhfById.setTime(time);
					uhfById.setOperator(operator);
					UhfService service = new UhfService(InventoryActivity_bak.this);
					service.update(uhfById);
					getAllUhf();
					edit_UII.setText(null);
					// edit_UII.setText(uhfById.getUhfName());
					edit_UII.append("设备名称  " + uhfById.getUhfName() + " "
							+ uhfById.getFactory() + " "
							+ uhfById.getLineSpace() + " "
							+ uhfById.getVoltGrade());
					edit_UII.append("\n");

					edit_UII.append("巡视人员   " + operator);
					edit_UII.append("\n");
					edit_UII.append("巡视时间   " + time);
					edit_UII.append("\n");
					// service.createExcel(uhfById);
					int code = mTts.startSpeaking(edit_UII.getText().toString(), mTtsListener);
					edit_UII.append("\n");
					if (code != ErrorCode.SUCCESS) {
						if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
							// 未安装则跳转到提示安装页面
							mInstaller.install();
						} else {

						}
					}
				} else {
					stop();
					mTts.startSpeaking("数据库中无此设备信息，请录入", mTtsListener);
					edit_UII.setText(null);
					edit_UII.setText("数据库中无此设备信息，请录入");
					// 调取写入主程序
					Intent intent = new Intent(InventoryActivity_bak.this,
							EntryActivity.class);
					intent.putExtra("UII", uii);
					// startActivity(intent);
					startActivityForResult(intent, 1);
					isSpeaker = true;
				}
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}// getUhfById
				// // ///
				// //////////////////////////////////////////
		}
	};

	// 启动
	private void start() {
		if (mTimer == null) {
			mTimer = new Timer();
		}
		if (mTask == null) {
			mTask = new TimerTask() {

				@Override
				public void run() {
					startInventory();
				}
			};
		}
		mTimer.schedule(mTask, 200, 1000);
		;

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
		setContentView(R.layout.read_uii);
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.read_main_layout);
		if(getResources().getConfiguration().orientation==0){
			//竖屏
			layout.setBackgroundResource(R.drawable.scan_bg_v);
		}else{
			layout.setBackgroundResource(R.drawable.scan_bg_h);
		}
		Intent i = getIntent();
		if(i.hasExtra("operator")&&i.getExtras().containsKey("operator")){
			operator = i.getExtras().getString("operator");
		}
		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
		// ////////////////////////////////////////////////////////

		mInstaller = new ApkInstaller(this);
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		mEngineType = SpeechConstant.TYPE_LOCAL;

		// //////////////////////////////////////////////////////////
		scan = (Button) findViewById(R.id.scan);

		edit_UII = (EditText) findViewById(R.id.uii_data);
		clear = (Button) findViewById(R.id.clear_uii);
		if (!state) {
			scan.setText("打开扫描服务程序");

		} else {
			scan.setText("关闭扫描服务程序");
		}
		HandlerThread htHandlerThread = new HandlerThread("");
		htHandlerThread.start();

		accompainimentsHandler = new Handler(htHandlerThread.getLooper());
		accompaniment.init();

		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!state) {
					scan.setText("关闭扫描服务程序");
					state = true;
					start();

				} else {
					if (mTts != null) {
						mTts.stopSpeaking();
						// 退出时释放连接
						// mTts.destroy();
					}
					state = false;
					scan.setText("打开扫描服务程序");
					stop();

				}
				// mhandler = new Handler();
				// start();
			}
		});

		// ////////////////////////////////////////////////

		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				while (edit_UII.length() > 0) {

					edit_UII.setText(null);

				}
			}

		});

	}

	private void initView() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stop();
	}

	protected Uhf getUhfById(String uii2) throws RowsExceededException,	WriteException, BiffException, IOException {
		UhfService service = new UhfService(this);
		Uhf uhf = service.getUhfById(uii2);

		return uhf;
	}

	public void getAllUhf() throws RowsExceededException, WriteException,
			BiffException, IOException {
		UhfService service = new UhfService(this);
		List<Uhf> uhfs = service.getAllUhf();

		for (Uhf uhf : uhfs) {

			System.out.println("gggggggggggggggggggggggggg");
			System.out.println(uhf.getUhfId());
			System.out.println(uhf.getUhfName());

			System.out.println("endendendendendendend");
		}

		service.createExcel(uhfs);

	}

	private void stopInv() {
		App.stop();

	}

	private UII mUii;

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
								mUii = uii;

							}

							@Override
							public void onFinishedSuccessfully(Integer arg0,
									int arg1, int arg2) {
								System.out.println("-----onFinishedSuccessfully-----");
								if (mUii != null) {
									addToplay(mUii);
									stop();
								}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode
				|| event.getAction() == KeyEvent.ACTION_DOWN) {
			mTts.stopSpeaking();
			// 退出时释放连接
			mTts.destroy();

			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// //////////////////////////////////////////////////////
	private void setParam() {
		System.out.println("进入设置界面");

		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);

			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		} else {
			System.out.println("进入本地合同选择发音人界面");
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_LOCAL);

			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
		}

		mTts.setParameter(SpeechConstant.SPEED,
				mSharedPreferences.getString("speed_preference", "50"));

		mTts.setParameter(SpeechConstant.PITCH,
				mSharedPreferences.getString("pitch_preference", "50"));

		mTts.setParameter(SpeechConstant.VOLUME,
				mSharedPreferences.getString("volume_preference", "50"));

		mTts.setParameter(SpeechConstant.STREAM_TYPE,
				mSharedPreferences.getString("stream_preference", "3"));
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
	// ///////////////////////////////////////////////////////
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.read_main_layout);
		//切换为竖屏
		if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
			layout.setBackgroundResource(R.drawable.scan_bg_v);

		} 
		//切换为横屏
		else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
			layout.setBackgroundResource(R.drawable.scan_bg_h);
		}
	}
}
