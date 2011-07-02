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

	public ReversiView(Context context) {
		super(context);

		setId(VIEW_ID);
		setFocusable(true);
		
		mPaintBoardBg.setColor(getResources().getColor(R.color.board_bg));
		mPaintBoardBorder.setColor(getResources().getColor(R.color.board_border));
		mPaintCellFgB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellFgW.setColor(getResources().getColor(R.color.cell_fg_white));

		//アンチエイリアスを指定。これをしないと円がギザギザになる。
		mPaintCellFgB.setAntiAlias(true);
		mPaintCellFgW.setAntiAlias(true);
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
					mBoard.changeTurn();
				} catch (Exception e) {
					//Toast.makeText(this.getContext(), e.getMessage(), 300).show();
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
	
	
	private void drawBoard(Canvas canvas){

		if (mBoard.getRectF().width() <= 0f ) return;
		
		float bw = mBoard.getRectF().width();
		float bh = mBoard.getRectF().height();
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeidht();

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
					canvas.drawCircle(cell.getCx(), cell.getCy(), (float) (cw * 0.46), mPaintCellFgB);
				} else if(st == E_STATUS.White){
					canvas.drawCircle(cell.getCx(), cell.getCy(), (float) (cw * 0.46), mPaintCellFgW);
				}
			}
		}

	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable p = super.onSaveInstanceState();
		
		Bundle b = new Bundle();
		b.putString(STATE_CELLS, mBoard.getStateString());		//Boardの状態を保存。
		b.putParcelable(STATE_VIEW, p);
		return b;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle b = (Bundle)state;
		mBoard.loadFromStateString(b.getString(STATE_CELLS));	//Boardの状態を復元。
		super.onRestoreInstanceState(b.getParcelable(STATE_VIEW));
	}
	
	
}
