package com.example.uhfxintong;

import com.senter.support.openapi.StUhf;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面
 * 
 * @author
 * @version 创建时间：2013-7-11 上午8:37:18
 */
public class MainActivity extends Activity {
	public static final String Tag = "MainActivity";
	private String operator = "";
	private TextView userTv = null;
	private TextView userCodeTv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (App.getRfid() == null) {
//			Toast.makeText(this, R.string.MakeSurePDAexitRfid,
//					Toast.LENGTH_LONG).show();
//			finish();
			new ViewsD2();
		} else {
			switch (App.getRfid().getInterrogatorModel()) {

			case InterrogatorModelD2: {
				new ViewsD2();
				break;
			}

			default:
				break;
			}
			onCreateInitViews();
		}
	}

	private class Views {
	}

	/*
	 * private class ViewsAB extends Views { public ViewsAB() {
	 * setContentView(R.layout.activitymain);
	 * 
	 * // GridLayout gl = (GridLayout)
	 * findViewById(R.id.idMain2Activity_glApps);
	 * 
	 * LinearLayout ll = (LinearLayout)
	 * findViewById(R.id.idMain2Activity_glApps_inInventory);
	 * ll.setOnClickListener(new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C0Inventory(); } });
	 * 
	 * 
	 * ll = (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inRead);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C1Read(); } });
	 * 
	 * ll = (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inWrite);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C2Write(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inErease);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C3Erase(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inLock);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C4Lock(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inUnlock);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C5UnLock(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inKill);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C6Kill(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inSettings);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { onSettings(); } }); } }
	 * 
	 * private class ViewsC extends Views { public ViewsC() {
	 * setContentView(R.layout.activitymain);
	 * 
	 * // GridLayout gl = (GridLayout)
	 * findViewById(R.id.idMain2Activity_glApps);
	 * 
	 * LinearLayout ll = (LinearLayout)
	 * findViewById(R.id.idMain2Activity_glApps_inInventory);
	 * ll.setOnClickListener(new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C0Inventory(); } });
	 * 
	 * 
	 * ll = (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inRead);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C1Read(); } });
	 * 
	 * ll = (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inWrite);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C2Write(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inErease);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C3Erase(); } });
	 * ll.setVisibility(View.GONE);
	 * 
	 * ll = (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inLock);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C4Lock(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inUnlock);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C5UnLock(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inKill);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { on6C6Kill(); } }); ll =
	 * (LinearLayout) findViewById(R.id.idMain2Activity_glApps_inSettings);
	 * ll.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { onSettings(); } }); } }
	 */
	private class ViewsD2 {

		public ViewsD2() {// idMain2ActivityD_glApps_6b_inInventory

			setContentView(R.layout.main_three);
			// LinearLayout ll = (LinearLayout)
			// findViewById(R.id.idMain2ActivityD_glApps_inInventory);
			userTv = (TextView) findViewById(R.id.operator);
			userCodeTv = (TextView) findViewById(R.id.operator_code);
			RelativeLayout layout = (RelativeLayout) MainActivity.this
					.findViewById(R.id.main_layout);
			if (getResources().getConfiguration().orientation == 0) {
				// 竖屏
				layout.setBackgroundResource(R.drawable.frame_bg_v);
			} else {
				layout.setBackgroundResource(R.drawable.frame_bg);
			}
			ImageView write = (ImageView) findViewById(R.id.write);
			write.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// on6C2Write();
					if (operator.equals("")) {
						Intent intent = new Intent(MainActivity.this,
								ErWeiScanCaptureActivity.class);
						startActivityForResult(intent, 99);
					} else {
						myStartActivity(EntryActivity.class);
					}
				}
			});

			ImageView read = (ImageView) findViewById(R.id.read);
			read.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// on6C1Read();
					if (operator.equals("")) {
						// Toast.makeText(getApplicationContext(), "未登录",
						// Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(MainActivity.this,
								ErWeiScanCaptureActivity.class);
						startActivityForResult(intent, 100);
					} else {
						Intent intent = new Intent(getApplicationContext(),
								InventoryActivity.class);
						intent.putExtra("operator", operator);
						startActivity(intent);
					}

				}
			});

//			ImageView person = (ImageView) findViewById(R.id.person);
			// person.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			// final Dialog dialog = new Dialog(MainActivity.this
			// ,R.style.operatordialog);
			//
			// dialog.setContentView(R.layout.operator_set_dialog);
			// TextView submitBtn = (TextView)
			// dialog.findViewById(R.id.queding);
			// TextView cancelBtn = (TextView)dialog.findViewById(R.id.quxiao);
			// final EditText nameEdt =
			// (EditText)dialog.findViewById(R.id.name_edt);
			// submitBtn.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// operator = nameEdt.getText().toString().trim();
			// if(operator.equals(""))
			// return;
			// Toast.makeText(MainActivity.this, "设置成功！当前管理员："+operator,
			// Toast.LENGTH_SHORT).show();
			// dialog.dismiss();
			// }
			// });
			// cancelBtn.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// dialog.dismiss();
			// }
			// });
			// dialog.show();
			// }
			// });
