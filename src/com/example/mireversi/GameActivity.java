package com.example.mireversi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GameActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new ReversiView(this));

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//
//		return super.onPrepareOptionsMenu(menu);
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.mnuExit:
			finish();
			break;
//		case R.id.mnuPref:
//			openPref();
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
