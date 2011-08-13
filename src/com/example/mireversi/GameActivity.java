package com.example.mireversi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivity extends Activity{

	ReversiView reversiview = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.d("GameActivity.onCreate");
		
//		reversiview = new ReversiView(this);
//		setContentView(reversiview);
		
		setContentView(R.layout.main);
		reversiview = new ReversiView(this);
		ArrayList<View> arr = new ArrayList<View>();
		arr.add(reversiview);
		
		FrameLayout frame;
		frame = (FrameLayout)this.findViewById(R.id.frame);
		frame.addView(reversiview, 0);			//一番奥にReversiViewを追加。
		
		TextView txt = new TextView(this);
		txt.setText("TEST");
		txt.setTextSize(48);
		//txt.setTextColor(Color.YELLOW);
		txt.setTextColor(Color.argb(200, 200, 200, 255));
		txt.setShadowLayer(5, 3, 3, Color.BLACK);
		frame.addView(txt);
	}
	
	@Override
	protected void onPause() {
		Utils.d("GameActivity.onPause");

		Pref.setState(this.getApplicationContext(), reversiview.getState());
		
		//別スレッドで思考ルーチンが動いていれば中断する。
		reversiview.pause();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		Utils.d("GameActivity.onResume");
		
		reversiview.resume(Pref.getState(this.getApplicationContext()));

		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.mnuExit:
			finish();
			break;
		case R.id.mnuPref:
			openPref();
			break;
		case R.id.mnuStat:
			reversiview.showCountsToast();
			break;
		case R.id.mnuInit:
			reversiview.init(true);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//設定画面を開く
	private void openPref() {
		Intent intent = new Intent(this, Pref.class); 
		startActivity(intent);
	}

}