//			person.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//						Intent intent = new Intent(MainActivity.this,
//								ErWeiScanCaptureActivity.class);
//						startActivityForResult(intent, 100);
//				}
//			});
			// 历史记录
			ImageView history = (ImageView) MainActivity.this
					.findViewById(R.id.stocktaking);
			history.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (operator.equals("")) {
						Intent intent = new Intent(MainActivity.this,
								ErWeiScanCaptureActivity.class);
						startActivityForResult(intent, 98);
					} else {
						myStartActivity(HistoryActivityNew.class);
					};
				}
			});

			/*
			 * //6b
			 * 
			 * ll = (LinearLayout)
			 * findViewById(R.id.idMain2ActivityD6b_glApps_inInventory);
			 * ll.setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { on6B0Inventory(); } });
			 * 
			 * ll = (LinearLayout)
			 * findViewById(R.id.idMain2ActivityD6b_glApps_inRead);
			 * ll.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { on6B1Read(); } });
			 * 
			 * ll = (LinearLayout)
			 * findViewById(R.id.idMain2ActivityD6b_glApps_inWrite);
			 * ll.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { on6B2Write(); } });
			 * 
			 * ll = (LinearLayout)
			 * findViewById(R.id.idMain2ActivityD6b_glApps_inLock);
			 * ll.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { on6B3Lock(); } }); ll =
			 * (LinearLayout)
			 * findViewById(R.id.idMain2ActivityD6b_glApps_inUnlock);
			 * ll.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { on6B4QuearyLock(); } });
			 */
		}
	}

	private void onCreateInitViews() {
	}

	protected void on6C0Inventory() {
		switch (App.rfid().getInterrogatorModel()) {

		case InterrogatorModelD1:
		case InterrogatorModelD2:
			// myStartActivity(EntryActivity.class);
			break;

		default:
			break;
		}
	}

	protected void on6C1Read() {
		switch (App.rfid().getInterrogatorModel()) {

		case InterrogatorModelD1:
		case InterrogatorModelD2:
			myStartActivity(InventoryActivity.class);
			break;

		default:
			break;
		}
	}

	protected void on6C2Write() {
		switch (App.rfid().getInterrogatorModel()) {

		case InterrogatorModelD1:
		case InterrogatorModelD2:
			myStartActivity(EntryActivity.class);
			break;

		default:
			break;
		}
	}

	protected void on6C3Erase() {
		switch (App.rfid().getInterrogatorModel()) {

		default:
			break;
		}
	}

	protected void on6C4Lock() {
		switch (App.rfid().getInterrogatorModel()) {

		case InterrogatorModelD1:
		case InterrogatorModelD2:

			break;

		default:
			break;
		}
	}

	protected void on6C5UnLock() {
		switch (App.rfid().getInterrogatorModel()) {

		default:
			break;
		}
	}

	protected void on6C6Kill() {
		switch (App.rfid().getInterrogatorModel()) {

		case InterrogatorModelD1:
		case InterrogatorModelD2:

			break;

		default:
			break;
		}
	}

	protected void onSettings() {
		switch (App.rfid().getInterrogatorModel()) {

		case InterrogatorModelD1:
		case InterrogatorModelD2:

			break;

		default:
			break;
		}
	}

	/*
	 * protected void on6B0Inventory() { switch
	 * (App.rfid().getInterrogatorModel()) { case InterrogatorModelD2:
	 * myStartActivity
	 * (com.senter.demo.uhf.modelD2.iso18k6b.Activity0Inventory.class); break;
	 * default: break; } }
	 * 
	 * protected void on6B1Read() {
	 * 
	 * switch (App.rfid().getInterrogatorModel()) { case InterrogatorModelD2:
	 * myStartActivity
	 * (com.senter.demo.uhf.modelD2.iso18k6b.Activity1Read.class); break;
	 * default: break; } }
	 * 
	 * 
	 * protected void on6B2Write() {
	 * 
	 * switch (App.rfid().getInterrogatorModel()) { case InterrogatorModelD2:
	 * myStartActivity
	 * (com.senter.demo.uhf.modelD2.iso18k6b.Activity2Write.class); break;
	 * default: break; } }
	 * 
	 * 
	 * protected void on6B3Lock() {
	 * 
	 * switch (App.rfid().getInterrogatorModel()) { case InterrogatorModelD2:
	 * myStartActivity
	 * (com.senter.demo.uhf.modelD2.iso18k6b.Activity3Lock.class); break;
	 * default: break; } }
	 * 
	 * protected void on6B4QuearyLock() {
	 * 
	 * switch (App.rfid().getInterrogatorModel()) { case InterrogatorModelD2:
	 * myStartActivity
	 * (com.senter.demo.uhf.modelD2.iso18k6b.Activity4QueryLock.class); break;
	 * default: break; } }
	 */

	private void myStartActivity(Class<?> clazz) {
		startActivity(new Intent(this, clazz));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			StUhf rfid = App.rfid();
			if (rfid != null) {
				rfid.uninit();
			}
			finish();

			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		RelativeLayout layout = (RelativeLayout) this
				.findViewById(R.id.main_layout);
		// 切换为竖屏
		if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
			layout.setBackgroundResource(R.drawable.frame_bg_v);

		}
		// 切换为横屏
		else if (newConfig.orientation == this.getResources()
				.getConfiguration().ORIENTATION_LANDSCAPE) {
			layout.setBackgroundResource(R.drawable.frame_bg);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null) {
			switch (requestCode) {
			case 100:
				Bundle bundle = data.getExtras();
				String code = bundle.getString("result");
				operator = "Eric Echeverri";
				// userTv.setText(operator);
				// userCodeTv.setText(code);
				myStartActivity(EntryActivity.class);
//				Intent intent = new Intent(getApplicationContext(),
//						InventoryActivity.class);
//				intent.putExtra("operator", operator);
//				startActivity(intent);
				// Toast.makeText(MainActivity.this, "扫描内容 = " + code, 0).show();
				break;
			case 99:
				bundle = data.getExtras();
				code = bundle.getString("result");
				operator = "Eric Echeverri";
				myStartActivity(EntryActivity.class);
				break;
			case 98:
				bundle = data.getExtras();
				code = bundle.getString("result");
				operator = "Eric Echeverri";
				myStartActivity(EntryActivity.class);
//				myStartActivity(HistoryActivityNew.class);
				break;
			}
		}
	}
}
