package com.example.mireversi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GameActivity extends Activity{

	ReversiView reversiview = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reversiview = new ReversiView(this);
		setContentView(reversiview);

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
			reversiview.init();
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
