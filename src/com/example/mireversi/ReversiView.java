package com.example.mireversi;

import java.util.List;

import com.example.mireversi.model.Board;
import com.example.mireversi.model.Cell;
import com.example.mireversi.model.Cell.E_STATUS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ReversiView extends View {

	private static final String TAG = "ReversiView";
	private static final String STATE_VIEW = "View";
	private static final String STATE_CELLS = "Cells";
	private static final int VIEW_ID = 1000;

	private Board mBoard = new Board();
	
	private Paint mPaintBoardBg = new Paint();
	private Paint mPaintBoardBorder = new Paint();
	private Paint mPaintCellFgB = new Paint();
	private Paint mPaintCellFgW = new Paint();
	private Paint mPaintCellAvB = new Paint();
	private Paint mPaintCellAvW = new Paint();

	public ReversiView(Context context) {
		super(context);

		setId(VIEW_ID);
		setFocusable(true);
		
		mPaintBoardBg.setColor(getResources().getColor(R.color.board_bg));
		mPaintBoardBorder.setColor(getResources().getColor(R.color.board_border));
		mPaintCellFgB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellFgW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintCellAvB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellAvW.setColor(getResources().getColor(R.color.cell_fg_white));

		//アンチエイリアスを指定。これをしないと円がギザギザになる。
		mPaintCellFgB.setAntiAlias(true);
		mPaintCellFgW.setAntiAlias(true);
		mPaintCellAvB.setAntiAlias(true);
		mPaintCellAvW.setAntiAlias(true);

		mPaintCellAvB.setAlpha(16);
		mPaintCellAvW.setAlpha(32);
}
	
	public void init(){
		mBoard = new Board();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mBoard.setSize(getWidth(), getHeight());
		
		drawBoard(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			int r = (int)(y / mBoard.getCellHeidht());
			int c = (int)(x / mBoard.getCellWidth());
			if (r < Board.ROWS && c < Board.COLS){
				List<Cell> changedCells = null;
				
				try {
					changedCells = mBoard.changeCell(r, c, mBoard.getTurn());
					
					int nextAvailableCellCount = mBoard.changeTurn(changedCells); 
					if (nextAvailableCellCount == 0){
						if (mBoard.isFinished()){
							showCountsToast();
						} else {
							showSkippMessage();
							mBoard.changeTurn(changedCells);
						}
					}
				} catch (Exception e) {
					//Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					Log.d(TAG, e.getMessage());
				}

				if (changedCells != null){
					for (Cell cell : changedCells) {
						invalidate(cell.getRect());			//変更された領域のみを再描画
					}
				}
			}
			break;
		default:
			return true;
		}
		
		return true;
	}

	public void showCountsToast(){
		Cell.E_STATUS winner = mBoard.getWinner();
		String msg = "Black: " + mBoard.countCells(Cell.E_STATUS.Black) + "\n"
			+ "White: " + mBoard.countCells(Cell.E_STATUS.White) + "\n\n";
		
		if (mBoard.isFinished()){
			msg += "Winner is: " + Cell.statusToDisplay(winner) + "!!";
		} else {
			if (winner != Cell.E_STATUS.None){
				msg += Cell.statusToDisplay(winner) + " is winning...\n\n";
			}
			msg += mBoard.getTurnDisplay() + "'s turn.";
		}
		Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
		Log.d(TAG, msg);
	}
	
	private void showSkippMessage(){
		String msg = Cell.statusToDisplay(mBoard.getTurn()) + " has been skipped.";
		Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
		Log.d(TAG, msg);
	}
	
	private void drawBoard(Canvas canvas){

		if (mBoard.getRectF().width() <= 0f ) return;
		
		float bw = mBoard.getRectF().width();
		float bh = mBoard.getRectF().height();
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeidht();
		float w = cw * 0.42f;

		//ボードの背景
		canvas.drawRect(mBoard.getRectF(), mPaintBoardBg);

		//縦線
		for (int i = 0; i < Board.COLS; i++) {
			canvas.drawLine(cw * (i+1), 0, cw * (i+1), bh, mPaintBoardBorder);
		}
		//横線
		for (int i = 0; i < Board.ROWS; i++) {
			canvas.drawLine(0, ch * (i+1), bw, ch * (i+1), mPaintBoardBorder);
		}

		//全てのCellについてコマが配置されていれば描く
		Cell[][] cells = mBoard.getCells();
		for (int i = 0; i < Board.ROWS; i++) {
			for (int j = 0; j < Board.COLS; j++) {
				Cell cell =cells[i][j]; 
				Cell.E_STATUS st = cell.getStatus();
				
				if (st == E_STATUS.Black){
					canvas.drawCircle(cell.getCx(), cell.getCy(), w, mPaintCellFgB);
				} else if(st == E_STATUS.White){
					canvas.drawCircle(cell.getCx(), cell.getCy(), w, mPaintCellFgW);
				} else {
					showHints(cell, canvas, cw);
				}
			}
		}
	}
	
	private void showHints(Cell cell, Canvas canvas, float cw){
		if (!Pref.getShowHints(getContext())){
			return;
		}

		float aw = cw * 0.1f;

		if (cell.getReversibleCells().size() > 0){
			if (mBoard.getTurn() == Cell.E_STATUS.Black){
				canvas.drawCircle(cell.getCx(), cell.getCy(), aw, mPaintCellAvB);
			} else {
				canvas.drawCircle(cell.getCx(), cell.getCy(), aw, mPaintCellAvW);
			}
		} else {
			canvas.drawCircle(cell.getCx(), cell.getCy(), aw, mPaintBoardBg);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable p = super.onSaveInstanceState();
		
		Bundle b = new Bundle();
		String boardState = mBoard.getStateString();
		b.putString(STATE_CELLS, boardState);						//Boardの状態を保存。
Log.d(TAG, "onSaveInstanceState: boardState=" + boardState);
		b.putParcelable(STATE_VIEW, p);
		return b;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle b = (Bundle)state;
		String boardState = b.getString(STATE_CELLS);
Log.d(TAG, "onRestoreInstanceState: boardState=" + boardState);
		mBoard.loadFromStateString(boardState);					//Boardの状態を復元。
		
		super.onRestoreInstanceState(b.getParcelable(STATE_VIEW));
	}
	
}
