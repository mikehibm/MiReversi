package com.example.mireversi.model;

import java.util.Comparator;
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

	
	public int getWeight(Cell cell, int[][] weight_table){
		Point pt = cell.getPoint();
		return weight_table[pt.y][pt.x];
	}


	/***
	 * セルの位置の評価値で降順にソートする為のComparatorクラス。
	 * @author mike
	 *
	 */
	public class WeightComparator implements Comparator<Cell> {  
		
		private int[][] mWeightTable;
		
		public WeightComparator(int[][] weight_table){
			mWeightTable = weight_table;
		}
		
		@Override
		public int compare(Cell cell1, Cell cell2) {
			//0：等しい。1：より大きい。-1：より小さい
			int weight1 = getWeight(cell1, mWeightTable);
			int weight2 = getWeight(cell2, mWeightTable);
			if (weight1 > weight2) return -1;
			if (weight1 < weight2) return 1;
			return 0;
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
			if (val1 > val2) return -1;
			if (val1 < val2) return 1;
			if (val1 == val2){
				if (cell1.getNextAvaiableCnt() > cell2.getNextAvaiableCnt()) return -1;
				if (cell1.getNextAvaiableCnt() < cell2.getNextAvaiableCnt()) return 1;
			}
			return 0;
		}  
	}
	
	public int getWeightTotal(Board board, int [][] weight_table){
		int total = 0;
		Cell[][] cells = board.getCells();
		E_STATUS player_turn = board.getTurn();		
		E_STATUS opp_turn = Cell.getOpponentStatus(player_turn);
		
		int cur_count = 0, opp_count = 0, blank_count = 0;
		
		for (int i = 0; i< Board.ROWS; i++ ){
			for (int j =0; j < Board.COLS; j++){
				E_STATUS st = cells[i][j].getStatus();
				if (st == player_turn){
					cur_count++;
					total += getWeight(cells[i][j], weight_table);
				} else if (st == opp_turn){
					opp_count++;
					total -= getWeight(cells[i][j], weight_table);
				} else {
					blank_count++;
				}
			}
		}
		
//		if (opp_count == 0){
//			total = Integer.MAX_VALUE;
//		}
		
		return total;
	}

}
