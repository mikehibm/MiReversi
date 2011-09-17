package com.example.mireversi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivity extends Activity{

	ReversiView mReversiView = null;
	Animation mAnimWinner = null;
	Animation mAnimFadeOut = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.d("GameActivity.onCreate");
		
		mAnimWinner = AnimationUtils.loadAnimation(this, R.anim.winner);
		mAnimFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

		setContentView(R.layout.main);
		mReversiView = new ReversiView(this);
		ArrayList<View> arr = new ArrayList<View>();
		arr.add(mReversiView);
		
		FrameLayout frame;
		frame = (FrameLayout)this.findViewById(R.id.frame);
		frame.addView(mReversiView, 0);			//一番奥にReversiViewを追加。
		
		TextView txt = (TextView)findViewById(R.id.txtWinner);
		txt.bringToFront();
	}
	
	@Override
	protected void onPause() {
		Utils.d("GameActivity.onPause");

		Pref.setState(this.getApplicationContext(), mReversiView.getState());
		
		//別スレッドで思考ルーチンが動いていれば中断する。
		mReversiView.pause();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		Utils.d("GameActivity.onResume");
		
		mReversiView.resume(Pref.getState(this.getApplicationContext()));

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
//		case R.id.mnuStat:
//			mReversiView.showCountsToast();
//			break;
		case R.id.mnuInit: 
			mReversiView.init(true);
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

	public void showWinner(String msg){
		TextView txt = (TextView)findViewById(R.id.txtWinner);
		txt.setText(msg);
		if (txt.getVisibility() == View.INVISIBLE){
			txt.setVisibility(View.VISIBLE);
			txt.startAnimation(mAnimWinner);
		}
		
		View vwBack = (View)findViewById(R.id.vwBack);
		if (vwBack.getVisibility() == View.INVISIBLE){
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.grayin);
			vwBack.startAnimation(anim);
			vwBack.setVisibility(View.VISIBLE);
		}
	}

	public void hideWinner(String msg){
		TextView txt = (TextView)findViewById(R.id.txtWinner);
		if (txt.getVisibility() == View.VISIBLE){
			txt.startAnimation(mAnimFadeOut);
			txt.setVisibility(View.INVISIBLE);
		}

		View vwBack = (View)findViewById(R.id.vwBack);
		if (vwBack.getVisibility() == View.VISIBLE){
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.grayout);
			vwBack.startAnimation(anim);
			vwBack.setVisibility(View.INVISIBLE);
		}
	}
}
