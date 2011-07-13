package com.example.mireversi;

import java.util.List;

import com.example.mireversi.model.Board;
import com.example.mireversi.model.Cell;
import com.example.mireversi.model.Cell.E_STATUS;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
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
	
	private Paint mPaintScreenBg = new Paint();
	private Paint mPaintBoardBg = new Paint();
	private Paint mPaintBoardBorder = new Paint();
	private Paint mPaintCellFgB = new Paint();
	private Paint mPaintCellFgW = new Paint();
	private Paint mPaintCellAvB = new Paint();
	private Paint mPaintCellAvW = new Paint();
	private Paint mPaintTextFg = new Paint();
	private Paint mPaintTurnRect = new Paint();
	
	private int mWidth;
	private int mHeight;
	private static final float CELL_SIZE_FACTOR = 0.42f;

	public ReversiView(Context context) {
		super(context);

		setId(VIEW_ID);
		setFocusable(true);
		
		mPaintScreenBg.setColor(getResources().getColor(R.color.screen_bg));
		mPaintBoardBg.setColor(getResources().getColor(R.color.board_bg));
		mPaintBoardBorder.setColor(getResources().getColor(R.color.board_border));
		mPaintCellFgB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellFgW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintCellAvB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellAvW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintTextFg.setColor(getResources().getColor(R.color.text_fg));
		mPaintTurnRect.setColor(getResources().getColor(R.color.turn_rect));

		//アンチエイリアスを指定。これをしないと縁がギザギザになる。
		mPaintCellFgB.setAntiAlias(true);
		mPaintCellFgW.setAntiAlias(true);
		mPaintCellAvB.setAntiAlias(true);
		mPaintCellAvW.setAntiAlias(true);

		mPaintCellAvB.setAlpha(32);
		mPaintCellAvW.setAlpha(64);
		
		mPaintTextFg.setAntiAlias(true);
		mPaintTextFg.setStyle(Style.FILL);
		
		//参考URL:
		// http://y-anz-m.blogspot.com/2010/02/android-multi-screen.html 
		// http://y-anz-m.blogspot.com/2010/05/androiddimension.html
		Resources res = getResources();  
		int fontSize = res.getDimensionPixelSize(R.dimen.font_size_status); 
		mPaintTextFg.setTextSize(fontSize);
		
		mPaintTurnRect.setAntiAlias(true);
		mPaintTurnRect.setAlpha(128);
		mPaintTurnRect.setStyle(Style.STROKE);
		mPaintTurnRect.setStrokeWidth(5f);
	}
	
	public void init(){
		mBoard = new Board();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		this.mWidth = getWidth();
		this.mHeight = getHeight();
		mBoard.setSize(this.mWidth, this.mHeight);
		
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
						if (mBoard.isFinished()){				//全部のセルが埋まった場合、
							showCountsToast();						//結果を表示。
						} else {
							showSkippMessage();
							nextAvailableCellCount = mBoard.changeTurn(changedCells);
							if (nextAvailableCellCount == 0){	//どちらも打つ場所が無くなった場合、
								showCountsToast();					//結果を表示
							}
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
		float w = cw * CELL_SIZE_FACTOR;
		boolean show_hints = Pref.getShowHints(getContext());

		//画面全体の背景
		canvas.drawRect(0 ,0, mWidth, mHeight, mPaintScreenBg);

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
					drawHints(cell, canvas, cw, show_hints);
				}
			}
		}
		
		drawStatus(canvas);
	}
	
	private void drawStatus(Canvas canvas){
		Resources res = getResources();  
		float turn_rect_inset = res.getDimension(R.dimen.turn_rect_inset); 
		float turn_rect_round = res.getDimension(R.dimen.turn_rect_round); 
		float turn_circle_x = res.getDimension(R.dimen.turn_circle_x); 
		float turn_circle_y = res.getDimension(R.dimen.turn_circle_y); 
		float turn_text_x = res.getDimension(R.dimen.turn_text_x); 
		float turn_text_y = res.getDimension(R.dimen.turn_text_y); 
		float top = mBoard.getRectF().bottom;
		float center = mBoard.getRectF().width() / 2f;
		
		RectF rect;
		if (mBoard.getTurn() == E_STATUS.Black){
			rect = new RectF(turn_rect_inset, top + turn_rect_inset, center - turn_rect_inset, mHeight - turn_rect_inset);
		} else {
			rect = new RectF(center + turn_rect_inset, top + turn_rect_inset, mWidth - turn_rect_inset, mHeight - turn_rect_inset);
		}
		mPaintTurnRect.setStyle(Style.FILL);
		mPaintTurnRect.setAlpha(64);
		canvas.drawRoundRect(rect, turn_rect_round, turn_rect_round, mPaintTurnRect);
		mPaintTurnRect.setStyle(Style.STROKE);
		mPaintTurnRect.setAlpha(255);
		canvas.drawRoundRect(rect, turn_rect_round, turn_rect_round, mPaintTurnRect);


		canvas.drawCircle(turn_circle_x, top + turn_circle_y, mBoard.getCellWidth() * CELL_SIZE_FACTOR, mPaintCellFgB);
		String s = String.valueOf(mBoard.countCells(E_STATUS.Black));
		canvas.drawText(s, turn_text_x, top + turn_text_y, mPaintTextFg);

		canvas.drawCircle(center + turn_circle_x, top + turn_circle_y, mBoard.getCellWidth() * CELL_SIZE_FACTOR, mPaintCellFgB);
		canvas.drawCircle(center + turn_circle_x, top + turn_circle_y, mBoard.getCellWidth() * CELL_SIZE_FACTOR * 0.94f, mPaintCellFgW);
		s = String.valueOf(mBoard.countCells(E_STATUS.White));
		canvas.drawText(s, center + turn_text_x, top + turn_text_y, mPaintTextFg);

		invalidate(0, (int)mBoard.getRectF().bottom, mWidth, mHeight);
	}
	
	private void drawHints(Cell cell, Canvas canvas, float cw, boolean show_hints){
		if (!show_hints){
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
