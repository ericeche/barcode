package com.example.uhfxintong;

import com.example.uhfxintong.db.Uhf;
import com.example.uhfxintong.eventbus.NoteListener;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class BeiZhuInputActivity extends Activity {
	private EditText bzContent;
	private Uhf uhf;
	private int position = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.acctivity_beizhu_inpput);
		uhf = (Uhf) this.getIntent().getSerializableExtra("uhf");
		position = this.getIntent().getIntExtra("position", 0);
		bzContent = (EditText) this.findViewById(R.id.quexianContent);
		bzContent.setText(uhf.getNotes());
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
		EventBus.getDefault().post(new NoteListener(bzContent.getText().toString(), position));
	}
	
}
