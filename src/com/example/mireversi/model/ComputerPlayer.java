package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Comparator;

import android.graphics.Point;
import android.os.Handler;
import com.example.mireversi.model.Cell.E_STATUS;

public abstract class ComputerPlayer extends Player implements Runnable {
	
	//場所毎の評価値
	protected static final int[][] weight 
		= { { 30,-12,  0, -1, -1,  0,-12, 30 }, 
			{-12,-15, -3, -3, -3, -3,-15,-12 },
			{  0, -3,  0, -1, -1,  0, -3,  0 },
			{ -1, -3, -1, -1, -1, -1, -3, -1 },
			{ -1, -3, -1, -1, -1, -1, -3, -1 },
			{  0, -3,  0, -1, -1,  0, -3,  0 },
			{-12,-15, -3, -3, -3, -3,-15,-12 },
			{ 30,-12,  0, -1, -1,  0,-12, 30 }
	   	};

	private Handler mHandler = new Handler();
	private IPlayerCallback mCallback;
	private Thread mThread;
	private boolean mStopped; 
	
	public ComputerPlayer(E_STATUS turn, String name, Board board){
		super(turn, name, board);
		mStopped = false;
	}

	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public synchronized void startThinking(IPlayerCallback callback) {
		mCallback = callback;
		mStopped = false;
		
		if (mBoard.getAvailableCellCount(true) == 0){
			callback.onEndThinking(new Point(-1, -1));
			return;
		}
		
		//別スレッドでタイトルの取得処理を開始。
		mThread = new Thread(this);
		mThread.start();
	}
	
	@Override
	public synchronized void stopThinking() {
		mStopped = true;

//		if (mThread != null && mThread.isAlive()){
//			mThread.interrupt();
//		}
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

	
	public int getWeight(Cell cell){
		Point pt = cell.getPoint();
		return weight[pt.y][pt.x];
	}


	/***
	 * セルの位置の評価値で降順にソートする為のComparatorクラス。
	 * @author mike
	 *
	 */
	public class WeightComparator implements Comparator<Cell> {  
		@Override
		public int compare(Cell cell1, Cell cell2) {
			//0：等しい。1：より大きい。-1：より小さい
			int weight1 = getWeight(cell1);
			int weight2 = getWeight(cell2);
			int n = Integer.signum(weight2 - weight1);		//降順にソートする。
			return n;      
		}  
	}
	
	/***
	 * セルの位置の評価値で降順にソートする為のComparatorクラス。
	 * @author mike
	 *
	 */
	public class EvaluationComparator implements Comparator<Cell> {  
		@Override
		public int compare(Cell cell1, Cell cell2) {
			//0：等しい。1：より大きい。-1：より小さい
			int val1 = cell1.getEval();
			int val2 = cell2.getEval();
			int n = Integer.signum(val2 - val1);		//降順にソートする。
			return n;      
		}  
	}
	
	public int getWeightTotal(Board board){
		int total = 0;
		Cell[][] cells = board.getCells();
		E_STATUS current_turn = board.getTurn();
		E_STATUS opp_turn = Cell.getOpponentStatus(current_turn);
		
		for (int i = 0; i< Board.ROWS; i++ ){
			for (int j =0; j < Board.COLS; j++){
				if (cells[i][j].getStatus() == current_turn){
					total += getWeight(cells[i][j]);
				} else if (cells[i][j].getStatus() == opp_turn){
					total -= getWeight(cells[i][j]);
				}
			}
		}
		return total;
	}

}
