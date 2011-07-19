package com.example.mireversi.model;

import android.graphics.Point;
import android.os.Handler;
import com.example.mireversi.model.Cell.E_STATUS;

public abstract class ComputerPlayer extends Player implements Runnable {
	
	private Handler mHandler = new Handler();
	private IPlayerCallback mCallback;
	private Thread mThread;
	private boolean mStopped; 
	
	public ComputerPlayer(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void startThinking(IPlayerCallback callback) {
		mCallback = callback;
		setStopped(false);
		
		if (mBoard.getAvailableCellCount(true) == 0){
			callback.onEndThinking(new Point(-1, -1));
			return;
		}
		
		//別スレッドでタイトルの取得処理を開始。
		mThread = new Thread(this);
		mThread.start();
	}
	
	@Override
	public void stopThinking() {
		if (mThread != null && mThread.isAlive()){
			mThread.interrupt();
		}
		
		setStopped(true);
	}

	@Override
	public void run() {
		//思考ルーチンを実行。
		final Point pos = think();
		
		//処理完了後、ハンドラにUIスレッド側で実行する処理を渡す。
		mHandler.post(new Runnable(){
			@Override
			public void run(){
				mCallback.onEndThinking(pos);
			}
		});
	}
	
	protected abstract Point think();

	public void setStopped(boolean mStopped) {
		this.mStopped = mStopped;
	}

	public boolean isStopped() {
		return mStopped;
	}
	
}
